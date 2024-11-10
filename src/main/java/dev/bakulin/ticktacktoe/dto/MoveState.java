package dev.bakulin.ticktacktoe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoveState {
    boolean accepted;

    String reason; // for accepted = false
}
