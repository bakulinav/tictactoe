package dev.bakulin.ticktacktoe.model;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class GameSession {
    final String id;
    final ZonedDateTime createdAt;
}
