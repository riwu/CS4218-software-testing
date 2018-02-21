package sg.edu.nus.comp.cs4218.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ImplTestSuite {
	
	@Test
	public void testInvalidAppMessage() {
		ShellImpl shellImpl = new ShellImpl();
		assertEquals("Invalid app.", ShellImpl.EXP_INVALID_APP);
	}
}
