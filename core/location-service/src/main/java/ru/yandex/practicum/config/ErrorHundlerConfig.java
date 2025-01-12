package ru.yandex.practicum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.exception.ErrorHandler;

@Configuration
public class ErrorHundlerConfig {

    @Bean
    ErrorHandler errorHandler() {
        return new ErrorHandler();
    }
}
