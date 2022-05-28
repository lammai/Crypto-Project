import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    // Symmetric Cryptography [a-e]
    // a -> Compute a plain cryptographic hash of a given file
    // b -> Compute a plain cryptographic hash of text input [BONUS]
    // c -> Encrypt a given data file symmetrically under a given passphrase
    // d -> Decrypt a given symmetric cryptogram under a given passphrase
    // e -> Compute an authentication tag (MAC) of a given file under a given passphrase [BONUS]

    // Elliptic Curve Arithmetic [f-m]
    // f -> Generate an elliptic key pair from a given passphrase and write the public key to a file
    // g -> Encrypt the private key under the given password and write it to a file [BONUS]
    // h -> Encrypt a data file under a given elliptic public key file
    // i -> Decrypt a given elliptic-encrypted file from a given password
    // j -> Encrypt/decrypt text input [BONUS]
    // k -> Sign a given file from a given password and write the signature to a file
    // l -> Verify a given data file and its signature file under a given public key file
    // m -> Offer the possibility of encrypting a file under the recipient's public key and also signing it under the user's own private key [BONUS]

    /** Scanner object for reading user input */
    private static final Scanner scan = new Scanner(System.in);

    /** Main method for the console I/O */
    public static void main(String[] args) {
        System.out.println("\nTCSS 487 - Cryptographic Utility Library");
        System.out.println("Made by: Daniel Jiang, David Shcherbina, Lam Mai");
        mainMenu();
    }

    /** Validates user input */
    private static String validateInput(String input, final String regex, final String menu) {
        input = input.toLowerCase().trim();
        while (!input.matches(regex)) {
            System.out.println("Invalid option, please try again");
            switch (menu) {
                case "MainMenu" -> mainMenuOptions();
                case "SymmetricMenu" -> symmetricMenuOptions();
            }
            input = scan.nextLine();
        }
        return input;
    }

    /** Main menu */
    private static void mainMenu() {
        final String mainMenuRegex = "[1-2q]";
        mainMenuOptions();
        final String input = scan.nextLine();
        String choice = validateInput(input, mainMenuRegex, "MainMenu");
        switch (choice) {
            case "1" -> symmetricMenu(input, choice);
            case "2" -> ellipticMenu(input, choice);
            case "q" -> System.exit(0);
        }
    }

    /** Main menu options */
    private static void mainMenuOptions() {
        System.out.println("\nMain Menu: Select an option");
        System.out.println("1 -> Symmetric Cryptography");
        System.out.println("2 -> Elliptic Curve Arithmetic");
        System.out.println("q -> Quit\n");
        System.out.print("-> ");
    }

    /** Symmetric cryptography menu */
    private static void symmetricMenu(String input, String choice) {
        final String symmetricMenuRegex = "[a-erq]";
        symmetricMenuOptions();
        input = scan.nextLine().trim();
        choice = validateInput(input, symmetricMenuRegex, "SymmetricMenu");
        switch (choice) {
            case "a" -> cryptoHashFile();
            case "b" -> cryptoHashInput();
            case "c" -> encryptWithPassphrase();
            case "d" -> decryptWithPassphrase();
            case "e" -> computeMACFileWithPassphrase();
            case "r" -> mainMenu();
            case "q" -> System.exit(0);
        }
    }

    /** Symmetric cryptography menu options */
    private static void symmetricMenuOptions() {
        System.out.println("\nSymmetric Cryptography Menu: Select an option");
        System.out.println("a -> Compute a plain cryptographic hash of a given file");
        System.out.println("b -> Compute a plain cryptographic hash of text input [BONUS]");
        System.out.println("c -> Encrypt a given data file symmetrically under a given passphrase");
        System.out.println("d -> Decrypt a given symmetric cryptogram under a given passphrase");
        System.out.println("e -> Compute an authentication tag (MAC) of a given file under a given passphrase [BONUS]");
        System.out.println("r -> Return to the main menu");
        System.out.println("q -> Quit\n");
        System.out.print("-> ");
    }

    /** Elliptic curve arithmetic menu */
    private static void ellipticMenu(String input, String choice) {
        final String ellipticMenuRegex = "[f-mrq]";
        ellipticMenuOptions();
        input = scan.nextLine().trim();
        choice = validateInput(input, ellipticMenuRegex, "EllipticMenu");
        switch (choice) {
            case "f" -> keyPairPassphraseFile();
            case "g" -> encryptKeyPasswordFile();
            case "h" -> encryptDataFile();
            case "i" -> decryptEllipticEncryptedFile();
            case "j" -> encryptDecryptInput();
            case "k" -> signFileWithPassword();
            case "l" -> verifySignatureFile();
            case "m" -> encryptRecipientKeySignOwnKey();
            case "r" -> mainMenu();
            case "q" -> System.exit(0);
        }
    }

    /** Elliptic curve arithmetic menu options */
    private static void ellipticMenuOptions() {
        System.out.println("\nElliptic Curve Arithmetic Menu: Select an option");
        System.out.println("f -> Generate an elliptic key pair from a given passphrase and write the public key to a file");
        System.out.println("g -> Encrypt the private key under the given password and write it to a file [BONUS]");
        System.out.println("h -> Encrypt a data file under a given elliptic public key file");
        System.out.println("i -> Decrypt a given elliptic-encrypted file from a given password");
        System.out.println("j -> Encrypt/decrypt text input [BONUS]");
        System.out.println("k -> Sign a given file from a given password and write the signature to a file");
        System.out.println("l -> Verify a given data file and its signature file under a given public key file");
        System.out.println("m -> Offer the possibility of encrypting a file under the recipient's public key and also signing it under the user's own private key [BONUS]");
        System.out.println("r -> Return to the main menu");
        System.out.println("q -> Quit\n");
        System.out.print("-> ");
    }

    /** a -> Compute a plain cryptographic hash of a given file */
    private static void cryptoHashFile() {
        System.out.println("\na -> Compute a plain cryptographic hash of a given file");
        System.out.println("Please enter the directory of the file");
        String path = scan.nextLine();

        String outPath = getOutputDirectory(path);

    }

    /** b -> Compute a plain cryptographic hash of text input [BONUS] */
    private static void cryptoHashInput() {
        System.out.println("\nb -> Compute a plain cryptographic hash of text input [BONUS]");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }

    /** c -> Encrypt a given data file symmetrically under a given passphrase */
    private static void encryptWithPassphrase() {
        System.out.println("\nc -> Encrypt a given data file symmetrically under a given passphrase");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }

    /** d -> Decrypt a given symmetric cryptogram under a given passphrase */
    private static void decryptWithPassphrase() {
        System.out.println("\nd -> Decrypt a given symmetric cryptogram under a given passphrase");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }

    /** e -> Compute an authentication tag (MAC) of a given file under a given passphrase [BONUS] */
    private static void computeMACFileWithPassphrase() {
        System.out.println("\ne -> Compute an authentication tag (MAC) of a given file under a given passphrase [BONUS]");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }

    /** f -> Generate an elliptic key pair from a given passphrase and write the public key to a file */
    private static void keyPairPassphraseFile() {
        System.out.println("\nf -> Generate an elliptic key pair from a given passphrase and write the public key to a file");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }

    /** g -> Encrypt the private key under the given password and write it to a file [BONUS] */
    private static void encryptKeyPasswordFile() {
        System.out.println("\ng -> Encrypt the private key under the given password and write it to a file [BONUS]");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }

    /** h -> Encrypt a data file under a given elliptic public key file */
    private static void encryptDataFile() {
        System.out.println("\nh -> Encrypt a data file under a given elliptic public key file");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }

    /** i -> Decrypt a given elliptic-encrypted file from a given password */
    private static void decryptEllipticEncryptedFile() {
        System.out.println("\ni -> Decrypt a given elliptic-encrypted file from a given password");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }

    /** j -> Encrypt/decrypt text input [BONUS] */
    private static void encryptDecryptInput() {
        System.out.println("\nj -> Encrypt/decrypt text input [BONUS]");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }

    /** k -> Sign a given file from a given password and write the signature to a file */
    private static void signFileWithPassword() {
        System.out.println("\nk -> Sign a given file from a given password and write the signature to a file");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }

    /** l -> Verify a given data file and its signature file under a given public key file */
    private static void verifySignatureFile() {
        System.out.println("\nl -> Verify a given data file and its signature file under a given public key file");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }

    /** m -> Offer the possibility of encrypting a file under the recipient’s public key and also signing it under the user’s own private key [BONUS] */
    private static void encryptRecipientKeySignOwnKey() {
        System.out.println("\nm -> Offer the possibility of encrypting a file under the recipient's public key and also signing it under the user's own private key [BONUS]");
        String input = scan.nextLine();
        System.out.println("NEED TODO: Exiting program now");
    }
    
    // private static void handleCryptHash() {
    //     // Doing console input first for now
    //     // TODO: compute plain cryptographic hash from file
    //     System.out.print("Enter message to encrypt\n-> ");
    //     String input = scan.nextLine();

    //     System.out.println("Result:");
    //     System.out.println(Symmetric.byteToHexString(Symmetric.computeHash(input.getBytes())));
    //     System.out.println("----------------------------------------------");
    //     System.out.println();
    // }

    // private static void handleSymmEncrypt() {

    // }

    // private static void handleSymmDecrypt() {

    // }

    /**
     * Get the output directory, which is the same directory as the input file.
     * The output file will have "-output" added to the name.
     *
     * @param fileDirectory The directory of the input file
     * @return The directory of the output file
     */
    public static String getOutputDirectory(String fileDirectory) {
        if (fileDirectory.isEmpty()) return null;

        String outputDir = "", fileName;
        try {
            // Making this process file directory correctly for both Windows and Linux
            if (fileDirectory.lastIndexOf("/") < 0 && fileDirectory.lastIndexOf("\\") < 0) {
                fileName = fileDirectory.substring(0, fileDirectory.lastIndexOf(".txt"));
                System.out.println(fileName);
                outputDir = fileName + "-output.txt";
            } else {
                fileName = fileDirectory.substring(2 + fileDirectory.lastIndexOf("/") + fileDirectory.lastIndexOf("\\"), fileDirectory.lastIndexOf(".txt"));
                int slashIndex = fileDirectory.lastIndexOf("/") > 0 ? fileDirectory.lastIndexOf("/") + 1 : fileDirectory.lastIndexOf("\\") + 1;
                outputDir = fileDirectory.substring(0, slashIndex) + fileName + "-output.txt";
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid directory: " + e.getMessage());
        }
        return outputDir;
    }
}
