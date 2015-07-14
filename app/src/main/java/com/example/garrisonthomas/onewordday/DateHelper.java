package com.example.garrisonthomas.onewordday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelper {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static String formatDate(String dateTBFormatted)
    {
        SimpleDateFormat inputformat = new SimpleDateFormat("yyyyMMdd", Locale.CANADA);
        SimpleDateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        Date inputDate=null;
        try {
            inputDate = inputformat.parse(dateTBFormatted);
        } catch (ParseException e) {
            return dateTBFormatted;
        }
        return outputformat.format(inputDate);
    }

    public static String formatTime(String timeTBFormatted)
    {
        SimpleDateFormat inputformat = new SimpleDateFormat("hhmmss",Locale.CANADA);
        SimpleDateFormat outputformat = new SimpleDateFormat("hh:mm:ss",Locale.CANADA);
        Date inputTime=null;
        try {
            inputTime = inputformat.parse(timeTBFormatted);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            return timeTBFormatted;
        }
        return outputformat.format(inputTime);
    }


    public static String getCurrentDate(){

        Date resultdate = new Date(System.currentTimeMillis());
        return sdf.format(resultdate);
    }

    public static String getDateBasedOnMonths(Integer months)
    {
        Calendar cal = GregorianCalendar.getInstance();
        cal.add( Calendar.MONTH, months);
        Date monthsAgo = cal.getTime();

        return sdf.format(monthsAgo);

    }

    public static  String getDateTimeFormattedFromMilliseconds(long milliseconds)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm",Locale.CANADA);
        Date resultdate = new Date(milliseconds);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(resultdate);
    }
    public static  String getDateTimeFormattedForPhotos(long milliseconds)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy",Locale.CANADA);
        Date resultdate = new Date(milliseconds);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(resultdate);
    }
}
