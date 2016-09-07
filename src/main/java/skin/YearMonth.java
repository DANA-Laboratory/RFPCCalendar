package skin;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

import java.util.Date;

/**
 * Created by AliReza on 8/18/2016.
 */
public class YearMonth {
    final static ULocale faLocale = new ULocale("fa_IR@calendar=persian");
    final DateFormat faMonthFormatter = DateFormat.getPatternInstance("MMMM", faLocale);
    private Calendar calendar;

    public static boolean isToday(Date date) {
        return equals(date, Calendar.getInstance().getTime());
    }
    public static int compare(Date d1, Date d2) {
        if(d1 == null || d2 == null)
            return -2;
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        if(c1.get(Calendar.JULIAN_DAY) == c2.get(Calendar.JULIAN_DAY))
            return  0;
        else
            if(c1.get(Calendar.JULIAN_DAY) > c2.get(Calendar.JULIAN_DAY))
                return 1;
            else
                if(c1.get(Calendar.JULIAN_DAY) < c2.get(Calendar.JULIAN_DAY))
                    return -1;
        return -2;
    }
    public static boolean equals(Date d1, Date d2){
        if(d1 == null || d2 == null)
            return false;
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        return(c1.get(Calendar.JULIAN_DAY) == c2.get(Calendar.JULIAN_DAY));
    }

    public static YearMonth now() {
        return new YearMonth();
    };

    YearMonth() {
        calendar = Calendar.getInstance(faLocale);
    }

    YearMonth(Date date) {
        calendar = Calendar.getInstance(faLocale);
        setDate(date);

    }
    public YearMonth setDate(Date date) {
        calendar.setTime(date);
        return this;
    }
    public boolean equals(YearMonth yearMonth){
        return(this.getMonthValue() == yearMonth.getMonthValue() && this.getYear() == yearMonth.getYear());
    }

    public boolean isInSameMonth(Date date) {
        Calendar _calendar = Calendar.getInstance(faLocale);
        _calendar.setTime(date);
        return (_calendar.get(Calendar.MONTH) == getMonthValue());
    }

    public boolean isInSameWeek(Date date) {
        Calendar _calendar = Calendar.getInstance(faLocale);
        _calendar.setTime(date);
        return (_calendar.get(Calendar.WEEK_OF_YEAR) == getWeekValue());
    }

    public Date atDay(int i){
        Calendar _calendar = Calendar.getInstance(faLocale);
        _calendar.setTime(this.calendar.getTime());
        _calendar.set(Calendar.DAY_OF_MONTH, i);
        return _calendar.getTime();//TODO
    }

    public YearMonth minusMonths(int i){
        Calendar _calendar = Calendar.getInstance(faLocale);
        _calendar.setTime(this.calendar.getTime());
        _calendar.add(Calendar.MONTH, -1*i);
        return new YearMonth(_calendar.getTime());
    }

    public YearMonth plusMonths(int i){
        Calendar _calendar = Calendar.getInstance(faLocale);
        _calendar.setTime(this.calendar.getTime());
        _calendar.add(Calendar.MONTH, i);
        return new YearMonth(_calendar.getTime());
    }

    public int lengthOfMonth(){
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public int getMonthValue(){
        return calendar.get(Calendar.MONTH);
    }

    public int getWeekValue(){
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }
    public int getYear(){
        return calendar.get(Calendar.YEAR);
    }

    public String getMonth(){
        return faMonthFormatter.format(calendar);
    }
}
