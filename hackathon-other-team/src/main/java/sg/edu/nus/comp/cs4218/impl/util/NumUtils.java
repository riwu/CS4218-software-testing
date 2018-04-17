package sg.edu.nus.comp.cs4218.impl.util;

public final class NumUtils {
    private NumUtils() {}
    public static int decToOctal(int number) {
        return Integer.parseUnsignedInt(Integer.toOctalString(number), 10);
    }
}
