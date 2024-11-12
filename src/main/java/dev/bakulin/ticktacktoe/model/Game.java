package dev.bakulin.ticktacktoe.model;

import dev.bakulin.ticktacktoe.dto.GameState;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Game {
    final String session;
    final GameState state;
    String opponent;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;

    public static Game init(String session, GameState state) {
        return new Game(session, state)
                .setCreatedAt(ZonedDateTime.now())
                .setUpdatedAt(ZonedDateTime.now())
                ;
    }
}
