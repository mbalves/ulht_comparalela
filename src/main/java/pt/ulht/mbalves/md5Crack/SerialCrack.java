package pt.ulht.mbalves.md5Crack;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SerialCrack {

    private static byte[] passHash;
    // Number of symbols in password
    private static final int PASSLEN = 6;
    // Possible password symbols
    private static String symbols = "abcdefghijklmnopqrstuvwxyz";
    // Byte Symbols
    private static byte[] byteSymbols;
    // Total number of possible passwords with PASSLEN length and symbols.length
    private static long nPasswords;

    // Power digits using the number of symbols
    private static int[] digitPowers;
    private static byte[] password;

    private static MessageDigest md;

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String passHashString = "b373870b9139bbade83396a49b1afc9a";
        // MD5(aaaaaa) = 0b4e7a0e5fe84ad35fb5f95b9ceeac79
        // MD5(baaaaa) = 46399f97b09b5ca519a524a3dfc68419
        // MD5(helloo) = b373870b9139bbade83396a49b1afc9a
        // MD5(######) = 7627cb9027e713e301e83a8f13057055

        if (args.length == 1) {
            passHashString = args[0];
        }

        init(passHashString);

        System.out.println("------------ 4. Crack MD5 (Serial) ---------------");
        System.out.print("Cracking Hash  = ");
        printHash(passHash, passHash.length, "%02x", true);

        long startTime = System.currentTimeMillis();
        hashExplore();
        long stopTime = System.currentTimeMillis();
        long serialTime = stopTime - startTime;
        System.out.println("Elapsed Time   = " + serialTime + "ms");
        System.out.println("-------------------------------------------------------");

    }

    private static void init(String passHashString) throws NoSuchAlgorithmException {
        int i;

        // Convert the 32 digits hash string to crack into bytes
        // Each byte corresponds to 2 string characters: we need 16 bytes
        passHash = new byte[passHashString.length() / 2];
        for (i = 0; i < passHashString.length() / 2; i++) {
            byte high = (byte) (xtob(passHashString.charAt(2 * i)) << 4);
            byte low = xtob(passHashString.charAt(2 * i + 1));
            passHash[i] = (byte) (high + low);
            // System.out.printf("%02x ", passHash[i]);
        }
        digitPowers = new int[PASSLEN + 1];
        for (i = 0; i < PASSLEN + 1; i++) {
            digitPowers[i] = (int) Math.pow(symbols.length(), i);
        }
        byteSymbols = symbols.getBytes();
        nPasswords = (long) Math.pow(symbols.length(), PASSLEN);
        password = new byte[PASSLEN];

        md = MessageDigest.getInstance("MD5");
    }

    private static void hashExplore() {
        int i, n, loops = 0, digit;
        try {
            for (n = 0; n < nPasswords; n++) {
                for (i = PASSLEN - 1; i >= 0; i--) {
                    digit = (n % digitPowers[i + 1]) / digitPowers[i];
                    password[PASSLEN - 1 - i] = byteSymbols[digit];
                }
                loops++;
                if (testPass(password) == 0) {
                    System.out.print("Found password = ");
                    printHash(password, password.length, "%c", true);
                    System.out.printf("Iterations     = %d \n", loops);
                    break;
                }
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No such Algorithm");
        }
    }

    private static int testPass(byte[] password) throws NoSuchAlgorithmException {

        // MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(password);
        byte[] hash = md.digest();
        int diff;
        int i = 0;
        // Compare hashes
        while (((diff = passHash[i] ^ hash[i]) == 0) && (i < hash.length - 1))
            i++;
        if (diff == 0) {
            //System.out.print("\nMatch ");
        }
        return diff;
    }

    private static void printHash(byte[] hash, int length, String format, boolean newline) {
        for (int i = 0; i < length; i++)
            System.out.printf(format, hash[i]);
        if (newline)
            System.out.println();
    }

    // Converts ascii hexadecimal to byte
    private static byte xtob(char c) {
        byte result;

        switch (c) {
            case 'f':
            case 'F':
                result = 15;
                break;
            case 'e':
            case 'E':
                result = 14;
                break;
            case 'd':
            case 'D':
                result = 13;
                break;
            case 'c':
            case 'C':
                result = 12;
                break;
            case 'b':
            case 'B':
                result = 11;
                break;
            case 'a':
            case 'A':
                result = 10;
                break;
            case '9':
                result = 9;
                break;
            case '8':
                result = 8;
                break;
            case '7':
                result = 7;
                break;
            case '6':
                result = 6;
                break;
            case '5':
                result = 5;
                break;
            case '4':
                result = 4;
                break;
            case '3':
                result = 3;
                break;
            case '2':
                result = 2;
                break;
            case '1':
                result = 1;
                break;
            case '0':
                result = 0;
            default:
                result = 0;
        }
        return result;
    }
}
