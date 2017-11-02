package doInput;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FileUtil {
  public static FileWriter createFile(String path) throws IOException{
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dNow = new Date();   //当前时间
		Date dBefore = new Date();
		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTime(dNow);//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -0);  //设置为前一天
		dBefore = calendar.getTime();   //得到前一天的时间
		String defaultStartDate = sdf.format(dBefore);    //格式化前一天
		//File f = new File("/home/zyx/hbase/dt/"+defaultStartDate);
		//f.mkdir();
	    File f = new File("d:\\yubing.lei\\Desktop\\hivetest");
        File filehive = new File(f,defaultStartDate);
        if(!filehive.exists()){
	        filehive.createNewFile();
	        return  new FileWriter(filehive,false);
        }else{
        	return new FileWriter(filehive,true);
        }

  }
}
