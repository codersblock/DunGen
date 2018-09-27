package room;

public class Tile {
    public int x;
    public int y;
    public boolean[] walls;

    public Tile(int x, int y, boolean[] walls) {
        this.x = x;
        this.y = y;
        this.walls = walls;
    }

    public Tile(Tile tile) {
        this.x = tile.x;
        this.y = tile.y;
        this.walls = new boolean[4];
        for (int index = 0; index < 4; ++index) {
            this.walls[index] = tile.walls[index];
        }
    }

    public String toString() {
        return Integer.toString(x) + "," + Integer.toString(y);
    }
}
