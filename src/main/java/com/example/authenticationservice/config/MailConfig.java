package com.example.authenticationservice.config;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerViewResolver;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername("my.gmail@gmail.com");
        mailSender.setPassword("password");
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        
        return mailSender;
    }

    @Bean 
    public FreeMarkerViewResolver freemarkerViewResolver() { 
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver(); 
        resolver.setPrefix(""); 
        resolver.setSuffix(".ftl"); 
        return resolver; 
    }

    @Bean 
    public FreeMarkerConfigurer freemarkerConfig() { 
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer(); 
        freeMarkerConfigurer.setTemplateLoaderPath("authentication-service/src/main/resources/templates");
        return freeMarkerConfigurer; 
    }
    
}
