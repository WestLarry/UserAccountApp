package ru.westlarry.userAccount.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("ru.westlarry.userAccount.controller")) // Пакет сканирования Swagger
                .paths(PathSelectors.any()).build();
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
                .description("Простой и элегантный стиль Restful").termsOfServiceUrl("")
                // номер версии
                .version("0.0.1").build();
    }
}
