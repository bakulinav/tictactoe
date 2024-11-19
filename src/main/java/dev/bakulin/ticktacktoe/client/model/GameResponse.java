package dev.bakulin.ticktacktoe.client.model;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class GameResponse {
        final String session;
        final GameState state;
        final Sides sides;
        ZonedDateTime createdAt;
        ZonedDateTime updatedAt;
}
