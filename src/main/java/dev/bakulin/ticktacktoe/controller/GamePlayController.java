package dev.bakulin.ticktacktoe.controller;

import dev.bakulin.ticktacktoe.dto.ActorMove;
import dev.bakulin.ticktacktoe.dto.MoveState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/gameplay")
public class GamePlayController {
    @PostMapping("/init")
    String init() {
        return UUID.randomUUID().toString(); // init new game session
    }

    @PostMapping("/move")
    MoveState move(ActorMove actorMove) {
        log.info("accept move: " + actorMove);
        return new MoveState().setAccepted(true);
    }
}
