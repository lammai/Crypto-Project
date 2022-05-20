import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Source: https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-185.pdf
 * for left_encode, right_encode, encode_string, bytepad
 */

public class Symmetric {

    public static byte[] cSHAKE256(byte[] inX, int lenL, String funcN, String customS) {
        if (funcN.equals("") && customS.equals("")) return SHAKE256(inX, lenL);

        byte[] processIn = byteConcat(encode_string(funcN), encode_string(customS));
        processIn = byteConcat(bytepad(processIn, 136), inX);
        processIn = byteConcat(processIn, new byte[] {0x04});

        return sponge(processIn, lenL, 512);
    }

    public static byte[] SHAKE256(byte[] in, int len) {
        byte[] newIn = Arrays.copyOf(in, in.length + 1);
        int bytesToPad = 136 - in.length % (136);
        newIn[in.length] = bytesToPad == 1 ? (byte) 0x9f : 0x1f;
        return sponge(newIn, len, 512);
    }

    private static final long[] rConst = {
            0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL,
            0x8000000080008000L, 0x000000000000808BL, 0x0000000080000001L,
            0x8000000080008081L, 0x8000000000008009L, 0x000000000000008aL,
            0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
            0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L,
            0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L,
            0x000000000000800aL, 0x800000008000000aL, 0x8000000080008081L,
            0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
    };

    private static final int[] rotOffset = {
            1,  3,  6,  10, 15, 21, 28, 36, 45, 55, 2,  14,
            27, 41, 56, 8,  25, 43, 62, 18, 39, 61, 20, 44
    };

    private static final int[] piLane = {
            10, 7,  11, 17, 18, 3, 5, 16, 8,  21, 24, 4,
            15, 23, 19, 13, 12, 2, 20, 14, 22, 9,  6,  1
    };

    private static BigInteger rotLeft64(BigInteger x, int offset) {
        long newX = x.longValue();
        int ofs = offset % 64;
        return BigInteger.valueOf(newX << ofs | (newX >>> (Long.SIZE - ofs)));
//        return (x.shiftLeft(ofs)).or(x.shiftRight(Long.SIZE - ofs));
    }

    private static int floorLog(int n) {
        if (n < 0) throw new IllegalArgumentException("Log undefined for negative number");
        int exp = -1;
        while (n > 0) {
            n = n >>> 1;
            exp++;
        }
        return exp;
    }


    // --------------------------------SPONGE------------------------------------------
    // --------------------------------------------------------------------------------------

    private static byte[] sponge(byte[] in, int bitLen, int cap) {
        int rate = 1600 - cap;
        byte[] pad = in.length % (rate / 8) == 0 ? in : padTenOne(rate, in);
        BigInteger[][] states = byteArrToStates(pad, cap);
        BigInteger[] stcml = Stream.generate(() -> BigInteger.ZERO)
                .limit(25).toArray(BigInteger[]::new);

        for (BigInteger[] st : states) {
            stcml = keccakp(xorStates(stcml, st), 1600, 24);
        }
        
        BigInteger[] result = new BigInteger[0];
        int offset = 0;
        do {
            result = Arrays.copyOf(result, offset + rate/64);
            System.arraycopy(stcml, 0, result, offset, rate/64);
            offset += rate/64;
            stcml = keccakp(stcml, 1600, 24);
        } while (result.length * 64 < bitLen);

        return stateToByteArray(result, bitLen);
    }

    private static BigInteger[] xorStates(BigInteger[] s1, BigInteger[] s2) {
        BigInteger[] result = Stream.generate(() -> BigInteger.ZERO).limit(25).toArray(BigInteger[]::new);
        for (int i = 0 ; i < s1.length; i++) {
            result[i] = s1[i].xor(s2[i]);
        }
        return result;
    }

    private static BigInteger[][] byteArrToStates(byte[] in, int cap) {
        BigInteger[][] states = new BigInteger[(in.length * 8)/(1600 - cap)][25];
        int offset = 0;
        for (int i = 0; i < states.length; i++) {
            BigInteger[] state = Stream.generate(() -> BigInteger.ZERO).limit(25).toArray(BigInteger[]::new);
            for (int j = 0; j < (1600 - cap)/64; j++) {
                state[j] = bytesToWord(offset, in);
                offset += 8;
            }
            states[i] = state;
        }
        return states;
    }

    private static byte[] stateToByteArray(BigInteger[] state, int bitLen) {
        if (state.length * 64 < bitLen) throw new IllegalArgumentException("State length is insufficient for desired bit length");
        byte[] result = new byte[bitLen/8];
        int wrdInd = 0;
        while (wrdInd * 64 < bitLen) {
            BigInteger word = state[wrdInd++];
            int fill = wrdInd * 64 > bitLen ? (bitLen - (wrdInd - 1) * 64) / 8 : 8;
            for (int b = 0; b < fill; b++) {
                byte convert = word.shiftRight(8*b).and(BigInteger.valueOf(0xFF)).byteValue();
                result[(wrdInd - 1) * 8 + b] = convert;
            }
        }
        return result;
    }

    private static BigInteger bytesToWord(int offset, byte[] in) {
        if (in.length < offset + 8) throw new IllegalArgumentException("Byte range unreachable, index out of range");
        BigInteger word = BigInteger.ZERO;
        for (int i = 0; i < 8; i++) {
            long temp = (((long)in[offset + i]) & 0xff)<<(8*i);
            word = word.add(BigInteger.valueOf(temp));
        }

        return word;
    }

    /**
     * The 10*1 padding scheme that follows FIPS 202 specification.
     *
     * @param in the byte array to pad
     * @param rate the result will be a positive multiple of rate (in terms of bit length)
     * @return the padded byte array
     */
    private static byte[] padTenOne(int rate, byte[] in) {
        int bytesToPad = (rate / 8) - in.length % (rate / 8);
        byte[] padded = new byte[in.length + bytesToPad];
        for (int i = 0; i < in.length + bytesToPad; i++) {
            if (i < in.length) padded[i] = in[i];
            else if (i==in.length + bytesToPad - 1) padded[i] = (byte) 0x80;
            else padded[i] = 0;
        }
        return padded;
    }

    // --------------------------------KECCAK STUFF------------------------------------------
    // --------------------------------------------------------------------------------------

    /**
     * The Keccak-p permutation following NIST FIPS 202 Section 3.
     * @param stateIn the input state, an array of 25 BigIntegers.
     * @return the state after applying the Keccak-p permutation.
     */
    private static BigInteger[] keccakp(BigInteger[] stateIn, int bitLen, int rounds) {
        BigInteger[] stateOut = stateIn.clone();
        int l = floorLog(bitLen/25);
        for (int i = 12 + 2*l - rounds; i < 12 + 2*l; i++) {
            stateOut = iota(chi(rhoPhi(theta(stateOut))), i);
        }
        return stateOut;
    }

    private static BigInteger[] theta(BigInteger[] stateIn) {
        BigInteger[] stateOut = Stream.generate(() -> BigInteger.ZERO).limit(25).toArray(BigInteger[]::new);
        BigInteger[] c = Stream.generate(() -> BigInteger.ZERO).limit(5).toArray(BigInteger[]::new);

        for (int i = 0; i < 5; i++) {
            c[i] = stateIn[i].xor(stateIn[i+5]).xor(stateIn[i+10]).xor(stateIn[i+15]).xor(stateIn[i+20]);
        }

        for (int i = 0; i < 5; i++) {
            BigInteger t = c[(i+4) % 5].xor(rotLeft64(c[(i+1) % 5], 1));

            for (int j = 0; j < 5; j++) {
                stateOut[i + 5*j] = stateIn[i + 5*j].xor(t);
            }
        }
        return stateOut;
    }

    private static BigInteger[] rhoPhi(BigInteger[] stateIn) {
        BigInteger[] stateOut = Stream.generate(() -> BigInteger.ZERO).limit(25).toArray(BigInteger[]::new);
        stateOut[0] = stateIn[0];
        BigInteger t = stateIn[1];
        BigInteger temp;
        int j;

        for (int i = 0; i < 24; i++) {
            j = piLane[i];
            temp = stateIn[j];
            stateOut[j] = rotLeft64(t, rotOffset[i]);
            t = temp;

        }
        return stateOut;
    }

    private static BigInteger[] chi(BigInteger[] stateIn) {
        BigInteger[] stateOut = Stream.generate(() -> BigInteger.ZERO).limit(25).toArray(BigInteger[]::new);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                BigInteger temp = (stateIn[(i+1) % 5 + 5 * j].not()).and(stateIn[(i+2) % 5 + 5*j]);
                stateOut[i + 5*j] = stateIn[i + 5*j].xor(temp);
            }
        }
        return stateOut;
    }

    private static BigInteger[] iota(BigInteger[] stateIn, int round) {
        stateIn[0] = stateIn[0].xor(BigInteger.valueOf(rConst[round]));
        return stateIn;
    }

    private static void endian_Convert(BigInteger[] stateIn) {
        for (int i = 0; i < 25; i++) {
            byte[] current = stateIn[i].toByteArray();
            byte[] rev = new byte[current.length];

            for (int x = 0; x < current.length; x++) {
                rev[x] = current[current.length - x - 1];
            }
            stateIn[i] = new BigInteger(rev);
        }
    }


    // ---------------------------Supporting methods below-----------------------------------
    // --------------------------------------------------------------------------------------

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

        if (x.compareTo(BigInteger.ZERO) == 0) return new byte[] {1, 0};

        byte[] buffer = new byte[8];
        long lenCopy = x.longValue();
        int count = 0;
        while (lenCopy > 0) {
            byte b = (byte) (lenCopy & 0xffL);
            lenCopy = lenCopy >>> (8);
            buffer[7 - count++] = b;
        }
        byte[] result = new byte[count + 1];
        System.arraycopy(buffer, 8 - count, result, 1, count);
        result[0] = (byte) count;
        return result;
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

        if (x.compareTo(BigInteger.ZERO) == 0) return new byte[] {0, 1};

        byte[] buffer = new byte[8];
        long lenCopy = x.longValue();
        int count = 0;
        while (lenCopy > 0) {
            byte b = (byte) (lenCopy & 0xffL);
            lenCopy = lenCopy >>> (8);
            buffer[7 - count++] = b;
        }
        byte[] result = new byte[count + 1];
        System.arraycopy(buffer, 8 - count, result, 0, count);
        result[result.length - 1] = (byte) count;
        return result;
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

        byte[] inputLength = left_encode(BigInteger.valueOf(s.length() * 8L));
        byte[] addBytes = s.getBytes();

        return byteConcat(inputLength,addBytes);
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
            z[i] = (byte)0;
        }

        return z;
    }

    /**
     * Concatenate two byte arrays.
     *
     * @param a The first array to add to the result
     * @param b The second array to add to the result
     * @return The result array of the concatenation of a and b
     */
    private static byte[] byteConcat(byte[] a, byte[] b) {
        byte[] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static String byteToHexString(byte[] in) {
        StringBuilder result = new StringBuilder();
        for (byte b : in)
            result.append(String.format("%02X ", b));
        return result.toString();
    }

    public static void main(String[] args) {

        // Testing Left Encode
        byte[] left = left_encode(new BigInteger("0"));
        System.out.println(" Left Encode  (by bytes): " + Arrays.toString(left));
        System.out.print(" Left Encode  (by bits): ");
        for (byte b : left) {
            System.out.print(Integer.toBinaryString(b & 255 | 256).substring(1) + " ");
        }

        System.out.println();

        // Testing Right Encode
        byte[] right = right_encode(new BigInteger("0"));
        System.out.println("Right Encode  (by bytes): " + Arrays.toString(right));
        System.out.print("Right Encode  (by bits): ");
        for (byte b : right) {
            System.out.print(Integer.toBinaryString(b & 255 | 256).substring(1) + " ");
        }

        System.out.println();

        // Testing Encode String -> can make the string = "A", "AB", or "ABC" for testing
        byte[] encode = encode_string("");
        System.out.println("Encode String (by bytes): " + Arrays.toString(encode));
        System.out.print("Encode String (by bits): ");
        for (byte b : encode) {
            System.out.print(Integer.toBinaryString(b & 255 | 256).substring(1) + " ");
        }

        System.out.println();
        System.out.println();

        // Testing sponge
        byte[] test = {24, 1, 2, 3,7,5,43,3,2,5,5,6,3,2,2,3,4};
        System.out.println("Sponge Result: ");
        System.out.println(Arrays.toString(sponge(test, 128, 0)));
        System.out.println("Expected: ");
        System.out.println("[-64, -101, 23, 67, -113, -72, -62, 71, -72, -72, 5, 33, -108, 28, 90, 126]");
        System.out.println();

        // Testing SHAKE256
        byte[] shakeOut = SHAKE256(test, 512);
        System.out.println("SHAKE RESULT:");
        System.out.println(Arrays.toString(shakeOut));
        System.out.println("EXPECTED:");
        System.out.println("[-49, 27, -48, -99, -63, 122, -91, 126, -95, -54, -12, 44, 95, 37, -100, 19, -74, -65, -75, -7, -52, -10, -42, 107, -42, -75, -24, -37, -99, -78, 12, -49, 1, 94, -20, 95, -84, -33, 21, 12, 51, -6, 13, 27, 115, 84, 110, 9, 70, 81, -98, 123, -18, 31, 1, -49, -94, -93, 86, 94, 113, 8, -64, 50]");
        System.out.println();

        // Testing cSHAKE256
        byte[] testVector = {0,1,2,3};
        byte[] cShakeResult = cSHAKE256(testVector, 512, "", "Email Signature");
        System.out.println("cSHAKE RESULT:");
        System.out.println(byteToHexString(cShakeResult));
        System.out.println("EXPECTED (cSHAKE Sample #3):");
        System.out.println("D0 08 82 8E 2B 80 AC 9D 22 18 FF EE 1D 07 0C 48 B8 E4 C8 7B FF 32 C9 69 9D 5B 68 96 EE E0 ED D1 64 02 0E 2B E0 56 08 58 D9 C0 0C 03 7E 34 A9 69 37 C5 61 A7 4C 41 2B B4 C7 46 46 95 27 28 1C 8C");
        System.out.println();
    }
}
