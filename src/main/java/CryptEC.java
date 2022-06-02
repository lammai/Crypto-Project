import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Elliptic Curve encryption/decryption
 * Pseudocode to perform these functions are provided by
 * Prof. Paulo Barreto in the TCSS 487 Cryptography course
 *
 * @author Daniel Jiang
 * @author Lam Mai
 * @author David Shcherbina
 */
public class CryptEC {

    public static byte[] encrypt(byte[] m, E521 V) {
        //  k <- Random(512);
        SecureRandom random = new SecureRandom();
        byte[] randBytes = new byte[65];
        random.nextBytes(randBytes);
        randBytes[0] = 0;
        BigInteger k = new BigInteger(randBytes);
        //  k <- 4k
        k = k.multiply(BigInteger.valueOf(4L));

        //  W <- k*V;
        //  Z <- k*G
        E521 W = V.multiply(k);
        E521 Z = ECKey.G.multiply(k);

        //  (ke || ka) <- KMACXOF256(W_x, “”, 1024, “P”)
        byte[] keyGen = Symmetric.KMACXOF256(
                Symmetric.byteArrayToString(W.getX().toByteArray()),
                new byte[]{},
                1024,
                "P");
        byte[] ke = Arrays.copyOfRange(keyGen, 0, 64);
        byte[] ka = Arrays.copyOfRange(keyGen, 64, 128);

        //  c <- KMACXOF256(ke, “”, |m|, “PKE”) xor m
        byte[] toXor = Symmetric.KMACXOF256(Symmetric.byteArrayToString(ke), new byte[]{}, m.length * 8, "PKE");
        byte[] c = Symmetric.xorBytes(toXor, m);

        //  t <- KMACXOF256(ka, m, 512, “PKA”)
        byte[] t = Symmetric.KMACXOF256(Symmetric.byteArrayToString(ka), m, 512, "PKA");

        //  cryptogram: (Z, c, t)
        return Symmetric.byteConcat(Symmetric.byteConcat(Z.getBytes(), c), t);
    }

    public static byte[] decrypt(byte[] zct, String pw) {
        // Take apart Z, c, t
        int bLen = E521.P.toByteArray().length * 2;
        E521 z = E521.createFromBytes(Arrays.copyOfRange(zct, 0, bLen));
        byte[] c = Arrays.copyOfRange(zct, bLen, zct.length - 64);
        byte[] t = Arrays.copyOfRange(zct, zct.length - 64, zct.length);

        // s <- KMACXOF256(pw, “”, 512, “K”); s <- 4s
        byte[] tempS = Symmetric.KMACXOF256(pw, new byte[]{}, 512, "K");
        byte[] sArr = new byte[65];
        System.arraycopy(tempS, 0, sArr, 1, tempS.length);
        BigInteger s = new BigInteger(sArr).multiply(BigInteger.valueOf(4L));

        // W <- s*Z
        E521 W = z.multiply(s);

        // (ke || ka) <- KMACXOF256(W_x, “”, 1024, “P”)
        byte[] keyGen = Symmetric.KMACXOF256(
                Symmetric.byteArrayToString(W.getX().toByteArray()),
                new byte[]{},
                1024,
                "P");
        byte[] ke = Arrays.copyOfRange(keyGen, 0, 64);
        byte[] ka = Arrays.copyOfRange(keyGen, 64, 128);

        // m <- KMACXOF256(ke, “”, |c|, “PKE”) xor c
        byte[] toXor = Symmetric.KMACXOF256(Symmetric.byteArrayToString(ke), new byte[]{}, c.length * 8, "PKE");
        byte[] m = Symmetric.xorBytes(toXor, c);

        // t’ <- KMACXOF256(ka, m, 512, “PKA”)
        byte[] tPrime = Symmetric.KMACXOF256(Symmetric.byteArrayToString(ka), m, 512, "PKA");

        // accept if, and only if, t’ = t
        return Arrays.equals(t, tPrime) ? m : c;
    }

}
