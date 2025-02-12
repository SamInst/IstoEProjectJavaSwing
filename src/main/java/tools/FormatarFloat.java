package tools;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatarFloat {

    public static String format(Float valor){
        NumberFormat nf = NumberFormat.getInstance(new Locale("pt", "BR"));
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return nf.format(valor);
    }
}
