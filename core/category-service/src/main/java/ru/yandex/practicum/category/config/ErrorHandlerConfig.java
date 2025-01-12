package ru.yandex.practicum.category.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.exception.ErrorHandler;

@Configuration
public class ErrorHandlerConfig {

    @Bean
    ErrorHandler errorHandler() {
        return new ErrorHandler();
    }
}
