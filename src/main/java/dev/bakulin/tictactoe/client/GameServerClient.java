package dev.bakulin.tictactoe.client;

import dev.bakulin.tictactoe.dto.GameInitRequest;
import dev.bakulin.tictactoe.dto.MoveRequest;
import dev.bakulin.tictactoe.model.Actor;
import dev.bakulin.tictactoe.model.Game;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


public class GameServerClient {

    final RestTemplate rest;
    final String host;
    final Integer port;

    public GameServerClient(RestTemplate rest, String host, Integer port) {
        this.rest = rest;
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public Game initGame() {
        return initGameAs(Actor.CROSS);
    }

    public Game initGameAs(Actor side) {
        return rest.postForObject(urlTo("/gameplay/init"), new GameInitRequest().setSide(side), Game.class);
    }

    public Game makeMove(String session, MoveRequest request) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<MoveRequest> requestHttpEntity = new HttpEntity<>(request, httpHeaders);
        ResponseEntity<Game> responseEntity = rest.postForEntity(
                urlTo("/gameplay/move/" + session),
                requestHttpEntity,
                Game.class);

        return responseEntity.getBody();
    }

    public Game getState(String session) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<MoveRequest> requestHttpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Game> responseEntity = rest.exchange(
                urlTo("/gameplay/state/" + session),
                HttpMethod.GET,
                requestHttpEntity,
                Game.class);

        return responseEntity.getBody();
    }

    public String getField(String session) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.TEXT_PLAIN_VALUE);

        HttpEntity<MoveRequest> requestHttpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> responseEntity = rest.postForEntity(
                urlTo("/gameplay/state/" + session),
                requestHttpEntity,
                String.class);

        return responseEntity.getBody();
    }

    public String getRunningSessions() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<MoveRequest> requestHttpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> responseEntity = rest.exchange(
                urlTo("/gameplay/list"),
                HttpMethod.GET,
                requestHttpEntity,
                String.class);

        return responseEntity.getBody();
    }

    private String urlTo(String path) {
        return "http://%s:%d%s".formatted(host, port, path);
    }
}
