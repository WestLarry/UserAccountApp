package ru.westlarry.userAccount;

import com.jayway.jsonpath.JsonPath;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.westlarry.userAccount.repository.AccountRepository;
import ru.westlarry.userAccount.service.ScheduleService;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = UserAccountApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = UserAccountApplication.class, initializers = {UserAccountApplicationTests.Initializer.class})
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@MockBean(ScheduleService.class)
public class UserAccountApplicationTests {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    public MockMvc mockMvc;

    static {
        // Postgres JDBC driver uses JUL; disable it to avoid annoying, irrelevant, stderr logs during connection testing
        LogManager.getLogManager().getLogger("").setLevel(Level.OFF);
    }

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
            .withDatabaseName("test-db")
            .withUsername("postgres")
            .withPassword("postgres");

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    public void contextLoads() {
        Assertions.assertThat(mockMvc).isNotNull();
        Assertions.assertThat(postgreSQLContainer.isRunning()).isTrue();

        List<Long> userIds = accountRepository.findAllIds();
        assertFalse(userIds.isEmpty());
        assertEquals(2, userIds.size());
    }

    @Test
    public void readUsersOperation() throws Exception {
        String findByBirthDateJsonPage1 = "{\"dateOfBirth\": \"01.01.1999\", \"name\": null, \"email\": null, \"phone\": null, \"page\": 0, \"size\" : 1}";
        String findByBirthDateJsonPage2 = "{\"dateOfBirth\": \"01.01.1999\", \"name\": null, \"email\": null, \"phone\": null, \"page\": 1, \"size\" : 1}";
        String findByBirthDateJsonSize2 = "{\"dateOfBirth\": \"01.01.1999\", \"name\": null, \"email\": null, \"phone\": null, \"page\": 0, \"size\" : 2}";
        String findByBirthDateJsonEmptyResult = "{\"dateOfBirth\": \"01.01.2003\", \"name\": null, \"email\": null, \"phone\": null, \"page\": 0, \"size\" : 2}";
        // запрос от неавторизованного пользователя
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(findByBirthDateJsonPage1))
                .andExpect(status().isUnauthorized());

        //получение токена
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser1@email.com\", \"password\": \"secret\"}"))
                .andExpect(status().isOk()).andReturn();
        String token = JsonPath.read(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.accessToken");

        // Получение данных с первой страницы
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(findByBirthDateJsonPage1)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.users[0].name").value("Иван Иванов"));

        // Получение данных со второй страницы
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(findByBirthDateJsonPage2)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.users[0].name").value("Петр Петров"));

        // получение данных со страницы размером = 2
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(findByBirthDateJsonSize2)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.users[0].name").value("Иван Иванов"))
                .andExpect(jsonPath("$.users[1].name").value("Петр Петров"));

        // получение пустого результата
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(findByBirthDateJsonEmptyResult)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());


        String findByEmailJson = "{\"dateOfBirth\": null, \"name\": null, \"email\": \"testUser2@email.com\", \"phone\": null, \"page\": 0, \"size\" : 1}";
        String findByPhoneJson = "{\"dateOfBirth\": null, \"name\": null, \"email\": null, \"phone\": \"79008200002\", \"page\": 0, \"size\" : 1}";
        String findByNameJson = "{\"dateOfBirth\": null, \"name\": \"Петр\", \"email\": null, \"phone\": null, \"page\": 0, \"size\" : 1}";
        String findByAllParamsJson = "{\"dateOfBirth\": \"01.01.1999\", \"name\": \"Петр\", \"email\":  \"testUser2@email.com\", \"phone\": \"79008200002\", \"page\": 0, \"size\" : 1}";

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(findByEmailJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.users[0].name").value("Петр Петров"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(findByPhoneJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.users[0].name").value("Петр Петров"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(findByNameJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.users[0].name").value("Петр Петров"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users/")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(findByAllParamsJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.users[0].name").value("Петр Петров"));

    }

    @Test
    public void transferOperation() throws Exception {
        String transferJson = "{\"toUserId\": 2, \"amount\": 2.0}";
        String transferBigAmountJson = "{\"toUserId\": 2, \"amount\": 2000.0}";
        String transferWrongUserJson = "{\"toUserId\": 3, \"amount\": 2.0}";
        String transferWrongAmountJson = "{\"toUserId\": 2, \"amount\": -2.0}";
        // запрос от неавторизованного пользователя
        mockMvc.perform(MockMvcRequestBuilders.post("/api/balance/transfer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferJson))
                .andExpect(status().isUnauthorized());

        //получение токена
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"testUser1@email.com\", \"password\": \"secret\"}"))
                .andExpect(status().isOk()).andReturn();
        String token = JsonPath.read(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), "$.accessToken");

        BigDecimal startBalanceUser1 = accountRepository.findById(1L).get().getBalance();
        BigDecimal startBalanceUser2 = accountRepository.findById(2L).get().getBalance();
        mockMvc.perform(MockMvcRequestBuilders.post("/api/balance/transfer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        BigDecimal endBalanceUser1 = accountRepository.findById(1L).get().getBalance();
        BigDecimal endBalanceUser2 = accountRepository.findById(2L).get().getBalance();
        assertEquals(2.0, startBalanceUser1.doubleValue() - endBalanceUser1.doubleValue(), 0.001);
        assertEquals(2.0, endBalanceUser2.doubleValue() - startBalanceUser2.doubleValue(), 0.001);

        // Попытка списать больше, чем есть на счете
        mockMvc.perform(MockMvcRequestBuilders.post("/api/balance/transfer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferBigAmountJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        // Попытка перевести на несуществующий счет
        mockMvc.perform(MockMvcRequestBuilders.post("/api/balance/transfer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferWrongUserJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        // Попытка перевести отрицательную сумму
        mockMvc.perform(MockMvcRequestBuilders.post("/api/balance/transfer")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferWrongAmountJson)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());

    }

}
