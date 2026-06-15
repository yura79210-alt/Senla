import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Board board1 = new Board();
        Board board2 = new Board();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите режим:");
        System.out.println("1 - Один игрок");
        System.out.println("2 - Два игрока");
        int gameMode = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Введите имя игрока: ");
        String player1Name = scanner.nextLine();
        if (player1Name.isEmpty()) {
            player1Name = "Игрок";
        }
        String player2Name;
        if (gameMode == 1) {
            player2Name = "Бот";
        } else {
            System.out.print("Введите имя игрока 2: ");
            player2Name = scanner.nextLine();
            if (player2Name.isEmpty()) {
                player2Name = "Игрок 2";
            }
        }
        if (player1Name.equalsIgnoreCase("admin") || player2Name.equalsIgnoreCase("admin")) {
            System.out.println("ADMIN MODE");
            MoveLogger.showAllGames();
            return;
        }
        MoveLogger.logGameStart(player1Name, player2Name);
        System.out.println("1 - Авторасстановка");
        System.out.println("2 - Ручная расстановка");
        int setupMode = scanner.nextInt();
        scanner.nextLine();

        int[][] fleet = {{6, 1}, {5, 2}, {4, 3}, {3, 4}, {2, 5}, {1, 6}};
        if (setupMode == 1) {
            for (int[] shipInfo : fleet) {
                int size = shipInfo[0];
                int count = shipInfo[1];
                for (int i = 0; i < count; i++) {
                    board1.placeShipRandomly(size);
                    board2.placeShipRandomly(size);
                }
            }
        } else {
            setupPlayerShips(scanner, board1, player1Name, fleet);
            System.out.println("\nПередайте ход следующему игроку.");
            System.out.println("Нажмите Enter...");
            scanner.nextLine();
            clearConsole();
            setupPlayerShips(scanner, board2, player2Name, fleet);
        }
        boolean player1Turn = true;
        int player1Shots = 0;
        int player2Shots = 0;
        int player1Hits = 0;
        int player2Hits = 0;
        System.out.println("Игра началась! Введи координаты выстрела X и Y (0-9)");
        String userInput;
        int[] coords;
        int x, y;
        while (true) {
            Board currentBoard;
            String playerName;
            if (player1Turn) {
                currentBoard = board2;
                playerName = player1Name;
            } else {
                currentBoard = board1;
                playerName = player2Name;
            }
            if (player1Turn) {
                printBoardsSideBySide(board1, board2);
            } else {
                printBoardsSideBySide(board2, board1);
            }
            System.out.println("\n" + playerName + ", твой ход!");
            try {
                if (playerName.equals("BOT")) {
                    userInput = generateBotShot();
                    System.out.println("BOT стреляет: " + userInput);
                } else {
                    System.out.print("Введите координаты: ");
                    userInput = scanner.next();
                }
                coords = InputParser.parse(userInput);
                x = coords[0];
                y = coords[1];
                int result = currentBoard.shoot(x, y);
                if (player1Turn) {
                    player1Shots++;
                    if (result == 1 || result == 2) {
                        player1Hits++;
                    }
                } else {
                    player2Shots++;
                    if (result == 1 || result == 2) {
                        player2Hits++;
                    }
                }
                if (playerName.equals("BOT")) {
                    if (result == 1) {
                        botHits.add(new int[]{x, y});
                    } else if (result == 2) {
                        for (int[] hit : botHits) {
                            for (int dx = -1; dx <= 1; dx++) {
                                for (int dy = -1; dy <= 1; dy++) {
                                    int nx = hit[0] + dx;
                                    int ny = hit[1] + dy;
                                    if (nx >= 0 && nx < 16 && ny >= 0 && ny < 16) {
                                        botShots[nx][ny] = true;
                                    }
                                }
                            }
                        }
                        // добавляем последний уничтоживший выстрел
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                int nx = x + dx;
                                int ny = y + dy;
                                if (nx >= 0 && nx < 16 && ny >= 0 && ny < 16) {
                                    botShots[nx][ny] = true;
                                }
                            }
                        }
                        botHits.clear();
                    }
                }
                if (result == -1)
                    continue;
                String moveText = userInput.toUpperCase();
                MoveLogger.logMove(playerName, moveText, result != 0);
                if (result == 0) {
                    System.out.println("МИМО");
                } else if (result == 1) {
                    System.out.println("РАНИЛ");
                } else if (result == 2) {
                    System.out.println("УБИЛ");
                }
                if (player1Turn) {
                    printBoardsSideBySide(board1, board2);
                } else {
                    printBoardsSideBySide(board2, board1);
                }
                if (currentBoard.allShipsSunk()) {
                    System.out.println("Победил " + playerName);
                    System.out.println("\nСТАТИСТИКА:");
                    double p1Accuracy = player1Shots == 0 ? 0 : (player1Hits * 100.0 / player1Shots);
                    double p2Accuracy = player2Shots == 0 ? 0 : (player2Hits * 100.0 / player2Shots);
                    System.out.println(player1Name + ": ходов=" + player1Shots + ", попаданий=" + player1Hits + ", точность=" + String.format("%.1f", p1Accuracy) + "%");
                    System.out.println(player2Name + ": ходов=" + player2Shots + ", попаданий=" + player2Hits + ", точность=" + String.format("%.1f", p2Accuracy) + "%");
                    MoveLogger.logFinalBoards(player1Name, board1.boardAsText(false), player2Name, board2.boardAsText(false));
                    MoveLogger.logStatistics(player1Name, player1Shots, player1Hits, p1Accuracy, board1.countAliveShips());
                    MoveLogger.logStatistics(player2Name, player2Shots, player2Hits, p2Accuracy, board2.countAliveShips());
                    MoveLogger.logGameEnd(playerName);
                    break;
                }
                if (result == 1 || result == 2) {
                    if (result == 1) {
                        System.out.println("Ранил! Ходишь ещё раз.");
                    } else {
                        System.out.println("Убил! Ходишь ещё раз.");
                    }
                    waitEnter(scanner);
                } else {
                    System.out.println("Мимо. Ход переходит.");
                    if (gameMode == 2) {
                        System.out.println("Передайте ход следующему игроку.");
                        System.out.println("Нажмите Enter...");
                        scanner.nextLine();
                        scanner.nextLine();
                        clearConsole();
                    } else {
                        System.out.println("Нажмите Enter...");
                        scanner.nextLine();
                        scanner.nextLine();
                    }
                    player1Turn = !player1Turn;
                }
            } catch (Exception e) {
                clearConsole();
                if (player1Turn) {
                    printBoardsSideBySide(board1, board2);
                } else {
                    printBoardsSideBySide(board2, board1);
                }
                System.out.println("\nНеверный ввод!");
                waitEnter(scanner);
            }
        }
    }

    private static void setupPlayerShips(Scanner scanner, Board board, String playerName, int[][] fleet) {
        System.out.println("\nРасстановка игрока: " + playerName);
        for (int[] shipInfo : fleet) {
            int size = shipInfo[0];
            int count = shipInfo[1];
            for (int shipNumber = 1; shipNumber <= count; shipNumber++) {
                boolean placed = false;
                while (!placed) {
                    board.printPrettyBoard(false);
                    System.out.println("\nКорабль размером " + size + " (" + shipNumber + "/" + count + ")");
                    System.out.println("Введите координату и направление");
                    System.out.println("Пример: A5 H или V! Где H - горизонтально, V - Вертикально");
                    String coord = scanner.next();
                    String direction = scanner.next();
                    try {
                        int[] parsed = InputParser.parse(coord);
                        boolean horizontal;
                        if (direction.equalsIgnoreCase("H")) {
                            horizontal = true;
                        } else if (direction.equalsIgnoreCase("V")) {
                            horizontal = false;
                        } else {
                            throw new IllegalArgumentException();
                        }
                        placed = board.placeShip(parsed[0], parsed[1], size, horizontal);
                        if (!placed) {
                            clearConsole();
                            board.printPrettyBoard(false);
                            System.out.println("\nНельзя поставить сюда");
                            System.out.println("Попробуйте другое место.");
                            System.out.println("Нажмите Enter...");
                            scanner.nextLine();
                            scanner.nextLine();
                        }
                    } catch (Exception e) {
                        clearConsole();
                        board.printPrettyBoard(false);
                        System.out.println("\nНеверный ввод!");
                        System.out.println("Введите координаты заново.");
                        waitEnter(scanner);
                    }
                }
            }
        }
    }

    private static boolean[][] botShots = new boolean[16][16];
    private static java.util.List<int[]> botHits = new java.util.ArrayList<>();

    private static String generateBotShot() {
        int row;
        int col;


        if (botHits.size() >= 2) {
            int[] first = botHits.get(0);
            int[] second = botHits.get(1);
            boolean vertical = first[1] == second[1];
            if (vertical) {
                int min = botHits.stream().mapToInt(h -> h[0]).min().getAsInt();
                int max = botHits.stream().mapToInt(h -> h[0]).max().getAsInt();
                col = first[1];
                row = min - 1;
                if (row >= 0 && !botShots[row][col]) {
                    botShots[row][col] = true;
                    return "" + (char) ('A' + col) + (row + 1);
                }
                row = max + 1;
                if (row < 16 && !botShots[row][col]) {
                    botShots[row][col] = true;
                    return "" + (char) ('A' + col) + (row + 1);
                }
            } else {
                int min = botHits.stream().mapToInt(h -> h[1]).min().getAsInt();
                int max = botHits.stream().mapToInt(h -> h[1]).max().getAsInt();
                row = first[0];
                col = min - 1;
                if (col >= 0 && !botShots[row][col]
                ) {
                    botShots[row][col] = true;
                    return "" + (char) ('A' + col) + (row + 1);
                }
                col = max + 1;
                if (col < 16 && !botShots[row][col]) {
                    botShots[row][col] = true;
                    return "" + (char) ('A' + col) + (row + 1);
                }
            }
        }


        if (botHits.size() == 1) {
            int[] hit = botHits.get(0);
            int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] d : dirs) {
                row = hit[0] + d[0];
                col = hit[1] + d[1];
                if (row >= 0 && row < 16 && col >= 0 && col < 16 && !botShots[row][col]) {
                    botShots[row][col] = true;
                    return "" + (char) ('A' + col) + (row + 1);
                }
            }
        }
        while (true) {
            row = (int) (Math.random() * 16);
            col = (int) (Math.random() * 16);
            if (!botShots[row][col]) {
                botShots[row][col] = true;
                return "" + (char) ('A' + col) + (row + 1);
            }
        }
    }

    private static void waitEnter(Scanner scanner) {
        System.out.println("Нажмите Enter...");
        scanner.nextLine();
    }

    private static void clearConsole() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    private static void printBoardsSideBySide(Board myBoard, Board enemyBoard) {
        String[] left = myBoard.getBoardLines(false);
        String[] right = enemyBoard.getBoardLines(true);
        System.out.println("\nТВОЁ ПОЛЕ" + "                                  " + "ПОЛЕ ПРОТИВНИКА");
        for (int i = 0; i < left.length; i++) {
            System.out.printf("%-45s %s%n", left[i], right[i]);
        }
    }
}