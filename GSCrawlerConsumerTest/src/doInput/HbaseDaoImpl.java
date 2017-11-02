package doInput;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;


public class HbaseDaoImpl{
	public static Configuration conf = new Configuration();
	
	public static HConnection  connection; 
	
	static{
		conf.set("hbase.zookeeper.quorum", "hadoop1,hadoop2,hadoop3");
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("fs.hdfs.impl", "org.apache.hadoop.hdfs.DistributedFileSystem");
		try {
			 connection = HConnectionManager.createConnection(conf);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void commPutMethods(String tableName,JSONObject jsonobj) throws IOException, JSONException{
		HTableInterface htable = (HTableInterface) connection.getTable(tableName);		
		Put put=new Put(Bytes.toBytes(jsonobj.getString("Rowkey")));
		String cfsAndClos=jsonobj.getString("clos");
		for(String eachCfAndClo:cfsAndClos.split(",")){
			put.add(Bytes.toBytes(eachCfAndClo.split(":")[0]),Bytes.toBytes(eachCfAndClo.split(":")[1]),Bytes.toBytes(eachCfAndClo.split(":")[2]));			
		}
		htable.put(put);	
		htable.close();
	} 
	
	public void commPutMethods(String tableName,List<Put> putlist) throws IOException{
		HTableInterface htable = (HTableInterface) connection.getTable(tableName);
		htable.put(putlist);	
		htable.close();
	}
	
	public void commPutMethods(String tableName,Put put) throws IOException{
		/*if(tableName.equals("LengJingThirdPartInterfaceRecordTemp")){
		}*/
		HTableInterface htable = connection.getTable(tableName);
		htable.put(put);	
		htable.close();
	} 
	
	public void commDeleteMethods(String tableName,List<Delete> dellist) throws IOException{
		HTableInterface htable = (HTableInterface) connection.getTable(tableName);
		htable.delete(dellist);	
		htable.close();
	}
	
	public void commDeleteMethods(String tableName,Delete del) throws IOException{
		HTableInterface htable = (HTableInterface) connection.getTable(tableName);
		htable.delete(del);	
		htable.close();
	}
	
	public ResultScanner commScanMethods(String tableName,Scan scan) throws IOException{
		HTableInterface htable = (HTableInterface) connection.getTable(tableName);
		ResultScanner rs=htable.getScanner(scan);
		htable.close();
		return rs;
	}
	
	public Result commGetMethods(String tableName,Get get) throws IOException{
		HTableInterface htable = (HTableInterface) connection.getTable(tableName);
		Result r=htable.get(get);
		htable.close();
		return r;
	}
	
}
