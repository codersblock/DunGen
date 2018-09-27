package room;

import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;

//Encompases the idea of a single room in a dungeon.
public class Room {
    private ArrayList<Tile> tiles;

    // key will be in the form "x,y".  value will represent directions of walls in the room.
    private Hashtable<String, boolean[]> tileHash;

    // key will be an "x,y" string that represents a tile that can connect to this room.  value will represent the direction(s) in which that tile can connect to the room.
    private Hashtable<String, boolean[]> availableConnections;

    private Color roomColor;

    public Room() {
        tiles = new ArrayList<>();
        roomColor = Color.getHSBColor((float)Math.random(), (float)Math.random(), 0.5f);
    }

    public Room(Room room) {
        tiles = new ArrayList();
        this.roomColor = room.roomColor;

        room.tiles.forEach(tile -> this.tiles.add(new Tile(tile)));

        if (room.tileHash != null) {
            this.tileHash = new Hashtable<>();
            room.tileHash.forEach((coord, walls) -> this.tileHash.put(coord, walls));
        }

        if (room.availableConnections != null) {
            this.availableConnections = new Hashtable<>();
            room.availableConnections.forEach((coord, walls) -> this.availableConnections.put(coord, walls));
        }
    }

    public Color getRoomColor() {
        return roomColor;
    }

    public void addTile(int x, int y, boolean[] walls) {
        tiles.add(new Tile(x, y, walls));
        getTileHash(true);
        getConnectingTiles(true);
    }

    public void shiftRoom(int x, int y) {
        tiles.forEach(tile -> {
            tile.x += x;
            tile.y += y;
        });
        getTileHash(true);
        getConnectingTiles(true);
    }

    public ArrayList<Tile> tiles() {
        return tiles;
    }

    public Hashtable<String, boolean[]> getConnectingTiles() {
        return getConnectingTiles(false);
    }

    private Hashtable<String, boolean[]> getConnectingTiles(boolean reCalculate) {
        if (availableConnections != null && !reCalculate) {
            return availableConnections;
        }

        availableConnections = new Hashtable<>();

        tiles.forEach(tile -> {
            if (!tile.walls[Direction.EAST.index()]) {
                String key = Integer.toString(tile.x + 1) + "," + Integer.toString(tile.y);
                if (availableConnections.containsKey(key)) {
                    boolean[] vals = availableConnections.get(key);
                    vals[Direction.WEST.index()] = true;
                    availableConnections.put(key, vals);
                } else {
                    boolean[] vals = new boolean[4];
                    vals[Direction.WEST.index()] = true;
                    availableConnections.put(key, vals);
                }
            }

            if (!tile.walls[Direction.WEST.index()]) {
                String key = Integer.toString(tile.x - 1) + "," + Integer.toString(tile.y);
                if (availableConnections.containsKey(key)) {
                    boolean[] vals = availableConnections.get(key);
                    vals[Direction.EAST.index()] = true;
                    availableConnections.put(key, vals);
                } else {
                    boolean[] vals = new boolean[4];
                    vals[Direction.EAST.index()] = true;
                    availableConnections.put(key, vals);
                }
            }

            if (!tile.walls[Direction.NORTH.index()]) {
                String key = Integer.toString(tile.x) + "," + Integer.toString(tile.y + 1);
                if (availableConnections.containsKey(key)) {
                    boolean[] vals = availableConnections.get(key);
                    vals[Direction.SOUTH.index()] = true;
                    availableConnections.put(key, vals);
                } else {
                    boolean[] vals = new boolean[4];
                    vals[Direction.SOUTH.index()] = true;
                    availableConnections.put(key, vals);
                }
            }

            if (!tile.walls[Direction.SOUTH.index()]) {
                String key = Integer.toString(tile.x) + "," + Integer.toString(tile.y - 1);
                if (availableConnections.containsKey(key)) {
                    boolean[] vals = availableConnections.get(key);
                    vals[Direction.NORTH.index()] = true;
                    availableConnections.put(key, vals);
                } else {
                    boolean[] vals = new boolean[4];
                    vals[Direction.NORTH.index()] = true;
                    availableConnections.put(key, vals);
                }
            }
        });

        tiles.forEach(tile -> availableConnections.remove(tile.toString()));

        return availableConnections;
    }

    public Hashtable<String, boolean[]> getTileHash() {
        return getTileHash(false);
    }

    private Hashtable<String, boolean[]> getTileHash(boolean reCalculate) {
        if (tileHash != null && !reCalculate) {
            return tileHash;
        }

        tileHash = new Hashtable<>();
        tiles.forEach(tile -> tileHash.put(tile.toString(), tile.walls));

        return tileHash;
    }


}
