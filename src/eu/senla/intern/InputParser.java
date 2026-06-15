package eu.senla.intern;

public class InputParser {
    public static int[] parse(String input) {
        if (input == null) {
            throw new IllegalArgumentException("Пустой ввод");
        }
        input = input.trim().toUpperCase();
        if (input.isEmpty()) {
            throw new IllegalArgumentException("Пустой ввод");
        }
        char letter;
        int number;
        try {
            if (Character.isLetter(input.charAt(0))) {
                letter = input.charAt(0);

                String numPart = input.substring(1);
                number = Integer.parseInt(numPart);
            } else {
                letter = input.charAt(input.length() - 1);
                String numPart = input.substring(0, input.length() - 1);
                number = Integer.parseInt(numPart);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Неверный формат");
        }
        int row = number - 1;
        int col = letter - 'A';
        if (row < 0 || row >= 16 || col < 0 || col >= 16) {
            throw new IllegalArgumentException("Координаты вне поля");
        }
        return new int[]{row, col};
    }
}