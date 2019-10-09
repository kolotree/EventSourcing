package com.kolotree.common.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CurrentDateAndTimeInUTCProvider {

    public static Date getCurrentDateAndTimeInUTC() {
        return getCalendarInstanceInUTC().getTime();
    }

    public static Calendar getCalendarInstanceInUTC() {
        return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    }
}
