import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

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
        byte[] toXor = Symmetric.KMACXOF256(Symmetric.byteArrayToString(ke), new byte[]{}, m.length, "PKE");
        byte[] c = Symmetric.xorBytes(toXor, m);

        //  t <- KMACXOF256(ka, m, 512, “PKA”)
        byte[] t = Symmetric.KMACXOF256(Symmetric.byteArrayToString(ka), m, 512, "PKA");

        //  cryptogram: (Z, c, t)
        return Symmetric.byteConcat(Symmetric.byteConcat(Z.getBytes(), c), t);
    }

    public static byte[] decrypt(byte[] zct, String pw) {
        // s <- KMACXOF256(pw, “”, 512, “K”); s <- 4s
        // W <- s*Z
        // (ke || ka) <- KMACXOF256(W_x, “”, 1024, “P”)
        // m <- KMACXOF256(ke, “”, |c|, “PKE”) xor c
        // t’ <- KMACXOF256(ka, m, 512, “PKA”)
        // accept if, and only if, t’ = t
        return new byte[]{};
    }

}
