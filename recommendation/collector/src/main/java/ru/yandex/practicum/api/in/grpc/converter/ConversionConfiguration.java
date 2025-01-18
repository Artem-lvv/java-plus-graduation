 package ru.yandex.practicum.api.in.grpc.converter;

 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.core.convert.ConversionService;
 import org.springframework.core.convert.support.DefaultConversionService;

 @Configuration
public class ConversionConfiguration {

    @Bean
    public ConversionService conversionService() {
        final DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new UserActionProtoToAvroConverter());
        return conversionService;
    }
}
