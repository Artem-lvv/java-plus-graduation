//package ru.yandex.practicum;
//
//import feign.okhttp.OkHttpClient;
//import okhttp3.ConnectionPool;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.concurrent.TimeUnit;
//
//@Configuration
//public class FeignOkHttpConfig {
//
//    @Bean
//    public OkHttpClient feignOkHttpClient() {
//        okhttp3.OkHttpClient okHttpClient = new okhttp3.OkHttpClient.Builder()
//                .connectTimeout(10, TimeUnit.SECONDS) // Таймаут соединения
//                .readTimeout(10, TimeUnit.SECONDS)    // Таймаут чтения
//                .writeTimeout(10, TimeUnit.SECONDS)   // Таймаут записи
//                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES)) // Пул соединений
//                .build();
//
//        return new OkHttpClient(okHttpClient);
//    }
//}
