import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import room.Room;
import room.Direction;
import room.Tile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

public class DunGen extends Frame{

    private final int GRID_SIZE = 10;
    private final int W_WIDTH = 600;
    private final int W_HEIGHT = 500;
    private final int NUM_ROOMS = 100;

    private Dungeon dungeon;

    public ArrayList<Room> roomTemplates = new ArrayList<>();

    public static void main(String[] args) {
        new DunGen();
    }

    public DunGen() {
        super("Dungeon Generator");

        File roomFile = new File("data/hallways");
//        File roomFile = new File("data/generic_rooms");
        buildRoomTemplates(roomFile);

        dungeon = new Dungeon(NUM_ROOMS, roomTemplates);

        setSize(W_WIDTH, W_HEIGHT);
        setVisible(true);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
            dispose();
            System.exit(0);
            }
        });

    }

    public void paint(Graphics g0) {
        Graphics2D g = (Graphics2D)g0;

        //draw grid lines
        int origin_x = W_WIDTH / 2;
        int origin_y = W_HEIGHT / 2;

        g.setColor(Color.lightGray);
        int x = origin_x + GRID_SIZE;
        int nx = origin_x - GRID_SIZE;
        while (x < W_WIDTH) {
            g.draw(new Line2D.Double(x, 0, x, W_HEIGHT));
            g.draw(new Line2D.Double(nx, 0, nx, W_HEIGHT));

            x += GRID_SIZE;
            nx -= GRID_SIZE;
        }

        int y = origin_y + GRID_SIZE;
        int ny = origin_y - GRID_SIZE;
        while (y < W_HEIGHT) {
            g.draw(new Line2D.Double(0, y, W_WIDTH, y));
            g.draw(new Line2D.Double(0, ny, W_WIDTH, ny));

            y += GRID_SIZE;
            ny -= GRID_SIZE;
        }

        g.setColor(Color.blue);
        g.draw(new Line2D.Double(origin_x, 0, origin_x, W_HEIGHT));

        g.setColor(Color.red);
        g.draw(new Line2D.Double(0, origin_y, W_WIDTH, origin_y));

        Hashtable<Tile, Room> dungeonTiles = dungeon.dungeon();
        dungeonTiles.forEach((tile, room) -> {
            int tile_origin_x = origin_x + (tile.x * GRID_SIZE);
            int tile_origin_y = origin_y - (tile.y * GRID_SIZE);

            //fill tile
//            g.setColor(room.getRoomColor());
            g.setColor(Color.gray);
            g.fill(new Rectangle2D.Double(tile_origin_x, tile_origin_y - GRID_SIZE, GRID_SIZE, GRID_SIZE));
        });

        dungeonTiles.forEach((tile, room) -> {
            int tile_origin_x = origin_x + (tile.x * GRID_SIZE);
            int tile_origin_y = origin_y - (tile.y * GRID_SIZE);

            //draw walls
            g.setColor(Color.black);
            g.setStroke(new BasicStroke(2));

            if (tile.walls[Direction.NORTH.index()]) {
                g.draw(new Line2D.Double(tile_origin_x, tile_origin_y - GRID_SIZE, tile_origin_x + GRID_SIZE, tile_origin_y - GRID_SIZE));
            }

            if (tile.walls[Direction.SOUTH.index()]) {
                g.draw(new Line2D.Double(tile_origin_x, tile_origin_y, tile_origin_x + GRID_SIZE, tile_origin_y));
            }

            if (tile.walls[Direction.EAST.index()]) {
                g.draw(new Line2D.Double(tile_origin_x + GRID_SIZE, tile_origin_y, tile_origin_x + GRID_SIZE, tile_origin_y - GRID_SIZE));
            }

            if (tile.walls[Direction.WEST.index()]) {
                g.draw(new Line2D.Double(tile_origin_x, tile_origin_y, tile_origin_x, tile_origin_y - GRID_SIZE));
            }

        });


    }

    private void buildRoomTemplates(File roomFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(roomFile);

            NodeList rooms = doc.getElementsByTagName("room");
            for (int roomIndex = 0; roomIndex < rooms.getLength(); ++roomIndex) {
                Node room = rooms.item(roomIndex);
                Room r = new Room();

                NodeList tiles = room.getChildNodes();
                for (int tileIndex = 0; tileIndex < tiles.getLength(); ++tileIndex) {
                    Node tNode = tiles.item(tileIndex);
                    if (tNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element tile = (Element)tNode;
                        int x = Integer.parseInt(tile.getElementsByTagName("x").item(0).getTextContent());
                        int y = Integer.parseInt(tile.getElementsByTagName("y").item(0).getTextContent());

                        NodeList walls = tile.getElementsByTagName("wall");
                        boolean[] w = new boolean[4];
                        for (int wallIndex = 0; wallIndex < walls.getLength(); ++wallIndex) {
                            String wallString = walls.item(wallIndex).getTextContent();
                            switch (wallString) {
                                case "NORTH":
                                    w[Direction.NORTH.index()] = true;
                                    break;
                                case "SOUTH":
                                    w[Direction.SOUTH.index()] = true;
                                    break;
                                case "EAST":
                                    w[Direction.EAST.index()] = true;
                                    break;
                                case "WEST":
                                    w[Direction.WEST.index()] = true;
                                    break;
                            }
                        }

                        r.addTile(x, y, w);
                    }
                }

                roomTemplates.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
