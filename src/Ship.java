import java.util.ArrayList;
import java.util.List;
public class Ship {
    private int size;
    private int health;
    private List<int[]> coordinates;
    public Ship(int size) {
        this.size = size;
        this.health = size;
        this.coordinates = new ArrayList<>();
    }
    public void addCoordinate(int x, int y) {
        coordinates.add(new int[]{x,y});
    }
    public void hit() {
        health--;
    }
    public boolean isSunk() {
        return health <= 0;
    }
    public int getSize() {
        return size;
    }
    public int getHealth() {
        return health;
    }
    public List<int[]> getCoordinates() {
        return coordinates;
    }
}