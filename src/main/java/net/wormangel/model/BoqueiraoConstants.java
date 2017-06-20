package net.wormangel.model;

public class BoqueiraoConstants {
    public static final double DEAD_VOLUME_THRESHOLD = 8.22f;

    public static final double MAX_VOLUME = new Double("411686287");

    public static final double RATIONING_VOLUME_THRESHOLD =
            (DEAD_VOLUME_THRESHOLD / 100f) * MAX_VOLUME;
}
