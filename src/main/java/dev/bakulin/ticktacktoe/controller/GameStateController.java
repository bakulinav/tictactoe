package dev.bakulin.ticktacktoe.controller;

import dev.bakulin.ticktacktoe.dto.GameState;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/state")
public class GameStateController {

    @GetMapping
    public GameState currentState() {
        return new GameState()
                .setStatus("playing")

                .setField("XX3|4O6|O89|")
                .setCrossesMoves("12000")
                .setZeroesMoves("57000")
                .setLastGameWinner(null)
                .setWinCombo(null)
                ;
    }
}
