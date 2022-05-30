import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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

    /** Input directory path */
    private static final String inputDirectory = System.getProperty("user.dir") + "/files/input/";

    /** Output directory path */
    private static final String outputDirectory = System.getProperty("user.dir") + "/files/output/";

    /** Main method for the console I/O */
    public static void main(String[] args) {
        System.out.println("------------------------------------------------");
        System.out.println("    TCSS 487 - Cryptographic Utility Library");
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
                case "EllipticMenu" -> ellipticMenuOptions();
                case "symmetric" -> afterResult("symmetric");
                case "elliptic" -> afterResult("elliptic");
            }
            
            input = scan.nextLine().toLowerCase().trim();
        }
        return input;
    }

    /** Main menu */
    private static void mainMenu() {
        final String mainMenuRegex = "[1-2q]";
        mainMenuOptions();
        final String input = scan.nextLine().toLowerCase().trim();
        String choice = validateInput(input, mainMenuRegex, "MainMenu");
        switch (choice) {
            case "1" -> symmetricMenu(input, choice);
            case "2" -> ellipticMenu(input, choice);
            case "q" -> System.exit(0);
        }
    }

    /** Main menu options */
    private static void mainMenuOptions() {
        System.out.println("------------------------------------------------");
        System.out.println("Main Menu: Select an option");
        System.out.println("1 -> Symmetric Cryptography");
        System.out.println("2 -> Elliptic Curve Arithmetic");
        System.out.println("q -> Quit");
        System.out.println("------------------------------------------------");
        System.out.print("-> ");
    }

    /** Symmetric cryptography menu */
    private static void symmetricMenu(String input, String choice) {
        final String symmetricMenuRegex = "[a-erq]";
        symmetricMenuOptions();
        input = scan.nextLine().trim();
        choice = validateInput(input, symmetricMenuRegex, "SymmetricMenu");
        switch (choice) {
            case "a" -> cryptoHashFromFile();
            case "b" -> cryptoHashFromInput();
            case "c" -> encryptUnderPassphrase();
            case "d" -> decryptUnderPassphrase();
            case "e" -> computeMACFromFileUnderPassphrase();
            case "r" -> mainMenu();
            case "q" -> System.exit(0);
        }
    }

    /** Symmetric cryptography menu options */
    private static void symmetricMenuOptions() {
        System.out.println("------------------------------------------------");
        System.out.println("Symmetric Cryptography Menu: Select an option");
        System.out.println("a -> Compute a plain cryptographic hash of a given file");
        System.out.println("b -> Compute a plain cryptographic hash of text input [BONUS]");
        System.out.println("c -> Encrypt a given data file symmetrically under a given passphrase");
        System.out.println("d -> Decrypt a given symmetric cryptogram under a given passphrase");
        System.out.println("e -> Compute an authentication tag (MAC) of a given file under a given passphrase [BONUS]");
        returnOrQuit();
    }

    /** Elliptic curve arithmetic menu */
    private static void ellipticMenu(String input, String choice) {
        final String ellipticMenuRegex = "[f-mrq]";
        ellipticMenuOptions();
        input = scan.nextLine().trim();
        choice = validateInput(input, ellipticMenuRegex, "EllipticMenu");
        switch (choice) {
            case "f" -> generateKeyPairUnderPassphraseToFile();
            case "g" -> encryptUnderPasswordToFile();
            case "h" -> encryptUnderPublicKeyFile();
            case "i" -> decryptUnderEncryptedFileFromPassword();
            case "j" -> encryptDecryptInput();
            case "k" -> signFileFromPasswordToFile();
            case "l" -> verifyUnderPublicKeyFile();
            case "m" -> encryptFileUnderPublicKeyPrivateKey();
            case "r" -> mainMenu();
            case "q" -> System.exit(0);
        }
    }

    /** Elliptic curve arithmetic menu options */
    private static void ellipticMenuOptions() {
        System.out.println("------------------------------------------------");
        System.out.println("Elliptic Curve Arithmetic Menu: Select an option");
        System.out.println("f -> Generate an elliptic key pair from a given passphrase and write the public key to a file");
        System.out.println("g -> Encrypt the private key under the given password and write it to a file [BONUS]");
        System.out.println("h -> Encrypt a data file under a given elliptic public key file");
        System.out.println("i -> Decrypt a given elliptic-encrypted file from a given password");
        System.out.println("j -> Encrypt/decrypt text input [BONUS]");
        System.out.println("k -> Sign a given file from a given password and write the signature to a file");
        System.out.println("l -> Verify a given data file and its signature file under a given public key file");
        System.out.println("m -> Offer the possibility of encrypting a file under the recipient's public key and also signing it under the user's own private key [BONUS]");
        returnOrQuit();
    }

    /** Return or quit dialog */
    private static void returnOrQuit() {
        System.out.println("r -> Return to the main menu");
        System.out.println("q -> Quit");
        System.out.println("------------------------------------------------");
        System.out.print("-> ");
    }

    /** Go again, return, or quit dialog */
    private static void afterResult(String menu) {
        System.out.println("------------------------------------------------");
        System.out.println("x -> Go again");
        System.out.println("r -> Return to " + menu + " menu");
        System.out.println("q -> Quit");
        System.out.println("------------------------------------------------");
        System.out.print("-> ");
    }

    /** Buffer for the user after the results of a computation */
    private static void buffer(final String menu, final String again) {
        afterResult(menu);

        final String goAgainReturnQuitRegex = "[rqx]";

        final String input = scan.nextLine();
        String choice = validateInput(input, goAgainReturnQuitRegex, menu);

        if (choice.equals("q")) {
            System.exit(0);
        }
        else if (choice.equals("r")) {
            switch (menu) {
                case "symmetric" -> symmetricMenu(input, choice);
                case "elliptic" -> ellipticMenu(input, choice);
            }
        }
        else {
            switch (again) {
                case "a" -> cryptoHashFromFile();
                case "b" -> cryptoHashFromInput();
                case "c" -> encryptUnderPassphrase();
                case "d" -> decryptUnderPassphrase();
                case "e" -> computeMACFromFileUnderPassphrase();
                case "f" -> generateKeyPairUnderPassphraseToFile();
                case "g" -> encryptUnderPasswordToFile();
                case "h" -> encryptUnderPublicKeyFile();
                case "i" -> decryptUnderEncryptedFileFromPassword();
                case "j" -> encryptDecryptInput();
                case "k" -> signFileFromPasswordToFile();
                case "l" -> verifyUnderPublicKeyFile();
                case "m" -> encryptFileUnderPublicKeyPrivateKey();
            }
        }
    }

    // ---------------------------- SYMMETRIC CRYPTOGRAHPY -------------------------------------

    /** a -> Compute a plain cryptographic hash of a given file */
    private static void cryptoHashFromFile() {
        System.out.println("------------------------------------------------");
        System.out.println("Enter a file to cryptographically hash:");
        String inputFileName = scan.nextLine().trim();

        List<String> listOfHashes = new ArrayList<>();

        try {
            File file = new File(inputDirectory + inputFileName);
            Scanner fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                byte[] m = line.getBytes();
                byte[] hash = Symmetric.computeHash(m);
                String output = Symmetric.byteToHexString(hash);
                listOfHashes.add(output);
            }
            fileReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found, try again");
            cryptoHashFromFile();
        }

        System.out.println("\nResult:");
        for (String hash : listOfHashes) {
            System.out.println(hash);
        }

        buffer("symmetric", "a");
    }

    /** b -> Compute a plain cryptographic hash of text input [BONUS] */
    private static void cryptoHashFromInput() {
        System.out.println("------------------------------------------------");
        System.out.println("Enter a message to cryptographically hash:");
        String input = scan.nextLine();

        byte[] m = input.getBytes();
        byte[] hash = Symmetric.computeHash(m);
        String output = Symmetric.byteToHexString(hash);

        System.out.println("\nResult:");
        System.out.println(output);

        buffer("symmetric", "b");
    }

    /** c -> Encrypt a given data file symmetrically under a given passphrase */
    private static void encryptUnderPassphrase() {
        System.out.println("------------------------------------------------");
        System.out.println("Enter a file to encrypt under a passphrase:");
        String inputFileName = scan.nextLine().trim();

        List<String> listOfEncryptions = new ArrayList<>();

        try {
            File file = new File(inputDirectory + inputFileName);
            Scanner fileReader = new Scanner(file);
            System.out.println("Enter a passphrase:");
            String passphrase = scan.nextLine();
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                byte[] m = line.getBytes();
                byte[] hash = Symmetric.symmetricEncrypt(passphrase, m);
                String output = Symmetric.byteToHexString(hash);
                listOfEncryptions.add(output);
            }
            fileReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found, try again");
            encryptUnderPassphrase();
        }

        System.out.println("Enter a file output name:");
        String outputFileName = scan.nextLine().trim();

        try {
            File file = new File(outputDirectory + outputFileName + "SymmetricEncrypt" + ".txt");
            while (!file.createNewFile()) {
                System.out.println("File name already exists");
                System.out.println("Enter another file output name:");
                outputFileName = scan.nextLine().trim();
                file = new File(outputDirectory + outputFileName + "SymmetricEncrypt" + ".txt");
            }
            FileWriter writer = new FileWriter(outputDirectory + file.getName());
            BufferedWriter bw = new BufferedWriter(writer);
            for (String encryption : listOfEncryptions) {
                bw.write(encryption + "\n");
            }
            bw.close();
        }
        catch (IOException e) {
            System.out.println("An IOException occurred");
        }

        buffer("symmetric", "c");
    }

    /** d -> Decrypt a given symmetric cryptogram under a given passphrase */
    private static void decryptUnderPassphrase() {
        System.out.println("------------------------------------------------");
        System.out.println("Enter a file to decrypt under a passphrase:");
        String inputFileName = scan.nextLine().trim();

        List<String> listOfDecryptions = new ArrayList<>();

        try {
            File file = new File(inputDirectory + inputFileName);
            Scanner fileReader = new Scanner(file);
            System.out.println("Enter a passphrase:");
            String passphrase = scan.nextLine();
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                byte[] m = Symmetric.hexStringToByte(line);
                byte[] hash = Symmetric.symmetricDecrypt(passphrase, m);
                String output = new String(hash);
                listOfDecryptions.add(output);
            }
            fileReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found, try again");
            decryptUnderPassphrase();
        }

        System.out.println("Enter a file output name:");
        String outputFileName = scan.nextLine().trim();

        try {
            File file = new File(outputDirectory + outputFileName + "SymmetricDecrypt" + ".txt");
            while (!file.createNewFile()) {
                System.out.println("File name already exists");
                System.out.println("Enter another file output name:");
                outputFileName = scan.nextLine().trim();
                file = new File(outputDirectory + outputFileName + "SymmetricDecrypt" + ".txt");
            }
            FileWriter writer = new FileWriter(outputDirectory + file.getName());
            BufferedWriter bw = new BufferedWriter(writer);
            for (String decryption : listOfDecryptions) {
                bw.write(decryption + "\n");
            }
            bw.close();
        }
        catch (IOException e) {
            System.out.println("An IOException occured");
        }

        buffer("symmetric", "d");
    }

    /** e -> Compute an authentication tag (MAC) of a given file under a given passphrase [BONUS] */
    private static void computeMACFromFileUnderPassphrase() {
        System.out.println("------------------------------------------------");
        System.out.println("Enter a file to compute an authentication tag (MAC):");
        String fileName = scan.nextLine().trim();

        List<String> listOfAuthTags = new ArrayList<>();

        try {
            File file = new File(inputDirectory + fileName);
            Scanner fileReader = new Scanner(file);
            System.out.println("Enter a passphrase:");
            String passphrase = scan.nextLine();
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                byte[] m = line.getBytes();
                byte[] hash = Symmetric.computeAuthTag(passphrase, m);
                String output = Symmetric.byteToHexString(hash);
                listOfAuthTags.add(output);
            }
            fileReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found, try again");
            computeMACFromFileUnderPassphrase();
        }

        System.out.println("\nResult:");
        for (String authTag : listOfAuthTags) {
            System.out.println(authTag);
        }

        buffer("symmetric", "e");
    }

    // ---------------------------- ELLIPTIC CURVE ARITHMETIC ----------------------------------

    /** f -> Generate an elliptic key pair from a given passphrase and write the public key to a file */
    private static void generateKeyPairUnderPassphraseToFile() {
        System.out.println("------------------------------------------------");
        System.out.println("Enter a passphrase for the generated elliptic key pair public key:");
        String passphrase = scan.nextLine();

        byte[] pw = passphrase.getBytes();
        ECKey keyPair = new ECKey(pw);

        System.out.println("Enter a file output name:");
        String outputFileName = scan.nextLine().trim();

        try {
            File file = new File(outputDirectory + outputFileName + "PublicKey" + ".txt");
            while (!file.createNewFile()) {
                System.out.println("File name already exists");
                System.out.println("Enter another file output name:");
                outputFileName = scan.nextLine().trim();
                file = new File(outputDirectory + outputFileName + "PublicKey" + ".txt");
            }
            FileWriter writer = new FileWriter(outputDirectory + file.getName());
            BufferedWriter bw = new BufferedWriter(writer);
            final String publicKey = Symmetric.byteToHexString(keyPair.getPublicKey().getBytes());
            bw.write(publicKey + "\n");
            bw.close();
        }
        catch (IOException e) {
            System.out.println("An IOException occured");
        }

        buffer("elliptic", "f");
    }

    /** g -> Encrypt the private key under the given password and write it to a file [BONUS] */
    private static void encryptUnderPasswordToFile() {
        System.out.println("------------------------------------------------");
        System.out.println("Enter a passphrase to encrypt the generated elliptic key pair private key:");
        String passphrase = scan.nextLine();

        byte[] pw = passphrase.getBytes();
        ECKey keyPair = new ECKey(pw);
        BigInteger privateKey = keyPair.getS_Scalar();
        ECKey encryptedPrivateKey = new ECKey(privateKey);

        System.out.println("Enter a file output name:");
        String outputFileName = scan.nextLine().trim();

        try {
            File file = new File(outputDirectory + outputFileName + "PrivateKey" + ".txt");
            while (!file.createNewFile()) {
                System.out.println("File name already exists");
                System.out.println("Enter another file output name:");
                outputFileName = scan.nextLine().trim();
                file = new File(outputDirectory + outputFileName + "PrivateKey" + ".txt");
            }
            FileWriter writer = new FileWriter(outputDirectory + file.getName());
            BufferedWriter bw = new BufferedWriter(writer);
            final String encryptedPrivateKeyHex = encryptedPrivateKey.getS_Scalar().toString(16).toUpperCase();
            bw.write(encryptedPrivateKeyHex + "\n");
            bw.close();
        }
        catch (IOException e) {
            System.out.println("An IOException occured");
        }

        buffer("elliptic", "g");
    }

    /** h -> Encrypt a data file under a given elliptic public key file */
    private static void encryptUnderPublicKeyFile() {
        System.out.println("------------------------------------------------");
        System.out.println("Enter a file to encrypt under a given elliptic public key file:");
        String inputFileName = scan.nextLine().trim();
        
        List<String> listOfEncryptions = new ArrayList<>();

        try {
            File file = new File(inputDirectory + inputFileName);
            Scanner fileReader = new Scanner(file);
            System.out.println("Enter a public key file:");
            String publicKeyFileName = scan.nextLine().trim();
            File keyFile = new File(inputDirectory + publicKeyFileName);
            while (!keyFile.exists()) {
                System.out.println("File not found, try again");
                System.out.println("------------------------------------------------");
                System.out.println("Enter a public key file:");
                publicKeyFileName = scan.nextLine().trim();
                keyFile = new File(inputDirectory + publicKeyFileName);
            }

            E521 publicKey = null;

            Scanner keyReader = new Scanner(keyFile);
            while (keyReader.hasNextLine()) {
                String publicKeyHex = keyReader.nextLine();
                byte[] publicKeyBytes = Symmetric.hexStringToByte(publicKeyHex);
                publicKey = E521.createFromBytes(publicKeyBytes);
            }
            keyReader.close();

            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                byte[] m = line.getBytes();
                byte[] encryption = CryptEC.encrypt(m, publicKey);
                final String encryptionHex = Symmetric.byteToHexString(encryption);
                listOfEncryptions.add(encryptionHex);
            }
            fileReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found, try again");
            encryptUnderPublicKeyFile();
        }

        System.out.println("Enter a file output name:");
        String outputFileName = scan.nextLine().trim();

        try {
            File file = new File(outputDirectory + outputFileName + "EllipticEncrypt" + ".txt");
            while (!file.createNewFile()) {
                System.out.println("File name already exists");
                System.out.println("Enter another file output name:");
                outputFileName = scan.nextLine().trim();
                file = new File(outputDirectory + outputFileName + "EllipticEncrypt" + ".txt");
            }
            FileWriter writer = new FileWriter(outputDirectory + file.getName());
            BufferedWriter bw = new BufferedWriter(writer);
            for (String encryption : listOfEncryptions) {
                bw.write(encryption + "\n");
            }
            bw.close();
        }
        catch (IOException e) {
            System.out.println("An IOException occured");
        }

        buffer("elliptic", "h");
    }

    /** i -> Decrypt a given elliptic-encrypted file from a given password */
    private static void decryptUnderEncryptedFileFromPassword() {
        System.out.println("------------------------------------------------");
        System.out.println("Enter a file to decrypt under a given passphrase:");
        String inputFileName = scan.nextLine().trim();

        List<String> listOfDecryptions = new ArrayList<>();

        try {
            File file = new File(inputDirectory + inputFileName);
            Scanner fileReader = new Scanner(file);
            System.out.println("Enter a passphrase:");
            String passphrase = scan.nextLine();
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                byte[] zct = Symmetric.hexStringToByte(line);
                byte[] decryption = CryptEC.decrypt(zct, passphrase);
                final String decryptionString = Symmetric.byteArrayToString(decryption);
                listOfDecryptions.add(decryptionString);
            }
            fileReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found, try again");
            decryptUnderEncryptedFileFromPassword();
        }

        System.out.println("Enter a file output name:");
        String outputFileName = scan.nextLine().trim();

        try {
            File file = new File(outputDirectory + outputFileName + "EllipticDecrypt" + ".txt");
            while (!file.createNewFile()) {
                System.out.println("File name already exists");
                System.out.println("Enter another file output name:");
                outputFileName = scan.nextLine().trim();
                file = new File(outputDirectory + outputFileName + "EllipticDecrypt" + ".txt");
            }
            FileWriter writer = new FileWriter(outputDirectory + file.getName());
            BufferedWriter bw = new BufferedWriter(writer);
            for (String decryption : listOfDecryptions) {
                bw.write(decryption + "\n");
            }
            bw.close();
        }
        catch (IOException e) {
            System.out.println("An IOException occured");
        }

        buffer("elliptic", "i");
    }

    /** j -> Encrypt/decrypt text input [BONUS] */
    private static void encryptDecryptInput() {
        final String encryptDecryptRegex = "[ed]";

        String choice;
        do {
            System.out.println("------------------------------------------------");
            System.out.println("e -> Encrypt text input");
            System.out.println("d -> Decrypt text input");
            System.out.println("------------------------------------------------");
            System.out.print("-> ");
            choice = scan.nextLine().trim().toLowerCase();
            if (!choice.matches(encryptDecryptRegex)) {
                System.out.println("Invalid option, please try again");
            }
        }
        while (!choice.matches(encryptDecryptRegex));

        if (choice.equals("e")) { // Encrypt
            System.out.println("------------------------------------------------");
            System.out.println("Enter a message to encrypt:");
            final String message = scan.nextLine();
            byte[] m = message.getBytes();
            System.out.println("Enter the public key hex (without the 0x):");
            final String publicKeyHex = scan.nextLine().trim().toUpperCase();
            byte[] publicKeyBytes = Symmetric.hexStringToByte(publicKeyHex);
            E521 publicKey = E521.createFromBytes(publicKeyBytes);
            byte[] encryption = CryptEC.encrypt(m, publicKey);
            final String encryptionHex = Symmetric.byteToHexString(encryption);
            System.out.println("\nResult:");
            System.out.println(encryptionHex);
        }
        else { // Decrypt
            System.out.println("------------------------------------------------");
            System.out.println("Enter a message to decrypt:");
            final String message = scan.nextLine();
            byte[] zct = Symmetric.hexStringToByte(message);
            System.out.println("Enter the passphrase:");
            final String passphrase = scan.nextLine();
            byte[] decryption = CryptEC.decrypt(zct, passphrase);
            final String decryptionString = Symmetric.byteArrayToString(decryption);
            System.out.println("\nResult:");
            System.out.println(decryptionString);
        }

        buffer("elliptic", "j");
    }

    /** k -> Sign a given file from a given password and write the signature to a file */
    private static void signFileFromPasswordToFile() {
        System.out.println("------------------------------------------------");
        System.out.println("Enter a file to sign:");
        String inputFileName = scan.nextLine().trim();

        List<BigInteger[]> listOfSignatures = new ArrayList<>();

        try {
            File file = new File(inputDirectory + inputFileName);
            Scanner fileReader = new Scanner(file);
            System.out.println("Enter a passphrase:");
            String passphrase = scan.nextLine();
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                byte[] m = line.getBytes();
                BigInteger[] signature = Signature.generateSignature(m, passphrase);
                listOfSignatures.add(signature);
            }
            fileReader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found, try again");
            signFileFromPasswordToFile();
        }

        System.out.println("Enter a file output name:");
        String outputFileName = scan.nextLine().trim();

        try {
            File file = new File(outputDirectory + outputFileName + "Signature" + ".txt");
            while (!file.createNewFile()) {
                System.out.println("File name already exists");
                System.out.println("Enter another file output name:");
                outputFileName = scan.nextLine().trim();
                file = new File(outputDirectory + outputFileName + "Signature" + ".txt");
            }
            FileWriter writer = new FileWriter(outputDirectory + file.getName());
            BufferedWriter bw = new BufferedWriter(writer);
            for (BigInteger[] signature : listOfSignatures) {
                bw.write(signature[0] + " " + signature[1] + "\n");
            }
            bw.close();
        }
        catch (IOException e) {
            System.out.println("An IOException occured");
        }

        buffer("elliptic", "k");
    }

    /** l -> Verify a given data file and its signature file under a given public key file */
    private static void verifyUnderPublicKeyFile() {
        System.out.println("\nl -> Verify a given data file and its signature file under a given public key file");
        System.out.println("Not implemented yet: Exiting program now");
    }

    /** m -> Offer the possibility of encrypting a file under the recipient’s public key and also signing it under the user’s own private key [BONUS] */
    private static void encryptFileUnderPublicKeyPrivateKey() {
        System.out.println("\nm -> Offer the possibility of encrypting a file under the recipient's public key and also signing it under the user's own private key [BONUS]");
        System.out.println("Not implemented yet: Exiting program now");
    }
}
