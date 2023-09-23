package com.example.testingframework;

import com.example.testingframework.yahoo_finance.stock_v3.config.YamlPropertySourceFactory;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "classpath:application.yml")
@ComponentScan("com.example.testingframework")
public class UiTestConfig {

  /*  @Bean
    public Methods methods() {

        return new Methods();
    }
*/
}
