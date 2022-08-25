package ru.westlarry.userAccount.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.westlarry.userAccount.entity.EmailData;
import ru.westlarry.userAccount.entity.PhoneData;
import ru.westlarry.userAccount.entity.User;
import ru.westlarry.userAccount.exception.CommonApiException;
import ru.westlarry.userAccount.exception.NonUniqueException;
import ru.westlarry.userAccount.exception.UserNotFoundException;
import ru.westlarry.userAccount.request.*;
import ru.westlarry.userAccount.security.UserDetailsImpl;
import ru.westlarry.userAccount.service.UserService;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/users")
@Api(tags = {"User Management"})
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/email")
    @ApiOperation(value = "Создание нового email")
    public ModelApiResponse addNewEmail(@Valid @RequestBody EmailRequest emailRequest) throws NonUniqueException, UserNotFoundException {
        Long id = userService.addNewEmail(getCurrentUserId(), emailRequest.getEmail());
        return new ModelApiResponse(id, "email");
    }

    @PutMapping("/email/{id}")
    @ApiOperation(value = "Изменение email")
    public ModelApiResponse changeEmail(@PathVariable Long id, @Valid @RequestBody EmailRequest emailRequest) throws NonUniqueException, UserNotFoundException, CommonApiException {
        userService.changeEmail(getCurrentUserId(), id, emailRequest.getEmail());
        return new ModelApiResponse(id, "email");
    }

    @DeleteMapping("/email/{id}")
    @ApiOperation(value = "Удаление email")
    public ModelApiResponse deleteEmail(@PathVariable Long id) throws UserNotFoundException, CommonApiException {
        userService.deleteEmail(getCurrentUserId(), id);
        return new ModelApiResponse(id, "email");
    }

    @PostMapping("/phone")
    @ApiOperation(value = "Создание нового номера телефона")
    public ModelApiResponse addNewPhone(@RequestBody PhoneRequest phoneRequest) throws NonUniqueException, UserNotFoundException {
        Long id = userService.addNewPhone(getCurrentUserId(), phoneRequest.getPhone());
        return new ModelApiResponse(id, "phone");
    }

    @PutMapping("/phone/{id}")
    @ApiOperation(value = "Изменение номера телефона")
    public ModelApiResponse changePhone(@PathVariable Long id, @Valid @RequestBody PhoneRequest phoneRequest) throws NonUniqueException, UserNotFoundException, CommonApiException {
        userService.changePhone(getCurrentUserId(), id, phoneRequest.getPhone());
        return new ModelApiResponse(id, "phone");

    }

    @DeleteMapping("/phone/{id}")
    @ApiOperation(value = "Удаление телефона по номеру")
    public ModelApiResponse deletePhone(@PathVariable Long id) throws UserNotFoundException, CommonApiException {
        userService.deletePhone(getCurrentUserId(), id);
        return new ModelApiResponse(id, "phone");
    }

    @GetMapping("/")
    @ApiOperation(value = "Поиск пользователей по фильтр с поддержкой пейджинга")
    public SearchResponse findUsers(@Valid @RequestBody @ApiParam(value = "a) Если передана «dateOfBirth», то фильтр записей, где «date_of_birth» больше чем переданный в запросе.\n" +
            "        b) Если передан «phone», то фильтр по 100% сходству.\n" +
            "        c) Если передан «name», то фильтр по like форматом ‘{text-from-request-param}%’\n" +
            "        d) Если передан «email», то фильтр по 100% сходству.") SearchRequest searchRequest) {

        List<User> arr = userService.findUsers(searchRequest.getName(), searchRequest.getDateOfBirth(),
                searchRequest.getEmail(), searchRequest.getPhone(), searchRequest.getPage(), searchRequest.getSize());
        if (arr.isEmpty()) {
            return new SearchResponse();
        } else {
            SearchResponse searchResponse = new SearchResponse();
            arr.forEach(e -> {
                UserInfo userInfo = new UserInfo();
                userInfo.setName(e.getName());
                userInfo.setDateOfBirth(e.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                userInfo.setEmails(String.join(", ", e.getEmails().stream().map(EmailData::getEmail).collect(Collectors.toSet())));
                userInfo.setPhones(String.join(", ", e.getPhones().stream().map(PhoneData::getPhone).collect(Collectors.toSet())));
                searchResponse.getUsers().add(userInfo);
            });
            return searchResponse;
        }

    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((UserDetailsImpl) authentication.getPrincipal()).getId();
    }
}
