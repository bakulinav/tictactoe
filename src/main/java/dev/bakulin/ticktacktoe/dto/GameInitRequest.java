package dev.bakulin.ticktacktoe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.bakulin.ticktacktoe.model.Actor;
import lombok.Data;

@Data
public class GameInitRequest {
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            with = JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
    Actor side;
}
