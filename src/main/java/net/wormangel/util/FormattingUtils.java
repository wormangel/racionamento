package net.wormangel.util;

import java.text.DecimalFormat;

public class FormattingUtils {
    public static String formatDouble(double x) {
        return new DecimalFormat("#.##").format(x);
    }
}
