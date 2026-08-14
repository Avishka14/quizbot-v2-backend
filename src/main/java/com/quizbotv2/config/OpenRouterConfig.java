package com.quizbotv2.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;


@Configuration
public class OpenRouterConfig {

    @Value("${openrouter.api.key}")
    private String apiKey;

    @Bean
    public WebClient openRouterWebClient(){

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) //maximum time to connection to OpenRouter
                .responseTimeout(Duration.ofSeconds(30)) // waiting time for response
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(30)) //maximum time to wait while reading data
                        .addHandlerLast(new WriteTimeoutHandler(30))); //maximum time to sending data to openrouter

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl("https://openrouter.ai/api/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("HTTP-Referer", "http://localhost:8080")
                .defaultHeader("X-Title", "quizbot-v2")
                .build();

    }

}
