import java.math.BigInteger;

/**
 * Implementation of the Elliptic Curve Key pair generation and encryption.
 * Pseudocode to perform these functionalities are provided by
 * Prof. Paulo Barreto in the TCSS 487 Cryptography course
 *
 * @author Daniel Jiang
 * @author Lam Mai
 * @author David Shcherbina
 */
public class ECKey {

    /**
     * Static point
     */
    public static final E521 G = new E521(BigInteger.valueOf(4L), false);

    /**
     * Public key
     */
    private final E521 V;

    /**
     * Private key of type byte array
     */
    private final byte[] s;

    /**
     * Private key of type BigInteger
     */
    private final BigInteger s_bigInt;

    /**
     * Generating a (Schnorr/ECDHIES) key pair from passphrase pw:
     * @param pw the passphrase
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
     * @param privateKey the private key
     */
    public ECKey(BigInteger privateKey) {
        s_bigInt = privateKey;
        s = s_bigInt.toByteArray();
        V = G.multiply(s_bigInt);
    }

    /**
     * Get the public key V
     * @return the public key
     */
    public E521 getPublicKey() { return V; }

    /**
     * Get the private key S
     * @return private key of type BigInteger
     */
    public BigInteger getS_Scalar() { return s_bigInt; }
}
