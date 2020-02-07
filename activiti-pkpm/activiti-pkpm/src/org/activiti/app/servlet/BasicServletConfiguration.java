package org.activiti.app.servlet;

import org.activiti.app.conf.SwaggerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@ComponentScan(value = {})
@EnableAsync
public class BasicServletConfiguration extends WebMvcConfigurationSupport {

}
