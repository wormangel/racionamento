package com.github.wormangel.racionamento.model;

public class BoqueiraoConstants {
    public static final double DEAD_VOLUME_THRESHOLD = 8.22d;

    public static final double MAX_VOLUME = new Double(411686287d);

    public static final double RATIONING_VOLUME_THRESHOLD =
            (DEAD_VOLUME_THRESHOLD / 100d) * MAX_VOLUME;
}
