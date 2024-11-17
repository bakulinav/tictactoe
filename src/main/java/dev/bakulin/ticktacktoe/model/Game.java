package dev.bakulin.ticktacktoe.model;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class Game {
    final String session;
    final GameState state;
    final Sides sides;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;

    public static Game init(String session, GameState state, Sides sides) {
        return new Game(session, state, sides)
                .setCreatedAt(ZonedDateTime.now())
                .setUpdatedAt(ZonedDateTime.now())
                ;
    }
}
