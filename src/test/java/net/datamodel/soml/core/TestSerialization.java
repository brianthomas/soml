/**
 * 
 */
package net.datamodel.soml.core;

import org.apache.log4j.Logger;

import net.datamodel.soml.BaseCase;

/**
 * @author thomas
 *
 */
public class TestSerialization extends BaseCase {

	private static final Logger logger = Logger.getLogger(TestSerialization.class);
	
	// test SO serialization
	//
	public void test1() {
		logger.info("Check SO serialization.");
		assertTrue(true);
	}
	
	// test URN serlization
	//
	public void test2() {
		logger.info("Check URN serialization.");
		
	}
	
	// test UnmixedList serialization
	//
	public void test3() {
		logger.info("Check unmixedlist serialization.");
	}
	

}
