package sg.edu.nus.comp.cs4218.impl;

import java.math.BigInteger;
import java.util.Arrays;

public final class CmpApplicationUtil {
	private static final String SIMPLE = "Files differ";
	private static final String NORMAL_FORMAT = "%1$s %2$s differ: char %3$s, line %4$s%5$s";
	private static final String LONG_FORMAT = "%1$s %2$s %3$s"; //byte# octal1# octal2#
	private static final String IS_FORMAT = " is %1$s %2$s";
	private static final String CHAR_FORMAT = "%1$s %2$s"; //octal# value#
	public static final String STDIN = "-";
	
	private CmpApplicationUtil() {}
	
	public static String getSimpleString(byte[] fileA, byte[] fileB) throws Exception {
		String message;
		boolean areEqual = Arrays.equals(fileA, fileB);
		if(areEqual) {
			message = "";
		}
		else {
			message = SIMPLE;
		}
		return message;
	}
	
	public static String getNormalFormatString(String fileNameA, String fileNameB, byte[] fileA, byte[] fileB, Boolean isCharDiff) throws Exception {
		int line = 1;
		StringBuilder strBuilder = new StringBuilder();
		String appendString = "";
		for(int i=0; i < fileA.length && i < fileB.length; i++) {
			if(fileA[i] != fileB[i]) {
				if(isCharDiff) {
					appendString = getIsFormatString(fileA[i], fileB[i]);
				}
				strBuilder.append(String.format(NORMAL_FORMAT, fileNameA, fileNameB,
						i+1, line, appendString));
				break;
			}
			if(fileA[i] == fileB[i] && fileA[i] == '\n') {
				line++;
			}
		}
		return strBuilder.toString();
	}
	
	private static String getIsFormatString(byte first, byte second) {
		return String.format(IS_FORMAT, getOctalCharString(first), getOctalCharString(second));
	}
	
	private static String getOctalCharString(byte single) {
		return String.format(CHAR_FORMAT, toOctal(single), Byte.toString(single));
	}
	
	public static String getLongFormatString(byte[] fileA, byte[] fileB, boolean isCharDiff) throws Exception {
		StringBuilder strBuilder = new StringBuilder();
		String diffA, diffB;
		for(int i=0; i < fileA.length && i < fileB.length; i++) {
			if(fileA[i] != fileB[i]) {
				if(strBuilder.length() > 0) {
					strBuilder.append(System.lineSeparator());
				}
				if(isCharDiff) {
					diffA = getOctalCharString(fileA[i]);
					diffB = getOctalCharString(fileB[i]);
				}
				else {
					diffA = toOctal(fileA[i]);
					diffB = toOctal(fileB[i]);
				}
				strBuilder.append(String.format(LONG_FORMAT, i+1, diffA, diffB));
			}
		}
		return strBuilder.toString();
	}

	private static String toOctal(byte single) {
		BigInteger bigInt = new BigInteger(new byte[] {single});
		return bigInt.toString(8);
	}
}
