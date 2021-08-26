package cloud.ptl.povserver.vaadin.utils;

import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;

@UtilityClass
public class NumberFormatter {
    private static final DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    public static String format(Float number) {
        return decimalFormat.format(number);
    }
}
