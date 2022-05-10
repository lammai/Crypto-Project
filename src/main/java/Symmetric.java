import java.math.BigInteger;
import java.util.Arrays;

/**
 * Source: https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-185.pdf
 * for left_encode, right_encode, encode_string, bytepad
 */

public class Symmetric {

     /**
     * left_encode(x) encodes the integer x as a byte string in a way that can be
     * unambiguously parsed from the beginning of the string by inserting the length of the
     * byte string before the byte string representation of x.
     * 
     * As an example, left_encode(0) will yield 10000000 00000000.
     * 
     * @param x The input integer of type BigInteger int
     * @return The left encoded byte string representation of the input x
     */
    private static byte[] left_encode(BigInteger x) {
        // Validity conditions: 0 <= x < 2^(2040)
        // 1. Let n be the smallest positive integer for which 2^(8n) > x.
        // 2. Let x1, x2, …, xn be the base-256 encoding of x satisfying:
        //        x = ∑ 2^(8(n-i))xi, for i = 1 to n.
        // 3. Let Oi = enc8(xi), for i = 1 to n.
        // 4. Let O0 = enc8(n).
        // 5. Return O = O0 || O1 || … || On−1 || On.
        
        assert (x.compareTo(BigInteger.ZERO) >= 0);
        assert (x.compareTo(BigInteger.TWO.pow(2040)) < 0);

        byte[] input = x.toByteArray();
        byte[] addBytes = BigInteger.valueOf(input.length).toByteArray();
        byte[] output = new byte[addBytes.length + input.length];

        System.arraycopy(addBytes, 0, output, 0, addBytes.length);
        System.arraycopy(input, 0, output, addBytes.length, input.length);
        
        return output;
    }

    /**
     * right_encode(x) encodes the integer x as a byte string in a way that can be
     * unambiguously parsed from the end of the string by inserting the length of the byte
     * string after the byte string representation of x.
     * 
     * As an example, right_encode(0) will yield 00000000 10000000.
     * 
     * @param x The input integer of type int
     * @return The right encoded byte string representation of the input x
     */
    private static byte[] right_encode(BigInteger x) {
        // Validity conditions: 0 <= x < 2^(2040)
        // 1. Let n be the smallest positive integer for which 2^(8n) > x.
        // 2. Let x1, x2,…, xn be the base-256 encoding of x satisfying:
        //        x = ∑ 28(n-i)xi, for i = 1 to n.
        // 3. Let Oi = enc8(xi), for i = 1 to n.
        // 4. Let On+1 = enc8(n).
        // 5. Return O = O1 || O2 || … || On || On+1.

        assert (x.compareTo(BigInteger.ZERO) >= 0);
        assert (x.compareTo(BigInteger.TWO.pow(2040)) < 0);

        byte[] input = x.toByteArray();
        byte[] addBytes = BigInteger.valueOf(input.length).toByteArray();
        byte[] output = new byte[input.length + addBytes.length];

        System.arraycopy(input, 0, output, 0, input.length);
        System.arraycopy(addBytes, 0, output, input.length, addBytes.length);

        return output;
    }

    /**
     * The encode_string function is used to encode bit strings in a way that may be parsed
     * unambiguously from the beginning of the string, s.
     * 
     * Note that if the bit string s is not byte-oriented
     * (i.e., len(s) is not a multiple of 8),
     * the bit string returned from encode_string(s) is also not byte-oriented.
     * However, if len(s) is a multiple of 8, then the length of the output of
     * encode_string(s) will also be a multiple of 8.
     * 
     * As an example, encode_string(S) where S is the empty string "" will yield 10000000
     * 00000000.
     * 
     * @param s The input bit string
     * @return The left encoded byte string from the length of the input bit string
     */
    private static byte[] encode_string(String s) {
        // Validity Conditions: 0 ≤ len(s) < 2^(2040)
        // 1. Return left_encode(len(s)) || s.

        assert s.length() >= 0;
        BigInteger lenS = BigInteger.valueOf(s.length());
        assert (lenS.compareTo(BigInteger.TWO.pow(2040)) < 0);

        byte[] inputLength = left_encode(BigInteger.valueOf(s.length() * 8));
        byte[] addBytes = new byte[s.length()];
        for (int i = 0; i < addBytes.length; i++) {
            addBytes[i] = (byte) s.charAt(i);
        }
        byte[] output = new byte[inputLength.length + addBytes.length];

        System.arraycopy(inputLength, 0, output, 0, inputLength.length);
        System.arraycopy(addBytes, 0, output, inputLength.length, addBytes.length);

        return output;
    }

    
    /**
     * The bytepad(X, w) function prepends an encoding of the integer w to an input string
     * X, then pads the result with zeros until it is a byte string whose length in bytes
     * is a multiple of w. In general, bytepad is intended to be used on encoded
     * strings—the byte string bytepad(encode_string(s), w) can be parsed unambiguously
     * from its beginning, whereas bytepad does not provide unambiguous padding for all
     * input strings.
     * 
     * @param X the byte array to bytepad
     * @param w the encoding factor (the output length must be a multiple of w)
     * @return the byte-padded byte array X with encoding factor w.
     */
    private static byte[] bytepad(byte[] X, int w) {
        // Validity Conditions: w > 0
        // 1. z = left_encode(w) || X
        // 2. while len(z) mod 8 != 0:
        //        z = z || 0
        // 3. while (len(z) / 8) mod w != 0:
        //        z = z || 00000000
        // 4. return z

        assert w > 0;

        byte[] wenc = left_encode(BigInteger.valueOf(w));
        byte[] z = new byte[w * ((wenc.length + X.length + w - 1) / w)];

        System.arraycopy(wenc, 0, z, 0, wenc.length);
        System.arraycopy(X, 0, z, wenc.length, X.length);

        for (int i = wenc.length + X.length; i < z.length; i++) {
            z[i] = (byte) 0;
        }

        return z;
    }

    public static void main(String[] args) {

        // Testing Left Encode
        System.out.println(" Left Encode (by bytes): " + Arrays.toString(left_encode(new BigInteger("0"))));
        byte[] left = left_encode(new BigInteger("0"));
        System.out.print(" Left Encode (by bits): ");
        for (byte b : left) {
            System.out.print(Integer.toBinaryString(b & 255 | 256).substring(1) + " ");
        }

        System.out.println();

        // Testing Right Encode
        System.out.println("Right Encode (by bytes): " + Arrays.toString(right_encode(new BigInteger("0"))));
        byte[] right = right_encode(new BigInteger("0"));
        System.out.print("Right Encode (by bits): ");
        for (byte b : right) {
            System.out.print(Integer.toBinaryString(b & 255 | 256).substring(1) + " ");
        }

        System.out.println();

        // Testing Encode String
        System.out.println("Encode String (by bytes): " + Arrays.toString(encode_string("ABC")));
        byte[] encode = encode_string("ABC");
        System.out.print("Encode String (by bits): ");
        for (byte b : encode) {
            System.out.print(Integer.toBinaryString(b & 255 | 256).substring(1) + " ");
        }

        System.out.println();
    }
}
