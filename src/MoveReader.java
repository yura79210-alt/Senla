import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class MoveReader {
    public static void readMoves() {
        try (BufferedReader reader = new BufferedReader(new FileReader("moves.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Прочитано: " + line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}