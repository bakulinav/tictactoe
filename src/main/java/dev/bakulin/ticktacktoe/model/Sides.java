package dev.bakulin.ticktacktoe.model;

import lombok.Value;

@Value
public class Sides {
    Actor host;
    Actor guest;

    public static Sides initByGuest(Actor guestSide) {
        return new Sides(
                Actor.CROSS.equals(guestSide) ? Actor.ZERO : Actor.CROSS,
                guestSide);
    }
}
