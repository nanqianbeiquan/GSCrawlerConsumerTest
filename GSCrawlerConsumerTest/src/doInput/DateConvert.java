package doInput;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConvert{
	
	
	public String evaluate(String changeDt) {
		String date="";
		SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2=new SimpleDateFormat("yyyy年M月d日");
		SimpleDateFormat sdf3=new SimpleDateFormat("yyyyMMdd");
		if(changeDt!=null && !changeDt.equals("null") && !changeDt.equals("") && changeDt.indexOf("年")>0){
			try{
				date=sdf1.format(sdf2.parse(changeDt)); 
			}catch (Exception e){
				date=changeDt;
			}
			return date;
		}else if(changeDt.indexOf("年")<0 && changeDt.indexOf(",")<0){
			if(changeDt.indexOf("-")>0){
				return changeDt;
			}else{
				try{
					date=sdf1.format(sdf3.parse(changeDt)); 
				}catch (Exception e){
					date=changeDt;
				}
				return date;
			}
		}else if(changeDt.indexOf(",")>0){
			  DateFormat formatFrom = new SimpleDateFormat("MMM dd,yyyy KK:mm:ss aa", Locale.ENGLISH);
			  try {
				Date d = formatFrom.parse(changeDt);
				DateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return formatTo.format(d);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			return "";
		}
		return date;
	}
	public static void main(String[] args) throws ParseException {
		String dateStr = "2015年12月31日";
		  /*DateFormat formatFrom = new SimpleDateFormat("MMM dd,yyyy KK:mm:ss aa", Locale.ENGLISH);
		  Date date = formatFrom.parse(dateStr);
		  DateFormat formatTo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  System.out.println(formatTo.format(date));*/
		DateConvert d =new DateConvert();
		System.out.println(d.evaluate(dateStr));
		
	}

}
