package com.partho.porter.porterdelivery.utils;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

public class Utils{

    public static String dateFromTimestamp(Long timestamp)
    {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}
