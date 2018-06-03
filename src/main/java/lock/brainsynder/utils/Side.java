package lock.brainsynder.utils;

public enum Side {
    LEFT, RIGHT;

    public static Side opposite (Side side) {
        if (side == RIGHT) return LEFT;
        return RIGHT;
    }
}