package io.bdx.microservices;

import io.bdx.microservices.config.ZookeeperConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by Olivier on 27/09/2015.
 */
@EnableAutoConfiguration
@ComponentScan("io.bdx.microservices")
public class Application {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
