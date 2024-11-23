package dev.bakulin.tictactoe.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import dev.bakulin.tictactoe.model.Actor;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoveRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            with = JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
    @NotNull(message = "required CROSS or ZERO")
    public Actor moveBy;

    @Min(value = 1, message = "required in interval 1..9")
    @Max(value = 9, message = "required in interval 1..9")
    @NotNull(message = "required")
    public Integer moveTo;
}
