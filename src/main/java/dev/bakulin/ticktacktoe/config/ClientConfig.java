package dev.bakulin.ticktacktoe.config;

import dev.bakulin.ticktacktoe.client.GameServerClient;
import dev.bakulin.ticktacktoe.engine.TicTacToe;
import dev.bakulin.ticktacktoe.service.Player;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Configuration
@Profile("player")
public class ClientConfig {

    @Value("${gameserver.host}")
    String host;

    @Value("${gameserver.port}")
    Integer port;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate gameplayTemplate = new RestTemplate();

        return gameplayTemplate;
    }

    @Bean
    public GameServerClient gameplayClient(RestTemplate gameplayTemplate) {
        return new GameServerClient(gameplayTemplate, host, port);
    }

    @Bean
    public Player player(GameServerClient gameServerClient, TicTacToe engine) {
        return new Player(gameServerClient, engine);
    }
}
