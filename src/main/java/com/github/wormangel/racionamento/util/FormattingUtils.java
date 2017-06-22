package com.github.wormangel.racionamento.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FormattingUtils {
    public static String formatDouble(double x) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
        DecimalFormat df = (DecimalFormat)nf;
        return df.format(x);
    }
}
