package com.eleks.academy.whoami;

import com.eleks.academy.whoami.api.GameApi;
import com.eleks.academy.whoami.handler.ApiClient;
//import com.eleks.academy.whoami.api.UserController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan(basePackages = {"com.eleks.academy.whoami"}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = ApiClient.class),
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = GameApi.class)
})
public class WhoAmIApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhoAmIApplication.class, args);
    }

}
