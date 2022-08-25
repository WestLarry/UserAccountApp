package ru.westlarry.userAccount.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("ru.westlarry.userAccount.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .tags(
                        new Tag("Authentication", "Authentication method", 0),
                        new Tag("User Management", "User management methods", 1),
                        new Tag("Operations", "Some operations", 2));
    }

    /**
     * Создайте основную информацию API (основная информация будет отображаться на
     * странице документа) Адрес для посещения: http: // фактический адрес проекта /
     * swagger-ui.html
     *
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // Заголовок страницы
                .title("«Swagger2 RESTful API»")
                // основатель
                .contact(new Contact("", "", ""))
                // Описание
                .description("REST API сервис").termsOfServiceUrl("")
                // номер версии
                .version("0.0.1").build();
    }
}
