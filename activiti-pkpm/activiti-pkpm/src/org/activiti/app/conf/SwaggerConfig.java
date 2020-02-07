package org.activiti.app.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import io.swagger.annotations.Api;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableWebMvc
@EnableSwagger2
@PropertySource("classpath:activiti-app.properties")
//@ComponentScan(basePackages = {"org.activiti.rest.service.api"})
public class SwaggerConfig {
	@Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //.apis(RequestHandlerSelectors.withClassAnnotation(Api.class))//这是注意的代码
                .apis(RequestHandlerSelectors.basePackage("org.activiti.rest.service.api.repository"))
                .paths(PathSelectors.any())
                .build().pathMapping("/app")
                ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("activiti-pkpm REST API")
                .description("activiti-pkpm工作流文档")
                .termsOfServiceUrl("http://www.xxx.com")
                .version("1.0")
                .build();
    }
}
