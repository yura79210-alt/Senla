public class Move {
    private int x;
    private int y;
    private boolean hit;
    public Move(int x, int y, boolean hit) {
        this.x = x;
        this.y = y;
        this.hit = hit;
    }
    @Override
    public String toString() {
        return x + "," + y + "," + hit;
    }
}