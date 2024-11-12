package dev.bakulin.ticktacktoe.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.bakulin.ticktacktoe.model.Actor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoveRequest {
    @JsonProperty
    Actor actor; //cross, zero
    int moveTo;
}
