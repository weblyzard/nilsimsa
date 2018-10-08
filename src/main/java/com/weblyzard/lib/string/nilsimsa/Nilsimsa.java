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

	// pre-defined transformation array
	private static final byte[] TRAN = {
			(byte)0x02, (byte)0xD6, (byte)0x9E, (byte)0x6F, (byte)0xF9, (byte)0x1D, (byte)0x04, (byte)0xAB, 
			(byte)0xD0, (byte)0x22, (byte)0x16, (byte)0x1F, (byte)0xD8, (byte)0x73, (byte)0xA1, (byte)0xAC,
			(byte)0x3B, (byte)0x70, (byte)0x62, (byte)0x96, (byte)0x1E, (byte)0x6E, (byte)0x8F, (byte)0x39, 
			(byte)0x9D, (byte)0x05, (byte)0x14, (byte)0x4A, (byte)0xA6, (byte)0xBE, (byte)0xAE, (byte)0x0E,
			(byte)0xCF, (byte)0xB9, (byte)0x9C, (byte)0x9A, (byte)0xC7, (byte)0x68, (byte)0x13, (byte)0xE1, 
			(byte)0x2D, (byte)0xA4, (byte)0xEB, (byte)0x51, (byte)0x8D, (byte)0x64, (byte)0x6B, (byte)0x50,
			(byte)0x23, (byte)0x80, (byte)0x03, (byte)0x41, (byte)0xEC, (byte)0xBB, (byte)0x71, (byte)0xCC,
			(byte)0x7A, (byte)0x86, (byte)0x7F, (byte)0x98, (byte)0xF2, (byte)0x36, (byte)0x5E, (byte)0xEE,
			(byte)0x8E, (byte)0xCE, (byte)0x4F, (byte)0xB8, (byte)0x32, (byte)0xB6, (byte)0x5F, (byte)0x59,
			(byte)0xDC, (byte)0x1B, (byte)0x31, (byte)0x4C, (byte)0x7B, (byte)0xF0, (byte)0x63, (byte)0x01,
			(byte)0x6C, (byte)0xBA, (byte)0x07, (byte)0xE8, (byte)0x12, (byte)0x77, (byte)0x49, (byte)0x3C,
			(byte)0xDA, (byte)0x46, (byte)0xFE, (byte)0x2F, (byte)0x79, (byte)0x1C, (byte)0x9B, (byte)0x30,
			(byte)0xE3, (byte)0x00, (byte)0x06, (byte)0x7E, (byte)0x2E, (byte)0x0F, (byte)0x38, (byte)0x33,
			(byte)0x21, (byte)0xAD, (byte)0xA5, (byte)0x54, (byte)0xCA, (byte)0xA7, (byte)0x29, (byte)0xFC,
			(byte)0x5A, (byte)0x47, (byte)0x69, (byte)0x7D, (byte)0xC5, (byte)0x95, (byte)0xB5, (byte)0xF4,
			(byte)0x0B, (byte)0x90, (byte)0xA3, (byte)0x81, (byte)0x6D, (byte)0x25, (byte)0x55, (byte)0x35,
			(byte)0xF5, (byte)0x75, (byte)0x74, (byte)0x0A, (byte)0x26, (byte)0xBF, (byte)0x19, (byte)0x5C,
			(byte)0x1A, (byte)0xC6, (byte)0xFF, (byte)0x99, (byte)0x5D, (byte)0x84, (byte)0xAA, (byte)0x66,
			(byte)0x3E, (byte)0xAF, (byte)0x78, (byte)0xB3, (byte)0x20, (byte)0x43, (byte)0xC1, (byte)0xED,
			(byte)0x24, (byte)0xEA, (byte)0xE6, (byte)0x3F, (byte)0x18, (byte)0xF3, (byte)0xA0, (byte)0x42,
			(byte)0x57, (byte)0x08, (byte)0x53, (byte)0x60, (byte)0xC3, (byte)0xC0, (byte)0x83, (byte)0x40,
			(byte)0x82, (byte)0xD7, (byte)0x09, (byte)0xBD, (byte)0x44, (byte)0x2A, (byte)0x67, (byte)0xA8,
			(byte)0x93, (byte)0xE0, (byte)0xC2, (byte)0x56, (byte)0x9F, (byte)0xD9, (byte)0xDD, (byte)0x85,
			(byte)0x15, (byte)0xB4, (byte)0x8A, (byte)0x27, (byte)0x28, (byte)0x92, (byte)0x76, (byte)0xDE,
			(byte)0xEF, (byte)0xF8, (byte)0xB2, (byte)0xB7, (byte)0xC9, (byte)0x3D, (byte)0x45, (byte)0x94,
			(byte)0x4B, (byte)0x11, (byte)0x0D, (byte)0x65, (byte)0xD5, (byte)0x34, (byte)0x8B, (byte)0x91,
			(byte)0x0C, (byte)0xFA, (byte)0x87, (byte)0xE9, (byte)0x7C, (byte)0x5B, (byte)0xB1, (byte)0x4D,
			(byte)0xE5, (byte)0xD4, (byte)0xCB, (byte)0x10, (byte)0xA2, (byte)0x17, (byte)0x89, (byte)0xBC,
			(byte)0xDB, (byte)0xB0, (byte)0xE2, (byte)0x97, (byte)0x88, (byte)0x52, (byte)0xF7, (byte)0x48,
			(byte)0xD3, (byte)0x61, (byte)0x2C, (byte)0x3A, (byte)0x2B, (byte)0xD1, (byte)0x8C, (byte)0xFB,
			(byte)0xF1, (byte)0xCD, (byte)0xE4, (byte)0x6A, (byte)0xE7, (byte)0xA9, (byte)0xFD, (byte)0xC4,
			(byte)0x37, (byte)0xC8, (byte)0xD2, (byte)0xF6, (byte)0xDF, (byte)0x58, (byte)0x72, (byte)0x4E
	};


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
