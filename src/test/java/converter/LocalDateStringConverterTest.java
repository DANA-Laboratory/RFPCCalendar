package converter;


import junit.framework.TestCase;
import org.junit.Test;
import java.util.Date;

import static org.junit.Assert.*;

public class LocalDateStringConverterTest extends TestCase {

    @Test
    public void testToString() throws Exception {
        LocalDateStringConverter ldsc = new LocalDateStringConverter();
        Date date = new Date();
        date.setTime(1471548600000L);
        assertEquals(ldsc.toString(date), "۱۳۹۵/۰۵/۲۹");
    }

    @Test
    public void testFromString() throws Exception {
        LocalDateStringConverter ldsc = new LocalDateStringConverter();
        Date date = new Date();
        date.setTime(1471548600000L);
        assertEquals(ldsc.fromString("۱۳۹۵/۰۵/۲۹"), date);
        assertEquals(ldsc.fromString("1395/05/29"), date);
        assertEquals(ldsc.fromString("1395/5/29"), date);
        assertEquals(ldsc.fromString("950529"), date);
        assertEquals(ldsc.fromString("13950529"), date);
        assertEquals(ldsc.fromString("1395529"), date);
    }
}