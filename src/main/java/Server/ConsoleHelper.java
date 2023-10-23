package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String input = "";
        while (true) {
            try {
                input = bufferedReader.readLine();
                break;
            } catch (IOException e) {
                System.out.println("An error occurred while trying to enter text. Try again.");
            }
        }
        return input;
    }

    public static int readInt() {
        int input = 0;
        while (true) {
            try {
                input = Integer.parseInt(readString());
                break;
            } catch (NumberFormatException e) {
                System.out.println("An error while trying to enter a number. Try again.");
            }
        }
        return input;
    }
}
