package com.weblyzard.lib.string.nilsimsa;

import static org.junit.Assert.*;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.apache.commons.codec.Charsets;
import org.junit.Test;

/**
 * Test the Nilsimsa algorithm
 * @author Albert Weichselbraun <albert@weichselbraun.net>
 *
 */
public class NilsimsaTest {
	
	private final static Charset CONTENT_ENCODING = Charsets.UTF_8;
	private final static String[] TEST_DATA = { 
		"73302df80673894c115249b1f880abb1ec2b09f1c9726e642b690291e636fe6f c",
		"67b02df81323816c51019d71da92612dede05cf1cd20fb042b218310e61368ef hmac",
		"5fb02d781633a84ed3420ab1f2922931cca119f14bba6ccc2f3b2010e23662ef java",
		"7bb42c980372a18f3113b1f5d29035a1fd2b79b5d9326c452b4023d0e212e14f lsh",
		"7fb004b80683894c751019b1d2882d3164aa48f14bb0eec42f380294e63468ef md5",
		"cb3025980276894c631208b1da8029b1446329f1c972f6642c292b91e636766f perl",
		"5b3025781212884ee90359b1fb842931ec2b39f1592264642f2d6a95e636f2eb python",
		"5e302d3802d2894ef30259b1f8902f31ec2b5bb14bd2664429386a91e632f26f ruby",
		"6b3024d80623984d51521df1da8c2131eca849f14bb37e442f3c2a98e637604f sha1"
	};
	
	// the bitwise similarity between the nilsimsa strings given above
	private final static int[][] REFERENCE_DISTANCE = {
			{  0, 129, 126, 128, 129, 122, 130, 121, 115, }, 
			{129,   0,  99, 111, 120, 123, 117, 116, 100, }, 
			{126,  99,   0, 104,  95, 120, 104, 115, 111, }, 
			{128, 111, 104,   0, 113, 102, 112, 101, 111, }, 
			{129, 120,  95, 113,   0, 111, 117, 112, 110, }, 
			{122, 123, 120, 102, 111,   0, 112,  99, 113, }, 
			{130, 117, 104, 112, 117, 112,   0,  93, 119, }, 
			{121, 116, 115, 101, 112,  99,  93,   0, 110, }, 
			{115, 100, 111, 111, 110, 113, 119, 110,   0, }, 
		};

	private static Map<String, String> testDocuments;

	public NilsimsaTest() {
		testDocuments = _readTestDocuments();
	}
	
	@Test
	public void hashTest() {	
		for (Map.Entry<String, String>testSet: testDocuments.entrySet()) {
			Nilsimsa n = Nilsimsa.getHash(testSet.getValue());
			System.out.println(n.hexdigest()+" "+testSet.getKey());
			assertEquals( testSet.getKey(), n.hexdigest( testSet.getValue()) );	
		}
	}
	
	@Test 
	public void umlautTest() {
		String digest = Nilsimsa.getHash("Öö".getBytes(Charsets.UTF_8)).hexdigest();
		assertEquals("0000100000000000000000000000040000004000000000000000000000004000", digest);
	}
	
	@Test
	public void differenceTest() {
		for (int i=0; i<TEST_DATA.length; i++) {
			String referenceString = TEST_DATA[i];
			Nilsimsa referenceHash = Nilsimsa.getHash(referenceString);
			// System.out.print("{");
			for (int j=0; j<TEST_DATA.length; j++) {
				int distance = referenceHash.bitwiseDifference(Nilsimsa.getHash(TEST_DATA[j]));
				// System.out.print(distance + ", ");
				assertEquals(REFERENCE_DISTANCE[i][j], distance);
			}
			// System.out.println("}, ");
		}
	}
	
	@Test
	public void equalsAndHashCodeTest() {
		Nilsimsa h1, h2;
		
		// test equals and hash code
		for (int i=0; i<TEST_DATA.length; i++) {
			h1 = Nilsimsa.getHash(TEST_DATA[i]);
			for (int j=0; j<TEST_DATA.length; j++) {
				h2 = Nilsimsa.getHash(TEST_DATA[j]);
				if (j == i) {
					assertEquals(h1, h2);
					assertEquals(h1.hashCode(), h2.hashCode());
				} else {
					assertNotEquals(h1, h2);
					assertNotEquals(h1.hashCode(), h2.hashCode());
				}
			}
		}
	}
	
	@Test
	public void equalsSpecialCasesTest() {
		Nilsimsa h = Nilsimsa.getHash("test");
		assertNotEquals(h, null);
		assertNotEquals(h, null);
		assertNotEquals(h, this);
		assertEquals(h, h);
	}
	
	/**
	 * compile test mapping
	 * @return a mapping of file content and the corresponding reference
	 *         Nilsimsa hash.
	 */
	private static Map<String, String> _readTestDocuments() {
		Map<String, String> result = new HashMap<> ();
		String[] testSet;
		String documentContent;
 		for (String testData: TEST_DATA) {
 			testSet = testData.split(" ");
 			try {
 				URL resource = NilsimsaTest.class.getClassLoader().getResource("wiki-"+ testSet[1] + ".txt");
 				documentContent = new String(Files.readAllBytes(Paths.get(resource.toURI())), CONTENT_ENCODING);
				result.put( testSet[0], documentContent);
			} catch (IOException | URISyntaxException e) {
				e.printStackTrace();
				fail("Cannot read corpus.");
			}
		}
		return result;
	}

}
