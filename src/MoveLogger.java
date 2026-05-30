import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
public class MoveLogger {
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
    public static void showAllGames() {
        System.out.println("История игр записана в файле: game_log.txt");
    }
}