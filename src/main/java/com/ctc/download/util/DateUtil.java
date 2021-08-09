package com.ctc.download.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
	public static String AddDay(String strDate, int day,String format) {
		return AddDate(strDate,0,0,day,format);
	}	
	public static String AddDay(String strDate, int day) {
		return AddDate(strDate,0,0,day,"yyyyMMdd");
	}
	
	public static String AddDate(String strDate, int year, int month, int day) {
		return AddDate(strDate,year,month,day,"yyyyMMdd");
	}
	
	public static String AddDate(String strDate, int year, int month, int day,String format) {		
		SimpleDateFormat dtFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		Date dt;
		try {
			dt = dtFormat.parse(strDate);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		cal.setTime(dt);
		cal.add(Calendar.YEAR, year);
		cal.add(Calendar.MONTH, month);
		cal.add(Calendar.DATE, day);
		return dtFormat.format(cal.getTime());
	}
	
	public static String now(String format){		
		SimpleDateFormat dtFormat = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		return dtFormat.format(cal.getTime());
	}

}
