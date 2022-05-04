import java.math.BigInteger;
import java.util.Arrays;

public class Utils {

    /**
     * Define the type of encoding
     */
    private enum encodeType {
        Left,
        Right
    }

    /**
     * Implements the NIST left_encode and right_encode functions.
     * @param x The input integer of type BigInteger
     * @param type The type of encoding (Left or Right).
     * @return the right encoded byte string representation of the integer input.
     */
    private static byte[] encode(BigInteger x, encodeType type) {
        byte[] input = x.toByteArray();
        byte[] addBytes = BigInteger.valueOf(input.length).toByteArray();
        byte[] result = new byte[input.length + addBytes.length];

        if (type == encodeType.Left)
            byteConcat(addBytes, input, result);
        else
            byteConcat(input, addBytes, result);
        return result;
    }

    /**
     * Credits goes to Professor Paulo Barreto for the course TCSS 487.
     * This function is taken from his lecture.
     *
     * Prepends an encoding of the integer w to an input string X, then pads the result
     * with zeros until it is a byte string whose length in bytes is a multiple of w.
      * @param x input byte array to bytepad.
     * @param w the encoding factor (the output length must be a multiple of w)
     * @return the byte-padded byte array X with encoding factor w.
     */
    private static byte[] bytepad(byte[] x, int w) {
        // Validity Conditions: w > 0
        assert w > 0;

        //1. z = left_encode(w) || X.
        byte[] wenc = encode(BigInteger.valueOf(w), encodeType.Left);
        byte[] z = new byte[w * ((wenc.length + x.length + w - 1)/w)];

        byteConcat(wenc, x, z);
        //2. while len(z) mod 8 ≠ 0:
        //  z = z || 0
        //3. while (len(z)/8) mod w ≠ 0:
        //  z = z || 00000000
        // might not need this loop since array is prefilled with 0
        for (int i = wenc.length + x.length; i < z.length; i++) {
            z[i] = (byte) 0;
        }
        //4. return z.
        return z;
    }

    /**
     * Encode bit strings in a way that may be parsed unambiguously from the beginning
     * of the string, s.
     * @param s The string input to be encoded.
     * @return the encoded byte array.
     */
    private static byte[] encode_string(String s) {
        byte[] leftEncLength = encode(BigInteger.valueOf(s.length()), encodeType.Left);
        byte[] result = new byte[leftEncLength.length + s.length()];
        byteConcat(leftEncLength, s.getBytes(), result);
        return result;
    }

    /**
     * @param a The first array to add to the result
     * @param b The second array to add to the result
     * @param result The result array of the concatenation of a and b
     */
    private static void byteConcat(byte[] a, byte[] b, byte[] result) {
       System.arraycopy(a, 0, result, 0, a.length);
       System.arraycopy(b, 0, result, a.length, b.length);
    }

    public static void main(String[] args) {
        // Test supporting methods
//        byte[] input = {0,1,2,3};
//        int encodingFactor = 8;
//        System.out.println(Arrays.toString(bytepad(input, encodingFactor)));
//
//        System.out.println("left encode: " + Arrays.toString(encode(new BigInteger("1061246"), encodeType.Left)));
//        System.out.println("right encode: " + Arrays.toString(encode(new BigInteger("0"), encodeType.Right)));

        String test = "bruh what duh phuck";
        System.out.println(Arrays.toString(encode_string(test)));
        System.out.println(Arrays.toString(encode_string("")));
    }
}
