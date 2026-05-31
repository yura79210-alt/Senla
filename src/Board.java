import java.util.Random;

public class Board {
    private static final int SIZE = 16;
    private char[][] grid = new char[SIZE][SIZE];  // отображение на экране
    private Ship[][] ships = new Ship[SIZE][SIZE]; // объекты кораблей
    // Конструктор
    public Board() {
        for(int i=0;i<SIZE;i++) {
            for(int j=0;j<SIZE;j++) {
                grid[i][j] = '.';
            }
        }
    }
    private boolean canPlaceShip(
            int x,
            int y,
            int size,
            boolean horizontal) {
        for(int i=0;i<size;i++) {
            int currentX = horizontal ? x : x+i;
            int currentY = horizontal ? y+i : y;
            for(int dx=-1; dx<=1; dx++){
                for(int dy=-1; dy<=1; dy++){
                    int nx = currentX+dx;
                    int ny = currentY+dy;
                    if(nx>=0 && nx<SIZE && ny>=0 && ny<SIZE){
                        if(ships[nx][ny] !=null){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    public void printPrettyBoard(boolean hideShips) {
        System.out.print("  ");
        for (char c = 'A'; c < 'A' + SIZE; c++) {
            System.out.print(c + " ");
        }
        System.out.println();
        for(int i=0;i<SIZE;i++) {
            System.out.print((i + 1) + " ");
            for(int j=0;j<SIZE;j++) {
                char cell = grid[i][j];
                if (hideShips && cell == 'S') {
                    System.out.print(". ");
                } else {
                    System.out.print(cell + " ");
                }
            }
            System.out.println();
        }
    }
    public void printHiddenBoard() {
        for(int i=0;i<SIZE;i++) {
             for(int j=0;j<SIZE;j++) {
                if (grid[i][j] == 'S') {
                    System.out.print(". "); // скрываем корабль
                } else {
                    System.out.print(grid[i][j] + " ");
                }
            }
            System.out.println();
        }
    }
    // Печать поля
    public void printBoard() {
        for(int i=0;i<SIZE;i++) {
             for(int j=0;j<SIZE;j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
    public boolean placeShip(int x, int y, int size, boolean horizontal) {
        if (!canPlaceShip(x, y, size, horizontal)) {
            return false;
        }
        Ship ship = new Ship(size);
        for(int i=0;i<size;i++) {
            int currentX;
            int currentY;
            if(horizontal){currentX = x;currentY = y+i;
            } else{
                currentX = x+i;currentY = y;}
            grid[currentX][currentY]='S';
            ships[currentX][currentY]=ship;
            ship.addCoordinate(currentX, currentY);
        }
        return true;
    }
    // Новый метод: случайное размещение корабля
    public void placeShipRandomly(int size) {
        Random rand = new Random();
        boolean placed = false;
        while (!placed) {
            int x = rand.nextInt(SIZE);
            int y = rand.nextInt(SIZE);
            boolean horizontal = rand.nextBoolean();
            if (horizontal) {
                if (y + size > SIZE) continue;
                if(!canPlaceShip(x, y, size, true)){
                    continue;
                }
                Ship ship = new Ship(size);
                for(int i=0;i<size;i++){
                    grid[x][y+i]='S';
                    ships[x][y+i]=ship;
                    ship.addCoordinate(x, y+i);
                }
                placed=true;
            } else {
                if (x + size > SIZE) continue;
                if(!canPlaceShip(x, y, size, false)){
                    continue;
                }
                Ship ship = new Ship(size);
                for(int i=0;i<size;i++){
                    grid[x+i][y]='S';
                    ships[x+i][y]=ship;
                    ship.addCoordinate(x+i, y);
                }
                placed=true;
            }
        }
    }
    // Выстрел
    public int shoot(int x, int y) {
            if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
            System.out.println(" Неверные координаты!");
            return -1;
        }
        if (grid[x][y] == 'X' || grid[x][y] == 'O' || grid[x][y] == '+') {
            System.out.println(" Ты уже стрелял сюда!");
            return -1;
        }
        if (ships[x][y] != null) {
            grid[x][y] = 'X';
            Ship ship = ships[x][y];
            ship.hit();
            if (ship.isSunk()) {
                System.out.println("Корабль уничтожен!");
                for(int[] coord : ship.getCoordinates()){
                    int cx = coord[0];
                    int cy = coord[1];
                    grid[cx][cy]='+';
                }
                for(int[] coord : ship.getCoordinates()){
                    int cx = coord[0];
                    int cy = coord[1];
                    for(int dx=-1; dx<=1; dx++) {
                        for(int dy=-1; dy<=1; dy++){
                            int nx= cx+dx;
                            int ny= cy+dy;
                            if(nx>=0 && nx<SIZE && ny>=0 && ny<SIZE){
                                if(grid[nx][ny] == '.'){
                                    grid[nx][ny] ='O';
                                }
                            }
                        }
                    }
                }
            } else {
                grid[x][y] = 'X';
            }
            return ship.isSunk() ? 2 : 1;
        } else {
            grid[x][y] = 'O';
            return 0;
        }
    }
    public boolean allShipsSunk() {
        for(int i=0;i<SIZE;i++) {
            for(int j=0;j<SIZE;j++) {
                if (ships[i][j] != null && !ships[i][j].isSunk()) {
                    return false;
                }
            }
        }
        return true;
    }
    public String[] getBoardLines(boolean hideShips){
        String[] lines = new String[SIZE + 1];
        StringBuilder header = new StringBuilder("   ");
        for(char c='A'; c<'A'+SIZE; c++){
            header.append(c).append(" ");
        }
        lines[0]=header.toString();
        for(int i=0; i<SIZE; i++){
            StringBuilder row = new StringBuilder();
            row.append(String.format("%2d ", i+1));
            for(int j=0; j<SIZE; j++){
                char cell = grid[i][j];
                if(hideShips && cell=='S'){
                    row.append(". ");
                } else{
                    row.append(cell).append(" ");
                }
            }
            lines[i+1]= row.toString();
        }
        return lines;
    }
}