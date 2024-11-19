package dev.bakulin.ticktacktoe.client.model;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

import java.util.List;

@Data
public class ListSessionsResponse {
    @JsonValue
    List<GameSession> gameSessions;
}
