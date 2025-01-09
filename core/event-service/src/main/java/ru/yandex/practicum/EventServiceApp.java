package ru.yandex.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.yandex.practicum.stats.api.StatsServiceApiClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {AdminUserClient.class,
        PublicCategoryClient.class,
        PrivateUserRequestClient.class,
        StatsServiceApiClient.class})
public class EventServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EventServiceApp.class);
    }
}
