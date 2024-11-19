package dev.bakulin.ticktacktoe.client.model;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class GameSession {
    String id;
    ZonedDateTime createdAt;
}
