package dev.bakulin.tictactoe.model;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class GameSession {
    final String id;
    final ZonedDateTime createdAt;
}
