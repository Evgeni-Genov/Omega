package com.example.omega.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static com.example.omega.service.util.Constants.*;

@EnableWebMvc
@EnableScheduling
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${spring.cors.allowed-origins}")
    private String cors;

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new StringHttpMessageConverter());
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(createJsonHttpMessageConverter());
    }

    private HttpMessageConverter<Object> createJsonHttpMessageConverter() {
        var jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setPrettyPrint(true);
        return jsonConverter;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new NormalizeStringResolver());
    }

    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(AUTH).allowedOrigins(cors).allowedMethods(ALLOWED_METHODS);
        registry.addMapping(USER).allowedOrigins(cors).allowedMethods(ALLOWED_METHODS);
        registry.addMapping(GOOGLE_AUTHENTICATOR).allowedOrigins(cors).allowedMethods(ALLOWED_METHODS);
        registry.addMapping(ACCOUNT_BALANCE).allowedOrigins(cors).allowedMethods(ALLOWED_METHODS);
        registry.addMapping(TRANSACTION).allowedOrigins(cors).allowedMethods(ALLOWED_METHODS);
        registry.addMapping("/api/**").allowedOrigins(cors).allowedMethods(ALLOWED_METHODS);
    }
}
