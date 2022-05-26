import java.math.BigInteger;

public class ECfunc {

    public static final E521 G = new E521();

    public static byte[] genSchnorrKeyPair(String pw) {
        // s <- KMACXOF256(pw, “”, 512, “K”);
        byte[] tempS = Symmetric.KMACXOF256(pw, new byte[]{}, 512, "K");
        byte[] s = new byte[65];
        System.arraycopy(tempS, 0, s, 1, tempS.length);

        // s <- 4s
        BigInteger sBigInt = new BigInteger(s);
        BigInteger sMultFour = sBigInt.multiply(BigInteger.valueOf(4L));

        // V <- s*G
        // Note: s*G is multiplication of the scalar factor s by curve point G


        // key pair: (s, V)
        return new byte[]{};
    }

}
