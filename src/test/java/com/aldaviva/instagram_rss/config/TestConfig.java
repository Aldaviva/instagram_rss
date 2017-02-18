package com.aldaviva.instagram_rss.config;

import static org.mockito.Mockito.mock;

import com.aldaviva.instagram_rss.service.instagram.InstagramService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan("com.aldaviva.instagram_rss.api")
@Import(ApplicationConfig.class)
public class TestConfig {

    @Bean
    public InstagramService instagramService(){
        return mock(InstagramService.class);
    }

}
