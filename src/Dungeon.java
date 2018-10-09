import room.Direction;
import room.Room;
import room.Tile;

import java.util.ArrayList;
import java.util.Hashtable;

public class Dungeon {

    private int numRooms;
    private int numTemplates;
    private Hashtable<String, boolean[]> fullTiles;
    private Hashtable<String, boolean[]> availableConnections;
    private ArrayList<Room> roomTemplates;
    private ArrayList<Room> rooms;
    private ArrayList<Tile> completedDungeon;

    public Dungeon(int numRooms, ArrayList<Room> roomTemplates) {
        this.numRooms = numRooms;
        this.numTemplates = roomTemplates.size();
        this.rooms = new ArrayList<>();
        this.roomTemplates = roomTemplates;
        this.fullTiles = new Hashtable<>();
        this.availableConnections = new Hashtable<>();
        this.build();
    }

    public ArrayList<Room> rooms() {
        return rooms;
    }

    private void build() {
        int firstRoomNumber = (int)Math.floor(Math.random() * numTemplates);

        //add a starter room
        rooms.add(new Room(roomTemplates.get(firstRoomNumber)));
        addFullTiles(rooms.get(0));
        updateAvailableConnections(rooms.get(0));

        //logic to fit new rooms around existing ones
        for (int roomNumber = 1; roomNumber < numRooms; ++roomNumber) {
            //generate all valid rooms for the current config
            ArrayList<Room> validRooms = new ArrayList<>();

            availableConnections.forEach((coordinate, dirToTile) ->
                roomTemplates.forEach(roomTemplate -> {
                    Hashtable<String, boolean[]> templateConnections = roomTemplate.getConnectingTiles();
                    templateConnections.forEach((templateCoordinate, templateDirToTile) -> {
                        if (dirToTile[Direction.NORTH.index()] && templateDirToTile[Direction.SOUTH.index()]) {
                            Room testRoom = new Room(roomTemplate);
                            String[] split_array = coordinate.split(",");
                            int target_x = Integer.parseInt(split_array[0]);
                            int target_y = Integer.parseInt(split_array[1]);

                            split_array = templateCoordinate.split(",");
                            int template_tile_x = Integer.parseInt(split_array[0]);
                            int template_tile_y = Integer.parseInt(split_array[1]) - 1;

                            int shift_x = target_x - template_tile_x;
                            int shift_y = target_y - template_tile_y;

                            testRoom.shiftRoom(shift_x, shift_y);

                            if (roomFits(testRoom)) {
                                validRooms.add(testRoom);
                            }
                        }
                        if (dirToTile[Direction.SOUTH.index()] && templateDirToTile[Direction.NORTH.index()]) {
                            Room testRoom = new Room(roomTemplate);
                            String[] split_array = coordinate.split(",");
                            int target_x = Integer.parseInt(split_array[0]);
                            int target_y = Integer.parseInt(split_array[1]);

                            split_array = templateCoordinate.split(",");
                            int template_tile_x = Integer.parseInt(split_array[0]);
                            int template_tile_y = Integer.parseInt(split_array[1]) + 1;

                            int shift_x = target_x - template_tile_x;
                            int shift_y = target_y - template_tile_y;

                            testRoom.shiftRoom(shift_x, shift_y);

                            if (roomFits(testRoom)) {
                                validRooms.add(testRoom);
                            }
                        }
                        if (dirToTile[Direction.EAST.index()] && templateDirToTile[Direction.WEST.index()]) {
                            Room testRoom = new Room(roomTemplate);
                            String[] split_array = coordinate.split(",");
                            int target_x = Integer.parseInt(split_array[0]);
                            int target_y = Integer.parseInt(split_array[1]);

                            split_array = templateCoordinate.split(",");
                            int template_tile_x = Integer.parseInt(split_array[0]) - 1;
                            int template_tile_y = Integer.parseInt(split_array[1]);

                            int shift_x = target_x - template_tile_x;
                            int shift_y = target_y - template_tile_y;

                            testRoom.shiftRoom(shift_x, shift_y);

                            if (roomFits(testRoom)) {
                                validRooms.add(testRoom);
                            }
                        }
                        if (dirToTile[Direction.WEST.index()] && templateDirToTile[Direction.EAST.index()]) {
                            Room testRoom = new Room(roomTemplate);
                            String[] split_array = coordinate.split(",");
                            int target_x = Integer.parseInt(split_array[0]);
                            int target_y = Integer.parseInt(split_array[1]);

                            split_array = templateCoordinate.split(",");
                            int template_tile_x = Integer.parseInt(split_array[0]) + 1;
                            int template_tile_y = Integer.parseInt(split_array[1]);

                            int shift_x = target_x - template_tile_x;
                            int shift_y = target_y - template_tile_y;

                            testRoom.shiftRoom(shift_x, shift_y);

                            if (roomFits(testRoom)) {
                                validRooms.add(testRoom);
                            }
                        }
                    });
                })
            );

            if (validRooms.size() > 0) {
                //pick a random room
                Room nextRoom = validRooms.get((int)Math.floor(Math.random() * validRooms.size()));
                rooms.add(nextRoom);
                addFullTiles(nextRoom);
                updateAvailableConnections(nextRoom);
            } else {
                throw new RuntimeException("Unable to find matching room");
            }

        }
    }

    private void addFullTiles(Room room) {
        Hashtable<String, boolean[]> roomTiles = room.getTileHash();
        fullTiles.putAll(roomTiles);
    }

    private void updateAvailableConnections(Room room) {
        Hashtable<String, boolean[]> roomConnections = room.getConnectingTiles();
        room.tiles().forEach(tile -> {
            if (availableConnections.containsKey(tile.toString())) {
                availableConnections.remove(tile.toString());
            }
        });
        roomConnections.forEach((tileCoord, dirsToTile) -> {
            boolean[] validConnections;
            if (availableConnections.containsKey(tileCoord)) {
                validConnections = availableConnections.get(tileCoord);
                for (int index = 0; index < 4; ++index) {
                    if (dirsToTile[index]) {
                        validConnections[index] = true;
                    }
                }
            } else {
                validConnections = dirsToTile;
            }
            availableConnections.put(tileCoord, validConnections);
        });
    }

    private boolean roomFits (Room room) {
        Boolean returnVal = true;
        for (Tile tile : room.tiles()) {
            if (fullTiles.containsKey(tile.toString())) {
                returnVal = false;
            }
        }
        return returnVal;
    }

    public Hashtable<Tile, Room> dungeon() {
        Hashtable <Tile, Room> dungeon = new Hashtable<>();
        for (Room room : rooms) {
            for (Tile tile : room.tiles()) {
                Tile dungeonTile = new Tile(tile);
                if (!fullTiles.containsKey(Integer.toString(tile.x) + "," + Integer.toString(tile.y + 1))) {
                    dungeonTile.walls[Direction.NORTH.index()] = true;
                }
                if (!fullTiles.containsKey(Integer.toString(tile.x) + "," + Integer.toString(tile.y - 1))) {
                    dungeonTile.walls[Direction.SOUTH.index()] = true;
                }
                if (!fullTiles.containsKey(Integer.toString(tile.x + 1) + "," + Integer.toString(tile.y))) {
                    dungeonTile.walls[Direction.EAST.index()] = true;
                }
                if (!fullTiles.containsKey(Integer.toString(tile.x - 1) + "," + Integer.toString(tile.y))) {
                    dungeonTile.walls[Direction.WEST.index()] = true;
                }

                dungeon.put(dungeonTile, room);
            }
        }

        return dungeon;
    }
}
