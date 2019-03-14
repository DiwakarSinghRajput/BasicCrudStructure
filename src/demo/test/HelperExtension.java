package demo.test;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

public class HelperExtension {

	long Time = 0;

	public String getUniqueId() {
		return (new Date().getTime() + RandonNumber() + "");
	}

	public Timestamp getDateTime() {
		long serverTimeStamp = new Date().getTime() + Time;
		return new Timestamp(serverTimeStamp);
	}

	public Timestamp getDateTime(String string_timestamp) {

		SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(string_timestamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Timestamp(date.getTime());
	}

	public Date getDate(String date) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		Date sqlDate = null;
		try {
			Date utilDate = format.parse(date);
			sqlDate = new java.sql.Date(utilDate.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return sqlDate;
	}

	public int RandonNumber() {
		int randomNumber = (new Random().nextInt(9999 - 1000) + 1000);
		return randomNumber;
	}

	public Date timestampToDate(long time) {
		if (time == 0) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setTimeZone(TimeZone.getDefault());
		return new Date(time);
	}

	public Date timestampToDatePlusOneDay(long time) {
		time += 86400000L;
		SimpleDateFormat sdf = new SimpleDateFormat();
		sdf.setTimeZone(TimeZone.getDefault());
		return new Date(time);
	}

	public String millisToDate(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getDefault());
		cal.setTimeInMillis(time);
		return cal.get(Calendar.DAY_OF_MONTH) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.YEAR);
	}

	public String millisToDatePostcard(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getDefault());
		cal.setTimeInMillis(time);
		return cal.get(Calendar.DAY_OF_MONTH) + " " + getMonthStringAbbrivated(cal.get(Calendar.MONTH) + 1) + " "
				+ cal.get(Calendar.YEAR);
	}

	/*
	 * public String millisToDateYMD(long time) { Calendar cal =
	 * Calendar.getInstance(); cal.setTimeZone(TimeZone.getDefault());
	 * //cal.setTimeZone(TimeZone.getTimeZone("UTC")); cal.setTimeInMillis(time);
	 * return (cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" +
	 * (cal.get(Calendar.DAY_OF_MONTH) + 1)) ; }
	 */

	public boolean isNullOrEmpty(Object message) {
		if (message != null && message != "null") {
			if (!message.toString().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public String isNullString(String message) {
		if (message == null || message.equalsIgnoreCase("null")) {
			return "";
		}
		return message.toString();
	}

	public String isNullInt(String message) {
		if (message == null) {
			return "";
		}
		return message.toString();
	}

	public String getMonthStringAbbrivated(int month) {
		String monthString;
		switch (month) {
		case 1:
			monthString = "Jan";
			break;
		case 2:
			monthString = "Feb";
			break;
		case 3:
			monthString = "Mar";
			break;
		case 4:
			monthString = "Apr";
			break;
		case 5:
			monthString = "May";
			break;
		case 6:
			monthString = "Jun";
			break;
		case 7:
			monthString = "Jul";
			break;
		case 8:
			monthString = "Aug";
			break;
		case 9:
			monthString = "Sep";
			break;
		case 10:
			monthString = "Oct";
			break;
		case 11:
			monthString = "Nov";
			break;
		case 12:
			monthString = "Dec";
			break;
		default:
			monthString = "Invalid month";
			break;
		}
		return monthString;
	}

	public static String getCalculateGeneralDefect(String strBf, String strOcc, String strRec) {
		String result = "";
		Double doubleBf = 0.0, doubleOcc = 0.0, doubleRec = 0.0;
		HelperExtension helperExtension = new HelperExtension();
		if (!helperExtension.isNullOrEmpty(strBf)) {
			doubleBf = Double.parseDouble(strBf);
		}
		if (!helperExtension.isNullOrEmpty(strOcc)) {
			doubleOcc = Double.parseDouble(strOcc);
		}
		if (!helperExtension.isNullOrEmpty(strRec)) {
			doubleRec = Double.parseDouble(strRec);
		}
		System.out.println("rec" + strRec);
		System.out.println("bf: " + doubleBf + "occ: " + doubleOcc + "rec: " + doubleRec);
		result = "" + (doubleBf + doubleOcc - doubleRec);
		System.out.println(result);
		return result;
	}

	public String getExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.') + 1);

	}

	public Integer[] pagination(int cp, int numberOfPages, int itemCount) {
		int npg = (int) Math.ceil(Double.parseDouble("" + numberOfPages / itemCount + "." + numberOfPages % itemCount));

		Integer count[] = null;
		int x = 0;

		if (npg <= 5) {
			count = new Integer[npg];
			for (int i = 1; i <= npg; i++) {
				count[x] = i;
				x++;
			}
			return count;
		}
		if (npg > 5) {
			count = new Integer[5];
			if (cp == npg - 2) {
				for (int i = cp - 2; i <= npg; i++) {
					count[x] = i;
					x++;
				}
			}
			if (cp > npg - 2) {
				for (int i = npg - 4; i <= npg; i++) {
					count[x] = i;
					x++;
				}
			}
			if (cp <= 3) {
				for (int i = 1; i <= 5; i++) {
					count[x] = i;
					x++;
				}
			}
			if (cp < npg - 2 && cp > 3) {
				for (int i = cp - 2; i <= cp + 2; i++) {
					count[x] = i;
					x++;
				}
			}
			return count;
		}
		return count;
	}

	// Start Convert long to date
	public Date startingDate(long startDate) {
		Date date = new Date(startDate);
		Date satrtingDate = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String start = df.format(date) + " 00:00:01";
		DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			satrtingDate = inputFormatter.parse(start);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return satrtingDate;
	}

	public Date endingDate(long endDate) {
		Date date = new Date(endDate);
		Date satrtingDate = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String end = df.format(date) + " 23:59:59";
		DateFormat inputFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			satrtingDate = inputFormatter.parse(end);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return satrtingDate;
	}
	// End convert long to date

	// Get the maximum vaule
	public int maxValue(int startVal, int value) {
		int startValue = 0;
		if (value > startVal) {
			startValue = value;
		} else {
			startValue = startVal;
		}
		return startValue;
	}
}
