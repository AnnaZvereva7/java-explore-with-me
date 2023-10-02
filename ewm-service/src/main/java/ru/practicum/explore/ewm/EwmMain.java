package ru.practicum.explore.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.practicum.explore.stats.client.StatsClient;
import ru.practicum.explore.stats.dto.StatsMapper;

@SpringBootApplication
@EnableTransactionManagement
public class EwmMain {
    public static void main(String[] args) {
        SpringApplication.run(EwmMain.class, args);
    }

    @Bean
    public StatsClient getStatsClientBean(@Value("${stats.server.url}") String baseUrl) {
        return new StatsClient(baseUrl);
    }

    @Bean
    public StatsMapper getStatsMapperBean() {
        return new StatsMapper();
    }
}
