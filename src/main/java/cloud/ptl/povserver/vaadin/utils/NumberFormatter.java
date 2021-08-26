package cloud.ptl.povserver.vaadin.utils;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;

/**
 * Utility class used to format numbers in user interface
 */
@UtilityClass
public class NumberFormatter {
    private static final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    public static String format(Float number) {
        return decimalFormat.format(number);
    }
}
