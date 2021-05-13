package com.samarthanam.digitallibrary.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfiguration {

    @Bean
    protected Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.samarthanam.digitallibrary"))
                .build()
                .useDefaultResponseMessages(false);
    }

    private static ApiInfo apiInfo() {

        return new ApiInfoBuilder()
                .title("Digital Library Services")
                .version("v1")
                .build();
    }

}
