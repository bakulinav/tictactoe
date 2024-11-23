package dev.bakulin.tictactoe.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorInfo {
    String error;
    String message;
}
