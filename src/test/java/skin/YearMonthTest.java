package skin;

import converter.LocalDateStringConverter;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

public class YearMonthTest extends TestCase {

    @Test
    public void testEquals() throws Exception {
        YearMonth ym = new YearMonth(new Date());
        YearMonth ym_ = new YearMonth(new Date());
        assertTrue(ym.equals(ym_));
    }

    @Test
    public void testEquals1() throws Exception {
        assertTrue(YearMonth.equals(new Date(), new Date()));
    }

    @Test
    public void testNow() throws Exception {
        assertTrue(YearMonth.now().equals(new YearMonth(new Date())));
    }

    @Test
    public void testAtDay() throws Exception {
        YearMonth ym = new YearMonth(new Date());
        YearMonth ym_ = new YearMonth(new Date());
        assertFalse(YearMonth.equals(ym_.atDay(1), ym.atDay(2)));
    }

    @Test
    public void testMinusMonths() throws Exception {
        YearMonth ym = new YearMonth(new Date());
        assertEquals(ym.minusMonths(1).getMonthValue()+1, ym.getMonthValue());
    }

    @Test
    public void testPlusMonths() throws Exception {
        YearMonth ym = new YearMonth(new Date());
        assertEquals(ym.plusMonths(1).getMonthValue()-1, ym.getMonthValue());
    }

    @Test
    public void testLengthOfMonth() throws Exception {
        YearMonth ym = new YearMonth(new Date());
        int[] data = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 30};
        assertEquals(data[ym.getMonthValue()], ym.lengthOfMonth());
    }

    @Test
    public void testIsInSameMonth() throws Exception {
        Date date = new Date();
        YearMonth ym = new YearMonth(date);
        assertFalse(ym.minusMonths(1).isInSameMonth(date));
        assertFalse(ym.plusMonths(1).isInSameMonth(date));
        assertTrue(ym.isInSameMonth(date));
    }

    @Test
    public void testGetMonthValue() throws Exception {
        LocalDateStringConverter ldsc = new LocalDateStringConverter();
        YearMonth ym = new YearMonth(ldsc.fromString("950101"));
        YearMonth ym_ = new YearMonth(ldsc.fromString("950106"));
        assertEquals(ym.getWeekValue(), 1);
        assertEquals(ym_.getWeekValue(), 1);
        assertTrue(ym.isInSameWeek(ldsc.fromString("950106")));
    }
}