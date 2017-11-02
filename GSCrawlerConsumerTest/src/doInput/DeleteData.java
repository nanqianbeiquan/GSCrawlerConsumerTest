package doInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;




import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;


public class DeleteData {
	public static void deleteHbaseData(String companyName,String tableName,String familyColumn,HbaseDaoImpl dao) throws IOException{
		List<Delete> deletebulist=new ArrayList<Delete>();
		Scan scan=new Scan();
		scan.addFamily(Bytes.toBytes(familyColumn));
		scan.setStartRow(Bytes.toBytes(companyName+"_01"));
		scan.setStopRow(Bytes.toBytes(companyName+"_99"));
		ResultScanner resultbu=dao.commScanMethods(tableName,scan);
		for(Result r:resultbu){
			Delete delete=new Delete(r.getRow());
			deletebulist.add(delete);
		}
		dao.commDeleteMethods(tableName, deletebulist);
	}
	

}
