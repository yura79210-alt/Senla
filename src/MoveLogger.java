import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.io.PrintWriter;
public class MoveLogger {
    public static void logStatistics(String player, int shots, int hits, double accuracy, int aliveShips){
        log("Игрок: " + player);
        log("Ходов: " + shots);
        log("Попаданий: " + hits);
        log("Точность: " + String.format("%.1f", accuracy) + "%");
        log("Осталось кораблей: " + aliveShips);
        log("----------------------");
    }
    public static void logFinalBoards(String p1, String board1, String p2, String board2){
        try(PrintWriter out = new PrintWriter(new FileWriter("game_log.txt", true))){
            out.println();
            out.println("===== FINAL BOARDS =====");
            out.println();
            out.println(p1);
            out.println(board1);
            out.println();
            out.println(p2);
            out.println(board2);
            out.println();
        }
        catch(Exception e){
            e.printStackTrace();

        }
    }
    private static final String FILE_NAME = "game_log.txt";
    public static void log(String text) {
        try (FileWriter writer = new FileWriter(FILE_NAME, true)) {
            writer.write(text + "\n");
        } catch (IOException e) {
            System.out.println("Ошибка записи в файл");
        }
    }
    public static void logGameStart(String p1, String p2) {
        log("\n=== Новая игра ===");
        log("Время: " + LocalDateTime.now());
        log("Игроки: " + p1 + " vs " + p2);
    }
    public static void logMove(String player, String move, boolean hit) {
        log("Ход: " + player + " -> " + move + " " + (hit ? "ПОПАЛ" : "МИМО"));
    }
    public static void logGameEnd(String winner) {
        log("Игра окончена. Победитель: " + winner);
    }
    public static void showAllGames(){
        try{
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(FILE_NAME));
            String line;
            while((line = reader.readLine()) != null){
                System.out.println(line);
            }
            reader.close();
        }
        catch(Exception e){
            System.out.println(
                    "История игр пока отсутствует."
            );
        }
    }
}