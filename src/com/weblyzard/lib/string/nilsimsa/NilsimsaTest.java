package com.weblyzard.lib.string.nilsimsa;

import static org.junit.Assert.*;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * Test the Nilsimsa algorithm
 * @author Albert Weichselbraun
 *
 */
public class NilsimsaTest {
	
	private final static String CONTENT_ENCODING = "UTF-8";
	private static List<String> TEST_DATA = Arrays.asList( 
			"19b3a8581437480de15031b4f286ef015c0e8631d960b4f62f63a95bce87f06f content-penguins.txt",
			"48b525408661294d953200b0728582654c03537349a3675c45390150f736e66f content-reading-stores.txt",
			"73f52da90660185e411102b9eb8129e1e42763f3496036647e6120d5e7b7e0ff content-scottish-independence.txt",
			"7ff0a9b98644590ca1038895d984a321668b4a6151733f642e2322d0efb2e2ca content-uk-pollution-outsourcing.txt",
			"5bb12c081271095d90530aa1da808171766313f1414c6fec2f202a14f656e4ed content-unrelated1.txt",
			"5fb405b81610094cc15196e1fa852c7506ac42f349325ccd2e1c0a12e637644e content-unrelated2.txt",
			"6f3223c88601014d94134afdfb88ba314cae19f149327ae4ea212a106312da6f content-unrelated3.txt",
			"6bb4058986700995805318fdfac4013144a30bf3e1747de42d20cf10ee334c6d content-unrelated4.txt",
			"5fb225e8027089fcd6530ab3d090c981642f08a549207cd52d222a04ea36e6ef content-wohlg.txt");

	private static Map<String, String> testDocuments;

	public NilsimsaTest() {
		testDocuments = _readTestDocuments();
	}
	
	@Test
	public void test() {	
		for (Map.Entry<String, String>testSet: testDocuments.entrySet()) {
			Nilsimsa n = new Nilsimsa();
			n.update( testSet.getValue() );
			System.out.println(n.hexdigest()+" "+testSet.getKey());
			assertEquals( testSet.getKey(), n.hexdigest( testSet.getValue()) );
			
		}
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
 				URL resource = NilsimsaTest.class.getResource("test/"+ testSet[1]);
				documentContent = FileUtils.readFileToString(
						new File( resource.getFile()), CONTENT_ENCODING);
				result.put( testSet[0], documentContent);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				fail("Cannot read corpus.");
			}
		}
		return result;
	}
	

}
