package dev.bakulin.ticktacktoe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActorMove {
    String sessionId; // uuid
    String actor; //cross, zero
    int moveTo;
}
