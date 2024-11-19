package dev.bakulin.ticktacktoe.client.model;

import lombok.Data;

@Data
public class MoveRequest {

    public Side moveBy;

    public Integer moveTo;
}