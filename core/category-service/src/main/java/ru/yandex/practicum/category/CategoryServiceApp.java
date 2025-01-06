package ru.yandex.practicum.category;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
//import ru.yandex.practicum.stats.api.StatsServiceApiClient;

@SpringBootApplication
@EnableDiscoveryClient
//@EnableFeignClients(clients = {StatsServiceApiClient.class})
public class CategoryServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CategoryServiceApp.class);
    }
}
