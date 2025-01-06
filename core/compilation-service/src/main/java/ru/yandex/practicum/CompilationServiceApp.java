package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
//import ru.yandex.practicum.stats.api.StatsServiceApiClient;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableFeignClients(clients = {StatsServiceApiClient.class})
public class CompilationServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CompilationServiceApp.class);
    }
}
