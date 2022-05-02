import javax.print.attribute.standard.PresentationDirection;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;

public class Utils {
    /**
     * Encodes the integer x as a byte string in a way that can be
     * unambiguously parsed from the beginning of the string by inserting the length
     * of the byte string before the byte string representation of x.
     * @param x The input integer of type BigInteger.
     * @return The left encoded byte string representation of the integer input.
     */
    private static byte[] left_encode(BigInteger x) {
        //Validity Conditions: 0 ≤ x < 2^2040
        //1. Let n be the smallest positive integer for which 2^8n > x.
        //2. Let x_1, x_2, …, xn be the base-256 encoding of x satisfying:
        //  x = ∑ 2^(8(n-i)) * x_i, for i = 1 to n.
        //3. Let O_i = enc8(x_i), for i = 1 to n.
        //4. Let O_0 = enc8(n).
        //5. Return O = O_0 || O_1 || … || O_n−1 || O_n.
        byte[] input = x.toByteArray();
        byte[] addBytes = BigInteger.valueOf(input.length).toByteArray();
        byte[] result = new byte[input.length + addBytes.length];

//      Copy all from addBytes to result starting at 0
        System.arraycopy(addBytes, 0, result, 0, addBytes.length);
//      Copy all from input to result starting at addBytes.length
        System.arraycopy(input, 0, result, addBytes.length, input.length);
        return result;
    }

    /**
     * Encodes the integer x as a byte string in a way that can be unambiguously parsed
     * from the end of the string by inserting the length of the byte string after the
     * byte string representation of x.
     * @param x The input integer of type BigInteger
     * @return the right encoded byte string representation of the integer input.
     */
    private static byte[] right_encode(BigInteger x) {
//        Validity Conditions: 0 ≤ x < 2^(2040)
//        1. Let n be the smallest positive integer for which 2^(8n) > x.
//        2. Let x_1, x_2,…, x_n be the base-256 encoding of x satisfying:
//        x = ∑ 2^(8(n-i))x_i, for i = 1 to n.
//        3. Let Oi = enc8(x_i), for i = 1 to n.
//        4. Let O_(n+1) = enc8(n).
//        5. Return O = O_1 || O_2 || … || O_n || O_(n+1).
        byte[] input = x.toByteArray();
        byte[] addBytes = BigInteger.valueOf(input.length).toByteArray();
        byte[] result = new byte[input.length + addBytes.length];

//      Copy all from input to result starting at 0
        System.arraycopy(input, 0, result, 0, input.length);
//      Copy all from addBytes to result starting at input.length
        System.arraycopy(addBytes, 0, result, input.length, addBytes.length);
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
        byte[] wenc = left_encode(BigInteger.valueOf(w));
        byte[] z = new byte[w * ((wenc.length + x.length + w - 1)/w)];

//        z = byteConcat(wenc, x, z);
        System.arraycopy(wenc, 0, z, 0, wenc.length);
        System.arraycopy(x, 0, z, wenc.length, x.length);
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
     * @param a The first array to add to the result
     * @param b The second array to add to the result
     * @param result The result array of the concatenation of a and b
     * @return Thr result array
     */
    private static byte[] byteConcat(byte[] a, byte[] b, byte[] result) {
       System.arraycopy(a, 0, result, 0, a.length);
       System.arraycopy(b, 0, result, a.length, b.length);

       return result;
    }

    public static void main(String[] args) {
        // Test supporting methods
        byte[] input = {0,1,2,3};
        int encodingFactor = 8;
        System.out.println(Arrays.toString(bytepad(input, encodingFactor)));

        System.out.println("left encode: " + Arrays.toString(left_encode(new BigInteger("0"))));
        System.out.println("right encode: " + Arrays.toString(right_encode(new BigInteger("0"))));
    }
}
