package room;

public enum Direction {
    NORTH (0),
    SOUTH (1),
    EAST  (2),
    WEST  (3);

    private final int index;

    Direction(int index) {
        this.index = index;
    }

    public int index() {
        return index;
    }
}
