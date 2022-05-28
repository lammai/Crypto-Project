import java.math.BigInteger;
import java.util.Arrays;

public class Signature {

    private static BigInteger h;
    private static BigInteger z;

    /** Generating a signature for a byte array m under passphrase pw:
     *
     * @param m
     * @param pw
     * @return
     */
    public static BigInteger[] generateSignature(byte[] m, String pw){

        // s <- KMACXOF256(pw, “”, 512, “K”); s <- 4s
        byte[] tempS = Symmetric.KMACXOF256(pw, new byte[]{}, 512, "K");
        byte[] sArr = new byte[65];
        System.arraycopy(tempS, 0, sArr, 1, tempS.length);
        BigInteger s = new BigInteger(sArr).multiply(BigInteger.valueOf(4L));

        // k <- KMACXOF256(s, m, 512, “N”); k <- 4k
        byte[] tempK = Symmetric.KMACXOF256(s.toString(), m, 512, "N");
        byte[] kArr = new byte[65];
        System.arraycopy(tempK, 0, kArr, 1, tempK.length);
        BigInteger k = new BigInteger(kArr).multiply(BigInteger.valueOf(4L));

        // U <- k*G
        E521 U = ECKey.G.multiply(k);

        // h <- KMACXOF256(U_x, m, 512, “T”); z <- (k – hs) mod r
        byte[] tempH = Symmetric.KMACXOF256(Symmetric.byteArrayToString(U.getX().toByteArray()), m, 512, "T");
        byte[] hArr = new byte[65];
        System.arraycopy(tempH, 0, hArr, 1, tempH.length);
        BigInteger h = new BigInteger(hArr).multiply(BigInteger.valueOf(4L));

        BigInteger z = k.subtract(h.multiply(s)).mod(E521.R);

        // signature: (h, z)
        return new BigInteger[]{h,z};

    }

    /** Verifying a signature (h, z) for a byte array m under the (Schnorr/ECDHIES) public key V:
     *
     * @param hz
     * @param m
     * @param V
     * @return
     */
    public static E521 verifySignature(BigInteger[] hz, byte[] m, E521 V) {
        BigInteger h = hz[0];
        BigInteger z = hz[1];

        // U <- z*G + h*V
        E521 U = ECKey.G.multiply(z).add( V.multiply(h) );

        // accept if, and only if, KMACXOF256(Ux, m, 512, “T”) = h
        byte[] tempH = Symmetric.KMACXOF256(Symmetric.byteArrayToString(U.getX().toByteArray()), m, 512, "T");

        return (Arrays.equals(tempH, h.toByteArray())) ? U : null;
    }

    /** Returns h
     *
     * @return
     */
    public static BigInteger getH() {
        return h;
    }

    /** Returns z
     *
     * @return
     */
    public static BigInteger getZ() {
        return z;
    }
}