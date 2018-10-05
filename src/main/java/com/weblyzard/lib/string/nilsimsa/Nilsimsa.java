package com.weblyzard.lib.string.nilsimsa;

import java.util.*;
import javax.xml.bind.DatatypeConverter;

/**
 * Computes the Nilsimsa hash for the given string.
 *
 * <p>This class is based on the Python implementation by Michael Itz
 * <http://code.google.com/p/py-nilsimsa>.
 *
 * <p>Original C nilsimsa-0.2.4 implementation by cmeclax:
 * <http://ixazon.dynip.com/~cmeclax/nilsimsa.html>
 *
 * @author Albert Weichselbraun
 */
public class Nilsimsa {

    private int count = 0; // num characters seen
    private int[] acc = new int[256]; // accumulators for computing the digest
    private int[] lastch = new int[4]; // the last four seen characters
    private byte[] digest = null; // the Nilsimsa digest

    // pre-defined transformation arrays
    private static final byte[] TRAN =
            DatatypeConverter.parseHexBinary(
                    "02D69E6FF91D04ABD022161FD873A1AC"
                            + "3B7062961E6E8F399D05144AA6BEAE0E"
                            + "CFB99C9AC76813E12DA4EB518D646B50"
                            + "23800341ECBB71CC7A867F98F2365EEE"
                            + "8ECE4FB832B65F59DC1B314C7BF06301"
                            + "6CBA07E81277493CDA46FE2F791C9B30"
                            + "E300067E2E0F383321ADA554CAA729FC"
                            + "5A47697DC595B5F40B90A3816D255535"
                            + "F575740A26BF195C1AC6FF995D84AA66"
                            + "3EAF78B32043C1ED24EAE63F18F3A042"
                            + "57085360C3C0834082D709BD442A67A8"
                            + "93E0C2569FD9DD8515B48A27289276DE"
                            + "EFF8B2B7C93D45944B110D65D5348B91"
                            + "0CFA87E97C5BB14DE5D4CB10A21789BC"
                            + "DBB0E2978852F748D3612C3A2BD18CFB"
                            + "F1CDE46AE7A9FDC437C8D2F6DF58724E");

    public Nilsimsa() {
        reset();
    }

    /**
     * Updates the Nilsimsa digest using the given String
     *
     * @param data the data to consider in the update
     */
    public Nilsimsa update(byte[] data) {
        for (int ch : data) {
            ch = ch & 0xff;
            count++;

            // incr accumulators for triplets
            if (lastch[1] > -1) {
                acc[tran3(ch, lastch[0], lastch[1], 0)]++;
            }
            if (lastch[2] > -1) {
                acc[tran3(ch, lastch[0], lastch[2], 1)]++;
                acc[tran3(ch, lastch[1], lastch[2], 2)]++;
            }
            if (lastch[3] > -1) {
                acc[tran3(ch, lastch[0], lastch[3], 3)]++;
                acc[tran3(ch, lastch[1], lastch[3], 4)]++;
                acc[tran3(ch, lastch[2], lastch[3], 5)]++;
                acc[tran3(lastch[3], lastch[0], ch, 6)]++;
                acc[tran3(lastch[3], lastch[2], ch, 7)]++;
            }

            // adjust lastch
            for (byte i = 3; i > 0; i--) {
                lastch[i] = lastch[i - 1];
            }
            lastch[0] = ch;
        }
        digest = null;
        return this;
    }

    public Nilsimsa update(String s) {
        return update(s.getBytes());
    }

    /** resets the Hash computation */
    public Nilsimsa reset() {
        count = 0;
        Arrays.fill(acc, (byte) 0);
        Arrays.fill(lastch, -1);
        this.digest = null;
        return this;
    }

    /** Accumulator for a transition n between the chars a, b, c */
    private static int tran3(int a, int b, int c, int n) {
        int i = (c) ^ TRAN[n];
        return (((TRAN[(a + n) & 255] ^ TRAN[b & 0xff] * (n + n + 1)) + TRAN[i & 0xff]) & 255);
    }

    /** @return the digest for the current Nilsimsa object. */
    public byte[] digest() {
        if (digest != null) {
            return digest;
        }
        int total = 0;
        int threshold;
        digest = new byte[32];
        Arrays.fill(digest, (byte) 0);

        if (count == 3) {
            total = 1;
        } else if (count == 4) {
            total = 4;
        } else if (count > 4) {
            total = 8 * count - 28;
        }
        threshold = total / 256;

        for (int i = 0; i < 256; i++) {
            if (acc[i] > threshold) {
                digest[31-(i >> 3)] += 1 << (i & 7);
            }
        }
        return digest;
    }

    /**
     * Compute the Nilsimsa digest for the given String.
     *
     * @param data an array of bytes to hash
     * @return the Nilsimsa digest.
     */
    public byte[] digest(byte[] data) {
        reset();
        update(data);
        return digest();
    }

    /**
     * Computes the Nilsimsa digest for the given byte array.
     *
     * @param data
     * @return the byte array's Nilsimsa hash.
     */
    public static Nilsimsa getHash(byte[] data) {
        return new Nilsimsa().update(data);
    }

    /**
     * Computes the Nilsimsa digest for the given String.
     *
     * @param s
     * @return the String's Nilsimsa hash.
     */
    public static Nilsimsa getHash(String s) {
        return getHash(s.getBytes());
    }

    /**
     * Compute the Nilsimsa digest for the given String.
     *
     * @param s the String to hash
     * @return the Nilsimsa digest.
     */
    public byte[] digest(String s) {
        return digest(s.getBytes());
    }

    /** @return a String representation of the current state of the Nilsimsa object. */
    public String hexdigest() {
        return DatatypeConverter.printHexBinary(digest());
    }

    /**
     * Compute the Nilsimsa hexDigest for the given String.
     *
     * @param data an array of bytes to hash
     * @return the Nilsimsa hexdigest.
     */
    public String hexdigest(byte[] data) {
        digest(data);
        return hexdigest();
    }

    /**
     * Compute the Nilsimsa hexDigest for the given String.
     *
     * @param s the String to hash
     * @return the Nilsimsa hexdigest.
     */
    public String hexdigest(String s) {
        digest(s);
        return hexdigest();
    }

    /**
     * Compares a Nilsimsa object to the current one and return the number of bits that differ.
     *
     * @param cmp the comparison Nilsimsa object
     * @return the number of bits in which the Nilsimsa digests differ.
     */
    public int bitwiseDifference(Nilsimsa cmp) {
        int distance = 0;
        int h1;
        int h2;

        byte[] n1 = digest();
        byte[] n2 = cmp.digest();

        for (int i = 0; i < 32; i += 4) {
            h1 =
                    (n1[i] & 0xFF)
                            | (n1[i + 1] & 0xFF) << 8
                            | (n1[i + 2] & 0xFF) << 16
                            | (n1[i + 3] & 0xFF) << 24;
            h2 =
                    (n2[i] & 0xFF)
                            | (n2[i + 1] & 0xFF) << 8
                            | (n2[i + 2] & 0xFF) << 16
                            | (n2[i + 3] & 0xFF) << 24;
            distance += Integer.bitCount(h1 ^ h2);
        }
        return distance;
    }

    /**
     * Returns a value between -128 and + 128 that indicates the difference between the nilsimsa
     * digest of the current object and cmp.
     *
     * @param cmp comparison Nilsimsa object
     * @return a value between -128 (no matching bits) and 128 (all bits match; both hashes are
     *     equal)
     */
    public int compare(Nilsimsa cmp) {
        return 128 - bitwiseDifference(cmp);
    }

    @Override
    public boolean equals(Object o) {
    	if (o == null || o.getClass() != getClass()) {
    		return false;
    	}
    	return Arrays.equals(digest(), ((Nilsimsa)  o).digest());
    }

    @Override
    public int hashCode() {
    	return Arrays.hashCode(digest);
    }
}
