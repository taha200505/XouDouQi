package org.ensah.util;
import java.util.Scanner;
public class ConsoleUtils {
    public static int readInt(Scanner scanner, int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.print("Veuillez entrer un nombre entre " + min + " et " + max + ": ");
            } catch (NumberFormatException e) {
                System.out.print("Entrée invalide. Veuillez entrer un nombre: ");
            }
        }
    }
}
