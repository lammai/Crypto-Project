import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    private static final Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Cryptographic utility - TCSS487");
        System.out.println("Made by: Daniel Jiang, David Shcherbina, Lam Mai");

        String input;
        String userChoice;
        displayOptions();

        do {
            System.out.print("(Press h to display option menu)\n-> ");
            input = scan.nextLine();
            // Regular expression [1-3QHqh] - only allow input of 1,2,3,Q,H,q,h
            userChoice = validateInput(input, "[1-3QHqh]").toLowerCase();

            System.out.println("----------------------------------------------");
            switch (userChoice) {
                case "1" -> handleCryptHash();
                case "2" -> handleSymmEncrypt();
                case "3" -> handleSymmDecrypt();

                case "h" -> displayOptions();
            }

        } while (!userChoice.equals("q"));


    }

    private static void displayOptions() {
        System.out.println("__Select from the following options__");
        System.out.println("1 -> Compute plain cryptographic hash");
        System.out.println("2 -> Encrypt data file symmetrically given a passphrase");
        System.out.println("3 -> Decrypt data file symmetrically given a passphrase");
        System.out.println("Press q to quit");

    }

    private static String validateInput(String input, String regex) {
        while( input == null || !input.matches(regex)) {
            System.out.println("Invalid input, please try again");
            System.out.print("-> ");
            input = scan.nextLine();
        }
        return input;
    }

    private static void handleCryptHash() {
        // Doing console input first for now
        // TODO: compute plain cryptographic hash from file
        System.out.print("Enter message to encrypt\n-> ");
        String input = scan.nextLine();

        System.out.println("Result:");
        System.out.println(Symmetric.byteToHexString(Symmetric.computeHash(input.getBytes())));
        System.out.println("----------------------------------------------");
        System.out.println();
    }

    private static void handleSymmEncrypt() {

    }

    private static void handleSymmDecrypt() {

    }

}
