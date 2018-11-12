package com.weblyzard.lib.string.nilsimsa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * Test the Nilsimsa algorithm.
 *
 * @author Albert Weichselbraun
 */
public class NilsimsaTest {

    private static final Charset CONTENT_ENCODING = Charset.forName("UTF-8");
    private static final String[] TEST_DATA = {"73302DF80673894C115249B1F880ABB1EC2B09F1C9726E642B690291E636FE6F c",
            "67B02DF81323816C51019D71DA92612DEDE05CF1CD20FB042B218310E61368EF hmac",
            "5FB02D781633A84ED3420AB1F2922931CCA119F14BBA6CCC2F3B2010E23662EF java",
            "7BB42C980372A18F3113B1F5D29035A1FD2B79B5D9326C452B4023D0E212E14F lsh",
            "7FB004B80683894C751019B1D2882D3164AA48F14BB0EEC42F380294E63468EF md5",
            "CB3025980276894C631208B1DA8029B1446329F1C972F6642C292B91E636766F perl",
            "5B3025781212884EE90359B1FB842931EC2B39F1592264642F2D6A95E636F2EB python",
            "5E302D3802D2894EF30259B1F8902F31EC2B5BB14BD2664429386A91E632F26F ruby",
            "6B3024D80623984D51521DF1DA8C2131ECA849F14BB37E442F3C2A98E637604F sha1"};

    // the bitwise similarity between the nilsimsa strings given above
    private static final int[][] REFERENCE_DISTANCE = {{0, 134, 123, 132, 134, 126, 136, 129, 136,},
            {134, 0, 99, 118, 120, 114, 112, 111, 112,}, {123, 99, 0, 113, 107, 103, 111, 106, 109,},
            {132, 118, 113, 0, 110, 112, 114, 115, 112,}, {134, 120, 107, 110, 0, 110, 110, 105, 106,},
            {126, 114, 103, 112, 110, 0, 108, 99, 98,}, {136, 112, 111, 114, 110, 108, 0, 91, 122,},
            {129, 111, 106, 115, 105, 99, 91, 0, 95,}, {136, 112, 109, 112, 106, 98, 122, 95, 0,},};

    private static Map<String, String> testDocuments;

    public NilsimsaTest() {
        testDocuments = _readTestDocuments();
    }

    @Test
    public void hashTest() {
        for (Map.Entry<String, String> testSet : testDocuments.entrySet()) {
            Nilsimsa n = Nilsimsa.getHash(testSet.getValue());
            System.out.println(n.hexdigest() + " " + testSet.getKey());
            assertEquals(testSet.getKey(), n.hexdigest(testSet.getValue()));
        }
    }

    @Test
    public void umlautTest() {
        String digest = Nilsimsa.getHash("Öö".getBytes(CONTENT_ENCODING)).hexdigest();
        assertEquals("0000100000000000000000000000040000004000000000000000000000004000", digest);
    }

    @Test
    public void differenceTest() {
        for (int i = 0; i < TEST_DATA.length; i++) {
            String referenceString = TEST_DATA[i];
            Nilsimsa referenceHash = Nilsimsa.getHash(referenceString);
            // System.out.print("{");
            for (int j = 0; j < TEST_DATA.length; j++) {
                int distance = referenceHash.bitwiseDifference(Nilsimsa.getHash(TEST_DATA[j]));
                assertEquals(REFERENCE_DISTANCE[i][j], distance);
            }
        }
    }

    @Test
    public void equalsAndHashCodeTest() {
        Nilsimsa h1, h2;

        // test equals and hash code
        for (int i = 0; i < TEST_DATA.length; i++) {
            h1 = Nilsimsa.getHash(TEST_DATA[i]);
            for (int j = 0; j < TEST_DATA.length; j++) {
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
        assertNotEquals(h, "");
        assertNotEquals(h, this);
        assertEquals(h, h);
        assertEquals(h, Nilsimsa.getHash("test"));
    }

    /**
     * Tests the example from the README.md file.
     */
    @Test
    public void readmeSimilarityTest() {
        Nilsimsa first = Nilsimsa.getHash("A short test message");
        Nilsimsa second = Nilsimsa.getHash("A short test message!");
        Nilsimsa third = Nilsimsa.getHash("Something completely different");


        assertEquals(0, first.bitwiseDifference(first));
        assertEquals(3, first.bitwiseDifference(second));
        assertEquals(133, first.bitwiseDifference(third));
    }


    /**
     * compile test mapping
     *
     * @return a mapping of file content and the corresponding reference Nilsimsa hash.
     */
    private static Map<String, String> _readTestDocuments() {
        Map<String, String> result = new HashMap<>();
        String[] testSet;
        String documentContent;
        for (String testData : TEST_DATA) {
            testSet = testData.split(" ");
            try {
                URL resource = NilsimsaTest.class.getClassLoader().getResource("wiki-" + testSet[1] + ".txt");
                documentContent = new String(Files.readAllBytes(Paths.get(resource.toURI())), CONTENT_ENCODING);
                result.put(testSet[0], documentContent);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                fail("Cannot read corpus.");
            }
        }
        return result;
    }
}
