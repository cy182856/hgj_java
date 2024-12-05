package com.ej.hgj.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class DateUtils {
    public static final String SHORT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SHORT_DATE_GBK_FORMAT = "yyyy年MM月dd日";
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_EXCLUD_YEAR = "MM-dd HH:mm";
    public static final String DATE_GBK_FORMAT = "yyyy年MM月dd日 HH时mm分";
    public static final String LONG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String LONG_DATE_GBK_FORMAT = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String MAIL_DATE_FORMAT = "yyyyMMddHHmmss";
    public static final String MAIL_DATE_HHMM_FORMAT = "HH:mm";
    public static final String FULL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
    public static final String FULL_DATE_GBK_FORMAT = "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒";
    public static final String FULL_DATE_COMPACT_FORMAT = "yyyyMMddHHmmssSSS";
    public static final String LDAP_DATE_FORMAT = "yyyyMMddHHmm'Z'";
    public static final String US_LOCALE_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String MAIL_DATE_DT_PART_FORMAT = "yyyyMMdd";
    public static final String MAIL_TIME_TM_PART_FORMAT = "HHmmss";
    public static final String LONG_DATE_TM_PART_FORMAT = "HH:mm:ss";
    public static final String Long_DATE_TM_PART_GBK_FORMAT = "HH时mm分ss秒";
    public static final String MAIL_DATA_DTM_PART_FORMAT = "MM月dd日HH:mm";
    public static final String POINT_DATA_DTM_PART_FORMAT = "yyyy.MM.dd";
    public static final String DEFAULT_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static long NANO_ONE_SECOND = 1000L;
    public static long NANO_ONE_MINUTE;
    public static long NANO_ONE_HOUR;
    public static long NANO_ONE_DAY;
    public static SimpleDateFormat sdfYmdHms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat sdf_Ymd = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyyMMdd");
    public static SimpleDateFormat sdfY = new SimpleDateFormat("yyyy");

    // 获取系统时间 年月日时分秒
    public static String strYmdHms(){
        Date date = new Date();
        String ymdHms = DateUtils.sdfYmdHms.format(date);
        return ymdHms;
    }

    public static String strYmd(Date date){
        String ymd = DateUtils.sdf_Ymd.format(date);
        return ymd;
    }

    public static String strYmd(){
        String ymd = DateUtils.sdfYmd.format(new Date());
        return ymd;
    }

    public static String strY(){
        String y = DateUtils.sdfY.format(new Date());
        return y;
    }

    public static Date getNow() {
        return new Date();
    }

    public static long getNowTimestamp() {
        return getNow().getTime();
    }

    public static String getCurrentDate() {
        return toMailDateDtPartString(getNow());
    }

    public static String getCurrentTime() {
        return toMailTimeTmPartString(getNow());
    }

    public static String getCurrentMmDdHmTime() {
        return toMailDtmPart(getNow());
    }

    public static String getCurrentDateTime() {
        return toMailDateString(getNow());
    }

    public static final String toFormatDateString(Date aDate, String formatStr) {
        if (aDate == null) {
            return "";
        } else {
            Assert.hasText(formatStr);
            return (new SimpleDateFormat(formatStr)).format(aDate);
        }
    }

    public static final String toShortDateString(Date aDate) {
        return toFormatDateString(aDate, "yyyy-MM-dd");
    }

    public static final String wechatPubFormat(Date aDate) {
        return toFormatDateString(aDate, "yyyy年MM月dd日 HH:mm:ss");
    }

    public static final String toMailDateDtPartString(Date aDate) {
        return toFormatDateString(aDate, "yyyyMMdd");
    }

    public static final String toMailTimeTmPartString(Date aDate) {
        return toFormatDateString(aDate, "HHmmss");
    }

    public static final String toMailDateString(Date aDate) {
        return toFormatDateString(aDate, "yyyyMMddHHmmss");
    }

    public static final String toMailDtmPart(Date aDate) {
        return toFormatDateString(aDate, "MM月dd日HH:mm");
    }

    public static final String toPointDtmPart(Date aDate) {
        return toFormatDateString(aDate, "yyyy.MM.dd");
    }

    public static final String toLongDateString(Date aDate) {
        return toFormatDateString(aDate, "yyyy-MM-dd HH:mm:ss");
    }

    public static final String toLongDateTmPartString(Date aDate) {
        return toFormatDateString(aDate, "HH:mm:ss");
    }

    public static final String toShortDateGBKString(Date aDate) {
        return toFormatDateString(aDate, "yyyy年MM月dd日");
    }

    public static final String parseDateExcluedYear(Date aDate) {
        return toFormatDateString(aDate, "MM-dd HH:mm");
    }

    public static final String toDateGBKString(Date aDate) {
        return toFormatDateString(aDate, "yyyy年MM月dd日 HH时mm分");
    }

    public static final String toLongDateGBKString(Date aDate) {
        return toFormatDateString(aDate, "yyyy年MM月dd日 HH时mm分ss秒");
    }

    public static final String toLongDateTmPartGBKString(Date aDate) {
        return toFormatDateString(aDate, "HH时mm分ss秒");
    }

    public static final String toFullDateString(Date aDate) {
        return toFormatDateString(aDate, "yyyy-MM-dd HH:mm:ss:SSS");
    }

    public static final String toFullDateGBKString(Date aDate) {
        return toFormatDateString(aDate, "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒");
    }

    public static final String toFullDateCompactString(Date aDate) {
        return toFormatDateString(aDate, "yyyyMMddHHmmssSSS");
    }

    public static final String toLDAPDateString(Date aDate) {
        return toFormatDateString(aDate, "yyyyMMddHHmm'Z'");
    }

    public static final Date parser(String aDateStr, String formatter) throws ParseException {
        if (StringUtils.isBlank(aDateStr)) {
            return null;
        } else {
            Assert.hasText(formatter);
            SimpleDateFormat sdf = new SimpleDateFormat(formatter);
            sdf.setLenient(false);
            return sdf.parse(aDateStr);
        }
    }

    public static final Date parseLongDateString(String aDateStr) throws ParseException {
        return parser(aDateStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static final Date parseLongDateDtPartString(String aDateStr) throws ParseException {
        return parser(aDateStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static final Date parseLongDateTmPartString(String aDateStr) throws ParseException {
        return parser(aDateStr, "yyyy-MM-dd HH:mm:ss");
    }

    public static final Date parseShortDateString(String aDateStr) throws ParseException {
        return parser(aDateStr, "yyyy-MM-dd");
    }

    public static final Date parseMailDateString(String aDateStr) throws ParseException {
        return parser(aDateStr, "yyyyMMddHHmmss");
    }

    public static final Date parseMailDateDtPartString(String aDateStr) throws ParseException {
        return parser(aDateStr, "yyyyMMdd");
    }

    public static final Date parseMailDateTmPartString(String aDateStr) throws ParseException {
        return parser(aDateStr, "HHmmss");
    }

    public static final Date parseFullDateString(String aDateStr) throws ParseException {
        return parser(aDateStr, "yyyy-MM-dd HH:mm:ss:SSS");
    }

    public static Date parseDateString(String aDateStr) {
        Date ret = null;
        if (StringUtils.isNotBlank(aDateStr)) {
            try {
                if (isLongDateStr(aDateStr)) {
                    ret = parseLongDateString(aDateStr);
                } else if (isShortDateStr(aDateStr)) {
                    ret = parseShortDateString(aDateStr);
                } else {
                    if (!isDateStrMatched(aDateStr, "EEE MMM dd HH:mm:ss zzz yyyy")) {
                        throw new IllegalArgumentException("date format mismatch");
                    }

                    ret = parser(aDateStr, "EEE MMM dd HH:mm:ss zzz yyyy");
                }
            } catch (ParseException var3) {
            }
        }

        return ret;
    }

    public static String transfer2ShortDate(String dt) {
        if (!StringUtils.isBlank(dt) && dt.length() == 10) {
            Assert.notNull(dt);
            String[] tmp = StringUtils.split(dt, "-");
            Assert.isTrue(tmp != null && tmp.length == 3);
            return tmp[0].concat(StringUtils.leftPad(tmp[1], 2, "0")).concat(StringUtils.leftPad(tmp[2], 2, "0"));
        } else {
            return dt;
        }
    }

    public static String transfer2LongDateDtPart(String dt) {
        if (StringUtils.isBlank(dt)) {
            return " ";
        } else if (dt.length() != 8) {
            return dt;
        } else {
            Assert.notNull(dt);
            Assert.isTrue(dt.length() == 8);
            return dt.substring(0, 4).concat("-").concat(dt.substring(4, 6)).concat("-").concat(dt.substring(6));
        }
    }

    public static String transfer2LongDateTmPart(String tm) {
        if (!StringUtils.isBlank(tm) && tm.length() == 6) {
            Assert.notNull(tm);
            Assert.isTrue(tm.length() == 6);
            return tm.substring(0, 2).concat(":").concat(tm.substring(2, 4)).concat(":").concat(tm.substring(4));
        } else {
            return tm;
        }
    }

    public static String transfer2ShortDateTmPart(String tm) {
        if (!StringUtils.isBlank(tm) && tm.length() == 8) {
            Assert.notNull(tm);
            Assert.isTrue(tm.length() == 8);
            String[] tmp = StringUtils.split(tm, ":");
            Assert.isTrue(tmp != null && tmp.length == 3);
            return tmp[0].concat(tmp[1]).concat(tmp[2]);
        } else {
            return tm;
        }
    }

    public static String transferTmInterval2LongDateTmPart(String tm) {
        if (!StringUtils.isBlank(tm) && tm.length() == 8) {
            Assert.notNull(tm);
            Assert.isTrue(tm.length() == 8);
            return tm.substring(0, 2).concat(":").concat(tm.substring(2, 4)).concat(" - ").concat(tm.substring(4, 6)).concat(":").concat(tm.substring(6, 8));
        } else {
            return tm;
        }
    }

    public static String transfer2LongDateGbkDtPart(String dt) {
        if (!StringUtils.isBlank(dt) && dt.length() == 8) {
            Assert.notNull(dt);
            Assert.isTrue(dt.length() == 8);
            return dt.substring(0, 4).concat("年").concat(dt.substring(4, 6)).concat("月").concat(dt.substring(6)).concat("日");
        } else {
            return dt;
        }
    }

    public static String transfer2LongDateGbkMonthAndDay(String dt) {
        if (!StringUtils.isBlank(dt) && dt.length() == 8) {
            Assert.notNull(dt);
            Assert.isTrue(dt.length() == 8);
            return String.valueOf(Integer.parseInt(dt.substring(4, 6))).concat("月").concat(String.valueOf(Integer.parseInt(dt.substring(6)))).concat("日");
        } else {
            return dt;
        }
    }

    public static String transfer2ShortMonthAndDay(String dt) {
        if (!StringUtils.isBlank(dt) && dt.length() == 8) {
            Assert.notNull(dt);
            Assert.isTrue(dt.length() == 8);
            return dt.substring(4, 6).concat("-").concat(dt.substring(6));
        } else {
            return dt;
        }
    }

    public static String transfer2LongDateGbkTmPart(String tm) {
        if (!StringUtils.isBlank(tm) && tm.length() == 6) {
            Assert.notNull(tm);
            Assert.isTrue(tm.length() == 6);
            return tm.substring(0, 2).concat("时").concat(tm.substring(2, 4)).concat("分").concat(tm.substring(4)).concat("秒");
        } else {
            return tm;
        }
    }

    public static String transfer2LongDateGbkDateTime(String dateTime) {
        if (StringUtils.isBlank(dateTime)) {
            return " ";
        } else {
            return dateTime.length() != 14 ? dateTime : transfer2LongDateGbkDtPart(dateTime.substring(0, 8)).concat(" ").concat(transfer2LongDateGbkTmPart(dateTime.substring(8)));
        }
    }

    public static String transfer2FullDateStyle(String date, String time) {
        return transfer2LongDateDtPart(date).concat(" ").concat(transfer2LongDateTmPart(time));
    }

    public static String transfer2FullDateStyle(String dateTime) {
        if (StringUtils.isBlank(dateTime)) {
            return " ";
        } else {
            return dateTime.length() != 14 ? dateTime : transfer2FullDateStyle(dateTime.substring(0, 8), dateTime.substring(8));
        }
    }

    public static String transfer2ShortFullDateStyle(String aDateStr) {
        if (StringUtils.isBlank(aDateStr)) {
            return "";
        } else if (isLongDateStr(aDateStr)) {
            String[] dateStr = StringUtils.splitPreserveAllTokens(aDateStr, " ");
            return transfer2ShortDate(dateStr[0]).concat(transfer2ShortDateTmPart(dateStr[1]));
        } else {
            return aDateStr;
        }
    }

    public static final Date addYears(Date aDate, int amount) {
        return addTime(aDate, 1, amount);
    }

    public static final Date addMonths(Date aDate, int amount) {
        return addTime(aDate, 2, amount);
    }

    public static final Date addDays(Date aDate, int amount) {
        return addTime(aDate, 6, amount);
    }

    public static final String addDays(String aDate, int amount) {
        try {
            return toMailDateDtPartString(addTime(parseMailDateDtPartString(aDate), 6, amount));
        } catch (ParseException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static final Date addHours(Date aDate, int amount) {
        return addTime(aDate, 10, amount);
    }

    public static final Date addMinutes(Date aDate, int amount) {
        return addTime(aDate, 12, amount);
    }

    public static final Date addSeconds(Date aDate, int amount) {
        return addTime(aDate, 13, amount);
    }

    private static final Date addTime(Date aDate, int timeType, int amount) {
        if (aDate == null) {
            return null;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(aDate);
            cal.add(timeType, amount);
            return cal.getTime();
        }
    }

    public static String getYesterday() {
        return getYesterday(getCurrentDate());
    }

    public static String getYesterday(String currentDay) {
        return getNDaysAgo(currentDay, 1);
    }

    public static String getNDaysAgo(String theDay, int nDays) {
        return addDays(theDay, -nDays);
    }

    public static String getNDaysAgo(int nDays) {
        return getNDaysAgo(getCurrentDate(), nDays);
    }

    public static final String getUTCTime() {
        return getSpecifiedZoneTime(Calendar.getInstance().getTime(), TimeZone.getTimeZone("GMT+0"));
    }

    public static final String getUTCTime(Date aDate) {
        return getSpecifiedZoneTime(aDate, TimeZone.getTimeZone("GMT+0"));
    }

    public static final String getSpecifiedZoneTime(TimeZone tz) {
        return getSpecifiedZoneTime(Calendar.getInstance().getTime(), tz);
    }

    public static int getTimeStamp(String dateStr) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        int timeStampUnix = (int)(simpleDateFormat.parse(dateStr).getTime() / 1000L);
        return timeStampUnix;
    }

    public static final String getSpecifiedZoneTime(Date aDate, TimeZone tz) {
        if (aDate == null) {
            return "";
        } else {
            Assert.notNull(tz);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(tz);
            return sdf.format(aDate);
        }
    }

    public static final int getDifferenceMonths(Date startDate, Date endDate) {
        Assert.notNull(startDate);
        Assert.notNull(endDate);
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        return Math.abs((startCal.get(1) - endCal.get(1)) * 12 + (startCal.get(2) - endCal.get(2)));
    }

    public static final int getDifferenceMonths(String startDateStr, String endDateStr) {
        checkShortDateStr(startDateStr);
        checkShortDateStr(endDateStr);

        try {
            return getDifferenceMonths(parseShortDateString(startDateStr), parseShortDateString(endDateStr));
        } catch (ParseException var3) {
            throw new RuntimeException(var3);
        }
    }

    public static final int getDifferenceDays(String startDateStr, String endDateStr) {
        return (new Long(getDifferenceMillis(startDateStr, endDateStr) / NANO_ONE_DAY)).intValue();
    }

    public static final int getDifferenceDays2(String startDateStr, String endDateStr) {
        return (new Long(getDifferenceMillis(startDateStr, endDateStr, "yyyyMMdd") / NANO_ONE_DAY)).intValue();
    }

    public static final int getDaysSubtract(String startDateStr, String endDateStr) {
        return (new Long(getDaysSubtractMillis(startDateStr, endDateStr) / NANO_ONE_DAY)).intValue();
    }

    public static final int getDaysSubtract2(String startDateStr, String endDateStr) {
        return (new Long(getDaysSubtractMillis(startDateStr, endDateStr, "yyyyMMdd") / NANO_ONE_DAY)).intValue();
    }

    public static final long getDaysSubtractMillis(String startDateStr, String endDateStr) {
        return getDaysSubtractMillis(startDateStr, endDateStr, "yyyy-MM-dd");
    }

    public static final long getDaysSubtractMillis(String startDateStr, String endDateStr, String dateFormat) {
        try {
            return getDaysSubtractMillis(parser(startDateStr, dateFormat), parser(endDateStr, dateFormat));
        } catch (ParseException var4) {
            throw new RuntimeException(var4);
        }
    }

    public static final long getDaysSubtractMillis(Date startDate, Date endDate) {
        Assert.notNull(startDate);
        Assert.notNull(endDate);
        return endDate.getTime() - startDate.getTime();
    }

    public static final int getDaysSubtractSeconds(Date startDate, Date endDate) {
        Assert.notNull(startDate);
        Assert.notNull(endDate);
        return (new Long((endDate.getTime() - startDate.getTime()) / NANO_ONE_SECOND)).intValue();
    }

    public static final int getDifferenceDays(Date startDate, Date endDate) {
        return (new Long(getDifferenceMillis(startDate, endDate) / NANO_ONE_DAY)).intValue();
    }

    public static final long getDifferenceMillis(String startDateStr, String endDateStr) {
        return getDifferenceMillis(startDateStr, endDateStr, "yyyy-MM-dd");
    }

    public static final long getDifferenceMillis2(String startDateStr, String endDateStr) {
        return getDifferenceMillis(startDateStr, endDateStr, "yyyyMMddHHmmss");
    }

    public static final long getDifferenceMillis(String startDateStr, String endDateStr, String dateFormat) {
        try {
            return getDifferenceMillis(parser(startDateStr, dateFormat), parser(endDateStr, dateFormat));
        } catch (ParseException var4) {
            throw new RuntimeException(var4);
        }
    }

    public static final long getDifferenceMillis(Date startDate, Date endDate) {
        Assert.notNull(startDate);
        Assert.notNull(endDate);
        return Math.abs(endDate.getTime() - startDate.getTime());
    }

    public static final boolean isDateBetween(Date aDate, String minDateStr, String maxDateStr) {
        Assert.notNull(aDate);
        boolean ret = false;

        try {
            Date dMaxDate = null;
            Date dMinDate = null;
            dMaxDate = parseShortDateString(maxDateStr);
            dMinDate = parseShortDateString(minDateStr);
            switch((dMaxDate != null ? 5 : 3) + (dMinDate != null ? 1 : 0)) {
                case 4:
                    ret = aDate.after(dMinDate);
                    break;
                case 5:
                    ret = aDate.before(dMaxDate);
                    break;
                case 6:
                    ret = aDate.before(dMaxDate) && aDate.after(dMinDate);
            }
        } catch (ParseException var6) {
        }

        return ret;
    }

    public static final int getDaysInMonth(String aDateStr) {
        checkShortDateStr(aDateStr);

        try {
            return getDaysInMonth(parseShortDateString(aDateStr));
        } catch (ParseException var2) {
            throw new RuntimeException(var2);
        }
    }

    public static final int getDaysInMonth(Date aDate) {
        Assert.notNull(aDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        return cal.getActualMaximum(5);
    }

    public static final boolean isShortDateStr(String aDateStr) {
        try {
            parseShortDateString(aDateStr);
            return true;
        } catch (ParseException var2) {
            return false;
        }
    }

    public static final void checkShortDateStr(String aDateStr) {
        Assert.isTrue(isShortDateStr(aDateStr), "The str-'" + aDateStr + "' must match 'yyyy-MM-dd' format.");
    }

    public static final boolean isLongDateStr(String aDateStr) {
        try {
            parseLongDateString(aDateStr);
            return true;
        } catch (ParseException var2) {
            return false;
        }
    }

    public static final void checkLongDateStr(String aDateStr) {
        Assert.isTrue(isLongDateStr(aDateStr), "The str-'" + aDateStr + "' must match 'yyyy-MM-dd HH:mm:ss' format.");
    }

    public static final boolean isMailDateStr(String aDateStr) {
        try {
            parseMailDateString(aDateStr);
            return true;
        } catch (ParseException var2) {
            return false;
        }
    }

    public static final boolean isMailDateDtPartStr(String aDateStr) {
        try {
            parseMailDateDtPartString(aDateStr);
            return true;
        } catch (ParseException var2) {
            return false;
        }
    }

    public static final void checkMailDateStr(String aDateStr) {
        Assert.isTrue(isMailDateStr(aDateStr), "The str-'" + aDateStr + "' must match 'yyyyMMddHHmmss' format.");
    }

    public static final boolean isDateStrMatched(String aDateStr, String formatter) {
        try {
            parser(aDateStr, formatter);
            return true;
        } catch (ParseException var3) {
            return false;
        }
    }

    public static Date getFirstDayOfLastWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(4, -1);
        calendar.set(7, 1);
        return calendar.getTime();
    }

    public static Date getFirstDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(2, -1);
        calendar.set(5, 1);
        return calendar.getTime();
    }

    public static String getFirstDayOfLastWeekFormatShortDate() {
        return toFormatDateString(getFirstDayOfLastWeek(), "yyyyMMdd");
    }

    public static void main(String[] args) {
        String ss = "2021-08-26 16:09:09";
        System.out.println(transfer2ShortFullDateStyle(ss));
    }

    public static String getFirstDayOfLastMonthFormatShortDate() {
        return toFormatDateString(getFirstDayOfLastMonth(), "yyyyMMdd");
    }

    public static Date getLastDayOfLastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(5, 1);
        calendar.add(5, -1);
        return calendar.getTime();
    }

    public static List<String> getDayOfWeekWithinDate(String dataBegin, String dataEnd, int weekDays) {
        return getDayOfWeekWithinDateInterval(transfer2LongDateDtPart(dataBegin), transfer2LongDateDtPart(dataEnd), weekDays);
    }

    public static List<String> getDayOfWeekWithinDateInterval(String dataBegin, String dataEnd, int weekDays) {
        if (!StringUtils.isBlank(dataBegin) && !StringUtils.isBlank(dataEnd) && weekDays >= 0 && weekDays <= 6) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<String> dateResult = new ArrayList();
            Calendar cal = Calendar.getInstance();
            String[] dateInterval = new String[]{dataBegin, dataEnd};
            Date[] dates = new Date[dateInterval.length];

            for(int i = 0; i < dateInterval.length; ++i) {
                String[] ymd = dateInterval[i].split("[^\\d]+");
                cal.set(Integer.parseInt(ymd[0]), Integer.parseInt(ymd[1]) - 1, Integer.parseInt(ymd[2]));
                dates[i] = cal.getTime();
            }

            for(Date date = dates[0]; date.compareTo(dates[1]) <= 0; date = cal.getTime()) {
                cal.setTime(date);
                if (cal.get(7) - 1 == weekDays) {
                    String format = sdf.format(date);
                    dateResult.add(transfer2ShortDate(format));
                }

                cal.add(5, 1);
            }

            return dateResult;
        } else {
            return null;
        }
    }

    private DateUtils() {
    }

    static {
        NANO_ONE_MINUTE = 60L * NANO_ONE_SECOND;
        NANO_ONE_HOUR = 60L * NANO_ONE_MINUTE;
        NANO_ONE_DAY = 24L * NANO_ONE_HOUR;
    }
}
