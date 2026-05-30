import java.util.Scanner;
public class Main {
    public static void main(String[] args) {
        Board board1 = new Board();
        Board board2 = new Board();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите имя игрока 1: ");
        String player1Name = scanner.nextLine();
        if (player1Name.isEmpty()) {
            player1Name = "Игрок 1";
        }
        System.out.print("Введите имя игрока 2: ");
        String player2Name = scanner.nextLine();
        if (player2Name.isEmpty()) {
            player2Name = "Игрок 2";
        }
        if (player1Name.equalsIgnoreCase("admin") || player2Name.equalsIgnoreCase("admin")) {
            System.out.println("ADMIN MODE");
            MoveLogger.showAllGames(); // позже сделаем
            return; // выходим из игры
        }
        MoveLogger.logGameStart(player1Name, player2Name);
        System.out.println("1 - Авторасстановка");
        System.out.println("2 - Ручная расстановка");
        int setupMode = scanner.nextInt();
        scanner.nextLine();
        //  расстановка кораблей первого игрока и второго игрока
        int[][] fleet = {{6,1}, {5,2}, {4,3}, {3,4}, {2,5}, {1,6}};
        if(setupMode==1){
            for (int[] shipInfo : fleet) {
                int size = shipInfo[0];
                int count = shipInfo[1];
                for(int i=0;i<count;i++) {
                    board1.placeShipRandomly(size);
                    board2.placeShipRandomly(size);
                }
            }
        } else{
            setupPlayerShips(scanner, board1, player1Name, fleet);
            setupPlayerShips(scanner, board2, player2Name, fleet);
        }
        boolean player1Turn = true;
        System.out.println ("Игра началась! Введи координаты выстрела X и Y (0-9)");
        String userInput;
        int[] coords;
        int x, y;
        while (true) {
            Board currentBoard;
            String playerName;
            if (player1Turn) {
                currentBoard = board2; // игрок 1 стреляет по игроку 2
                playerName = player1Name;
            } else {
                currentBoard = board1;
                playerName = player2Name;
            }
            System.out.println("\nТВОЁ ПОЛЕ:");
            if (player1Turn) {
                board1.printPrettyBoard(false);
            } else {
                board2.printPrettyBoard(false);
            }
            System.out.println("\nПОЛЕ ПРОТИВНИКА:");
            currentBoard.printPrettyBoard(true);

            System.out.println("\n" + playerName + ", твой ход!");
            try {
                System.out.print("Введи координаты (например A1): ");
                userInput = scanner.next();
                coords = InputParser.parse(userInput);
                x = coords[0];
                y = coords[1];
                int result = currentBoard.shoot(x, y);
                if (result == -1) continue;
                String moveText = userInput.toUpperCase();
                MoveLogger.logMove(playerName, moveText, result == 1);
                System.out.println(result == 1 ? "ПОПАЛ!" : "МИМО");
                currentBoard.printPrettyBoard(true);
                if (currentBoard.allShipsSunk()) {
                    System.out.println(" Победил " + playerName);
                    MoveLogger.logGameEnd(playerName);
                    break;
                }
                if (result == 1) {
                    System.out.println(" Попадание! Ходишь ещё раз.");
                } else if (result == 0) {
                    System.out.println(" Мимо. Ход переходит.");
                    System.out.println("Нажми Enter, чтобы передать ход...");
                    scanner.nextLine();
                    scanner.nextLine();
                    for (int i = 0; i < 30; i++) {
                        System.out.println();
                    }
                    player1Turn = !player1Turn;
                }
            } catch (Exception e) {
                System.out.println(" Неверный ввод! Пример: A1 или 1A");
            }
        }
    }
    private static void setupPlayerShips(Scanner scanner, Board board, String playerName,int[][] fleet){
        System.out.println("\nРасстановка игрока: " + playerName);
        for(int[] shipInfo : fleet){
            int size = shipInfo[0];
            int count = shipInfo[1];
            for(int shipNumber=1; shipNumber<=count; shipNumber++){
                boolean placed = false;
                while(!placed){
                    board.printPrettyBoard(false);
                    System.out.println("\nКорабль размером " +size+ " (" +shipNumber+ "/" +count+ ")");
                    System.out.println("Введите координату и направление");
                    System.out.println("Пример: A5 H");
                    String coord = scanner.next();
                    String direction = scanner.next();
                    try{
                        int[] parsed = InputParser.parse(coord);
                        boolean horizontal = direction.equalsIgnoreCase("H");
                        placed = board.placeShip(parsed[0], parsed[1], size, horizontal);
                    }
                    catch(Exception e){
                        System.out.println("Неверный ввод");
                    }
                    if(!placed){
                        System.out.println("Нельзя поставить сюда");
                    }
                }
            }
        }
    }
}