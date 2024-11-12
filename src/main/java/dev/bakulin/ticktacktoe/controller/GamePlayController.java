package dev.bakulin.ticktacktoe.controller;

import dev.bakulin.ticktacktoe.dto.MoveRequest;
import dev.bakulin.ticktacktoe.dto.MoveState;
import dev.bakulin.ticktacktoe.model.Game;
import dev.bakulin.ticktacktoe.model.GameSession;
import dev.bakulin.ticktacktoe.service.GamePlayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public Game init() {
        return gamePlayService.init();
    }


    @PostMapping("/move")
    public MoveState move(@RequestHeader("X-SESSION-ID") String sessionId,
                   MoveRequest moveRequest) { // TODO add validate
        log.info("session: {} accept move: {}", sessionId, moveRequest);

        return gamePlayService.acceptMove(sessionId, moveRequest);
    }

    @GetMapping("/state")
    public Game currentState(@RequestHeader("X-SESSION-ID") String sessionId) {
        log.info("Request to get state for sessionId: {}", sessionId);

        return gamePlayService.getGame(sessionId);
    }

    @GetMapping("/list")
    public List<GameSession> list() {
        return gamePlayService.list();
    }
}
