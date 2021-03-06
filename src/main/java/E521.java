import java.math.BigInteger;
import java.util.Arrays;

/**
 * The implementation of Elliptic Curve security level P-521
 *
 * (1) NIST Documentation: https://nvlpubs.nist.gov/nistpubs/SpecialPublications/NIST.SP.800-185.pdf
 * (2) Elliptic Curve Slides (canvas): https://canvas.uw.edu/courses/1555586/files/91632405?wrap=1
 * (3) https://cryptobook.nakov.com/asymmetric-key-ciphers/elliptic-curve-cryptography-ecc
 * (4) https://eprint.iacr.org/2013/647.pdf
 * (5) https://fse.studenttheses.ub.rug.nl/10478/1/Marion_Dam_2012_WB_1.pdf
 *
 * @author Daniel Jiang
 * @author Lam Mai
 * @author David Shcherbina
 */
public class E521 {

    /** Elliptic X-Coordinate */
    private BigInteger X;
    /** Elliptic Y-Coordinate */
    private BigInteger Y;
    /** Elliptic curve initialization of D = -376014 */
    public static final BigInteger D = new BigInteger("-376014");
    /** Finite Field defined as of Mersenne prime????  */
    public static final BigInteger P = new BigInteger("2").pow(521).subtract(BigInteger.ONE);
    /** Number of possible positions on Edwards Elliptical Curve 521 */
    public static final BigInteger R = BigInteger.TWO.pow(519).subtract(new BigInteger("337554763258501705789107630418782636071904961214051226618635150085779108655765"));


    // --- Constructors ---

    /**
     * Create a point with BigInteger input
     * @param x the x coordinate of the input point
     * @param lsb the lease significant bit for y
     */
    public E521(BigInteger x, boolean lsb) {
        BigInteger upper = (BigInteger.ONE.subtract(x.pow(2))).mod(P); // 1 - x^2
        BigInteger lower = BigInteger.ONE.subtract(D.multiply(x.pow(2))).mod(P); // 1 - d * x^2
        BigInteger rad = upper.multiply(lower.modInverse(P));
        BigInteger sqrt = sqrt(rad, P, lsb); // sqrt( (1 - x^2) / (1 - dx^2)) mod p
        if (sqrt == null) throw new IllegalArgumentException("Error -> no sqrt for x");

        this.X = x;
        this.Y = sqrt;
    }

    /**
     * Create a point from another point
     * @param point the other point
     */
    public E521(E521 point){
        this.X = point.getX();
        this.Y = point.getY();
    }

    /**
     * Create a point from the specified x and y coordinates
     *
     * @param x x-coordinate of Elliptic curve
     * @param y y-coordinate of Elliptic curve
     */
    public E521(BigInteger x, BigInteger y){
        this.X = x;
        this.Y = y;
    }

    /**
     * Create a point from the specified x and y coordinates of type integer
     *
     * @param x x-coordinate of Elliptic curve
     * @param y y-coordinate of Elliptic curve
     */
    public E521(int x, int y){
        this.X = new BigInteger(x + "");
        this.Y = new BigInteger(y + "");
    }

    /**
     * Create a point from the specified x and y coordinates of type string
     *
     * @param x x-coordinate of Elliptic curve
     * @param y y-coordinate of Elliptic curve
     */
    public E521(String x, String y){
        this.X = new BigInteger(x);
        this.Y = new BigInteger(y);
    }

    /**
     * Create a point from only the specified x coordinate
     *
     * @param x x-coordinate of Elliptic curve
     */
    public E521(BigInteger x) {
        this.X = x;
        this.Y = new BigInteger("" + x.toString(2).charAt(x.toString(2).length()-1));
    }

    /**
     * Create a default point
     * Default Initialization Constructor, defined as (0,1).
     */
    public E521(){
        X = new BigInteger("0");
        Y = new BigInteger("1");
    }


    // --- Fetch Functions ---

    /** Returns E521 X-coordinate */
    public BigInteger getX(){
        return X;
    }
    /** Returns E521 Y-coordinate */
    public BigInteger getY(){
        return Y;
    }

    /** Returns # of Possible E521 points */
    public BigInteger getR() { return R; }

    /** Replaces E521 X-coordinate */
    public void setX(BigInteger tempX){
        this.X = tempX;
    }

    /** Replaces E521 Y-coordinate */
    public void setY(BigInteger tempY){
        this.Y = tempY;
    }

    public byte[] getBytes() {
        byte[] result = new byte[P.toByteArray().length * 2];
        byte[] x = X.toByteArray();
        byte[] y = Y.toByteArray();

        int xPos = (P.toByteArray().length * 2) / 2 - x.length;
        int yPos = result.length - y.length;

        if (X.signum() < 0)
            Arrays.fill(result, 0, xPos, (byte) 0xff);
        if (Y.signum() < 0)
            Arrays.fill(result, (P.toByteArray().length * 2) / 2, yPos, (byte) 0xff);

        System.arraycopy(x, 0, result, xPos, x.length);
        System.arraycopy(y, 0, result, yPos, y.length);

        return result;
    }

    public static E521 createFromBytes(byte[] input) {
        int bLen = P.toByteArray().length * 2;
        if (input.length != bLen)
            throw new IllegalArgumentException("Invalid input byte array. Input length: " + input.length + ". Expected length: " + bLen);

        BigInteger myX = new BigInteger(Arrays.copyOfRange(input, 0, bLen / 2 ));
        BigInteger myY = new BigInteger(Arrays.copyOfRange(input, bLen / 2, bLen ));

        return new E521(myX, myY);
    }

    // --- Support Functions ---

    /**
     * Equal Function determining if two E521 points
     *
     * @param that E521 point to be compared against
     * @return boolean dependent on if the E521 points were a direct match
     */
    public boolean equals(E521 that){
        return this.getX().equals(that.getX()) && this.getY().equals(that.getY());
    }

    @Override
    public String toString(){
        return "X: " + X.toString() + "\nY: " + Y.toString();
    }

    // --- Main Mod Functions ---

    /**
     * Adds two Edward Elliptical Points returning a new point in the curve.
     * Result Generated by the use of the function: (x1, y1) + (x2, y2) = [(x1y2 + y1x2) / (1 + d * x1x2y1y2)], [(y1y2 - x1x2) / (1 - d* x1x2y1y2)]
     *
     * @param that E521 elliptic point to be added against
     * @return new E521 elliptic point
     */
    public E521 add( E521 that) {

        BigInteger xTop = this.X.multiply(that.getY()).add(this.Y.multiply(that.getX())).mod(P);
        BigInteger xBottom = BigInteger.ONE.add(this.D.multiply(this.X).multiply(that.getX()).multiply(this.Y).multiply(that.getY())).mod(P);

        BigInteger yTop = this.Y.multiply(that.getY()).subtract(this.X.multiply(that.getX())).mod(P);
        BigInteger yBottom = BigInteger.ONE.subtract(this.D.multiply(this.X).multiply(that.getX()).multiply(this.Y).multiply(that.getY())).mod(P);

        return new E521( xTop.multiply(xBottom.modInverse(P)).mod(P) , yTop.multiply(yBottom.modInverse(P)).mod(P) );
    }


    /** Pseudocode Obtained from Prof. Paulo Barreto | Practical project ??? cryptographic library & app | PDF
     * Multiplies elliptic curve point by inputted __s__ times
     * While s = (sk sk-1 ... s1 s0)2, sk = 1.
     * @param s scalar
     * @return elliptic curve point multiplied by scalar __s__, (E521 * __s__).
     */
    public E521 multiply(BigInteger s) {
        E521 V = new E521(this); // initialize with sk*P, which is simply P
        String binaryS = s.toString(2);
        for (int i = s.bitLength()-2; i >= 0; i--) { // scan over the k bits of s
            V = V.add(V); // invoke the Edwards point addition formula
            if (s.testBit(i)) { // test the i-th bit of s
                V = V.add(this); // invoke the Edwards point addition formula
            }
        }

        return V; // now finally V = s*P
    }

    /**
     * Returns elliptic curve point with x-coordinate negated
     *
     * @return E521 with x-coordinate negated
     */
    public E521 oppositeX() {
        return new E521(getX().negate(), getY());
    }


    /** Function Obtained from Prof. Paulo Barreto | Practical project ??? cryptographic library & app | PDF
     * Compute a square root of v mod p with a specified
     * least significant bit, if such a root exists.
     *
     * @param v the radicand.
     * @param p the modulus (must satisfy p mod 4 = 3).
     * @param lsb desired least significant bit (true: 1, false: 0).
     * @return a square root r of v mod p with r mod 2 = 1 iff lsb = true
     * if such a root exists, otherwise null.
     */
    public static BigInteger sqrt(BigInteger v, BigInteger p, boolean lsb) {
        assert (p.testBit(0) && p.testBit(1)); // p = 3 (mod 4)
        if (v.signum() == 0) {
            return BigInteger.ZERO;
        }
        BigInteger r = v.modPow(p.shiftRight(2).add(BigInteger.ONE), p);
        if (r.testBit(0) != lsb) {
            r = p.subtract(r); // correct the lsb
        }
        return (r.multiply(r).subtract(v).mod(p).signum() == 0) ? r : null;
    }

    public static void main(String[] args) { // Testing grounds

        E521 P1 = new E521( 5,1);
        E521 P2 = new E521( 81,27);
        BigInteger Scalar = new BigInteger("3");


//        /* Binary Out Test */
//        E521 test =  new E521("12","12");
//        System.out.println(test.getX());
//        System.out.println(test.getY().toString(2));

        /* Addition Function Test */
        System.out.println("Add-Test: " + (
                P1.add(P2).getX().toString().equals("2848943765807482629183477481678102432902002030599138426052505330041298686806599138531723034324789743195949108355871816153913064193649104858922212607148447777")
                && P1.add(P2).getY().toString().equals("1657176835415392287630535516983614129346937053394953538541004268437386848690020119161827751833656395707282989268129894793328156544381017307355667412309055955")
                ? "True" : "False")
        );

        /* Multiplication Functions Test */
        System.out.println("Multi-Test pass: " + (
                P1.multiply(Scalar).getX().toString().equals("2190175985086284674807883421628324633994434845451391264241444635118399436584067233578389614597462274595277033789204415683178948147986352391820605682598462750")
                && P1.multiply(Scalar).getY().toString().equals("978777927561135706406857654800995606165082231641015472999709319933535556213711712126238098692979603264059415224153774141974204817859869601609457192739451618")
                ? "True" : "False"));


//        E521 test1 = new E521(2, 2);
//
//        E521 g = new E521(new BigInteger("4"));
//		System.out.println("G    -> " + g.getX().toString() + "      " + g.getY().toString());
//
//		// 0*G = O
//        E521 g_0 = g.multiply(BigInteger.ZERO);
//		System.out.println("G x 0    -> " + g_0.getX().toString() + "      " + g_0.getY().toString());
//
//		// 1*G = G
//        E521 g_1 = g.multiply(BigInteger.ONE);
//		System.out.println("G x 1    -> " + g_1.getX().toString() + "      " + g_1.getY().toString());
//
//		// 2*G = G + G
//        E521 g_2_mult = g.multiply(BigInteger.TWO);
//        E521 g_2_add = g.add(g);
//		System.out.println("G + G == G x 2    ->" + g_2_mult.equals(g_2_add));
//
//		// 4*G = 2*(2*G)
//        E521 g_4_mult = g.multiply(BigInteger.valueOf(4));
//        E521 g_2_2 = g_2_mult.multiply(BigInteger.TWO);
//		System.out.println("4 * G == 2*G x 2    ->" + g_2_2.equals(g_4_mult));
//
//		// 4*G ??? O
//		System.out.println("4 * G != 0    ->" + !g_4_mult.equals(new E521(BigInteger.ZERO, BigInteger.ONE)));
//
//		// r*G = O
//		System.out.println("r * G = 0    ->" + g.multiply(g.getR()).equals(new E521(BigInteger.ZERO, BigInteger.ONE)));
//        // Gl if you're taking this class lel
    }

}


