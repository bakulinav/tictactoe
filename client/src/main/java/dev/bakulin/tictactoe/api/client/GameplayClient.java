package dev.bakulin.tictactoe.api.client;

import org.springframework.web.client.RestClient;

public class GameplayClient {
    final RestClient client;

    public GameplayClient(RestClient client) {
        this.client = client;
    }

    String initSession() {
        client.post();
    }
}
