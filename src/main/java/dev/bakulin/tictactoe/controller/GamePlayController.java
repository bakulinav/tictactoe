package dev.bakulin.tictactoe.controller;

import dev.bakulin.tictactoe.dto.GameInitRequest;
import dev.bakulin.tictactoe.dto.MoveRequest;
import dev.bakulin.tictactoe.model.Game;
import dev.bakulin.tictactoe.model.GameSession;
import dev.bakulin.tictactoe.service.GamePlayService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/gameplay")
public class GamePlayController {

    final GamePlayService gamePlayService;

    public GamePlayController(GamePlayService gamePlayService) {
        this.gamePlayService = gamePlayService;
    }

    @PostMapping("/init")
    public Game init(@RequestBody(required = false) GameInitRequest request) {
        return gamePlayService.init(request);
    }


    @PostMapping("/move/{sessionId}")
    public Game move(@PathVariable(name = "sessionId") String sessionId,
                   @Valid @RequestBody MoveRequest moveRequest) {
        log.info("session: {} accept move: {}", sessionId, moveRequest);

        return gamePlayService.acceptMove(sessionId, moveRequest);
    }

    @GetMapping(value = "/state/{sessionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Game currentState(@PathVariable(name = "sessionId") String sessionId) {
        log.info("Request for state of sessionId: {}", sessionId);

        return gamePlayService.getGame(sessionId);
    }

    @GetMapping(value = "/state/{sessionId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String currentStateText(@PathVariable(name = "sessionId") String sessionId) {
        log.info("Request to text state of sessionId: {}", sessionId);

        return gamePlayService.getGameText(sessionId);
    }

    @GetMapping("/list")
    public List<GameSession> list() {
        return gamePlayService.list();
    }
}
