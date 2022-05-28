import java.math.BigInteger;

public class ECKey {

    public static final E521 G = new E521(BigInteger.valueOf(4L), false);

    private final E521 V;

    private final byte[] s;

    private final BigInteger s_bigInt;

    /**
     * Generating a (Schnorr/ECDHIES) key pair from passphrase pw:
     * @param pw
     */
    public ECKey(byte[] pw) {
        // s <- KMACXOF256(pw, “”, 512, “K”);
        byte[] tempS = Symmetric.KMACXOF256(Symmetric.byteArrayToString(pw), new byte[]{}, 512, "K");
        byte[] sArr = new byte[65];
        System.arraycopy(tempS, 0, sArr, 1, tempS.length);

        // s <- 4s
        s_bigInt = new BigInteger(sArr).multiply(BigInteger.valueOf(4L));

        // V <- s*G
        // Note: s*G is multiplication of the scalar factor s by curve point G
        s = s_bigInt.toByteArray();
        V = G.multiply(s_bigInt);
        // key pair: (s, V)
    }

    /**
     * Encrypting a byte array m under the (Schnorr/ECDHIES) public key V:
     * @param privateKey
     */
    public ECKey(BigInteger privateKey) {
        s_bigInt = privateKey;
        s = s_bigInt.toByteArray();
        V = G.multiply(s_bigInt);
    }

    public ECKey(String pw) {
        this(pw.getBytes());
    }

    public E521 getPublicKey() { return V; }

    public BigInteger getS_Scalar() { return s_bigInt; }
}
