package converter;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.ULocale;
import javafx.util.StringConverter;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by AliReza on 8/19/2016.
 */
public class LocalDateStringConverter<Date> extends StringConverter<java.util.Date> {
    private ULocale faLocale = new ULocale("fa_IR@calendar=persian");
    final DateFormat faFormatter = DateFormat.getInstanceForSkeleton("y/MM/dd", faLocale);

    @Override
    public String toString(java.util.Date object) {
        return faFormatter.format(object).substring(0,10);
    }

    @Override
    public java.util.Date fromString(String string) {
        try {
            int i = Integer.parseInt(string);
            if (string.substring(0,2).compareTo("13")!=0)
                string = "13" + string;
            int dayPos = 6;
            if (string.length() == 7)
                dayPos = 5;
            string = string.substring(0, 4) + "/" + string.substring(4, dayPos) + "/" + string.substring(dayPos, dayPos + 2);
        } catch (NumberFormatException e)
        {

        }
        try {
            return faFormatter.parse(string + " ه. ش");
        } catch (ParseException e) {
            return null;
        }
    }
}
