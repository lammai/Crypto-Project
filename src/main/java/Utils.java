public class Utils {

    // TODO
    /* left_encode(x) encodes the integer x as a byte string in a way that can be
    unambiguously parsed from the beginning of the string by inserting the length
    of the byte string before the byte string representation of x. */
    private static byte[] left_encode(int x) {
        //Validity Conditions: 0 ≤ x < 2^2040
        //1. Let n be the smallest positive integer for which 2^8n > x.
        //2. Let x_1, x_2, …, xn be the base-256 encoding of x satisfying:
        //  x = ∑ 2^(8(n-i)) * x_i, for i = 1 to n.
        //3. Let O_i = enc8(x_i), for i = 1 to n.
        //4. Let O_0 = enc8(n).
        //5. Return O = O_0 || O_1 || … || O_n−1 || O_n.
        return new byte[0];
    }

    // TODO
    private static byte[] bytepad(byte[] x, int w) {
        // Validity Conditions: w > 0
        assert w > 0;

        //1. z = left_encode(w) || X.
        //2. while len(z) mod 8 ≠ 0:
        //  z = z || 0
        //3. while (len(z)/8) mod w ≠ 0:
        //  z = z || 00000000
        //4. return z.
        return new byte[0];
    }
}
