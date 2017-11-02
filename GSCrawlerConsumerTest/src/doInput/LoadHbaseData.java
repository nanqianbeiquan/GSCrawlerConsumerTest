package doInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.mortbay.log.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import sys.DataUtil;
import update.UpdateNeo4j;


public class LoadHbaseData {
	public static void loadData(String json) throws Exception{
		json=json.replace("(", "（").replace(")", "）");
		String tableName="GSTest";
		HbaseDaoImpl dao=new HbaseDaoImpl();
		DateConvert datautil=new DateConvert();
		Parsemoney parsemoney=new Parsemoney();
		String searchKeyname="";
		String searchShareholdername="";
		String companyName="";
		List<String> listPerson=new ArrayList<String>();
		List<String> listShare=new ArrayList<String>();
		Map<String,String> investor=new HashMap<String,String>();
		investor.put("Investor_Info:investor_certificationno","Shareholder_Info:shareholder_certificationno");
		investor.put("Investor_Info:investor_name","Shareholder_Info:shareholder_name");
		investor.put("Investor_Info:id","Shareholder_Info:id");
		investor.put("Investor_Info:investor_type","Shareholder_Info:shareholder_type");
		investor.put("Investor_Info:investor_details","Shareholder_Info:shareholder_details");
		investor.put("Investor_Info:ivt_subscripted_capital","Shareholder_Info:subscripted_capital");
		investor.put("Investor_Info:investor_certificationtype","Shareholder_Info:shareholder_certificationtype");
		investor.put("Investor_Info:enterprisename","Shareholder_Info:enterprisename");
		investor.put("Investor_Info:registrationno","Shareholder_Info:registrationno");
		investor.put("Investor_Info:investment_method","Shareholder_Info:subscripted_method");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
	    	JSONObject jsonobj=JSONObject.parseObject(json);
			companyName=jsonobj.getString("inputCompanyName");
			if(jsonobj.containsKey("Registered_Info")){
				/****
				 * 行政处罚  是通过行政处罚编号
				 */
				if(jsonobj.containsKey("Administrative_Penalty")){
					JSONArray business=jsonobj.getJSONArray("Administrative_Penalty");
	                if(business.size()>0){
						List<Put> addbulist=new ArrayList<Put>();
						Scan scan=new Scan();
						scan.addFamily(Bytes.toBytes("Administrative_Penalty"));
						scan.setStartRow(Bytes.toBytes(companyName+"_01"));
						scan.setStopRow(Bytes.toBytes(companyName+"_99"));
						ResultScanner resultbu=dao.commScanMethods(tableName,scan);
						Map<String,byte[]> hashmap=new HashMap<String,byte[]>();
						for(Result r:resultbu){
							String penalty_code=Bytes.toString(r.getValue(Bytes.toBytes("Administrative_Penalty"), Bytes.toBytes("penalty_code")));
							hashmap.put(penalty_code,r.getRow());
						}
						for(int i=0;i<business.size();i++){
							JSONObject t = business.getJSONObject(i);
							Put p=new Put(Bytes.toBytes((String)t.get("rowkey")));
							String penalty_code=(String)t.get("Administrative_Penalty:penalty_code");
							if(hashmap.containsKey(penalty_code)){
								p=new Put(hashmap.get(penalty_code));
							}
							Iterator it=t.keySet().iterator();
							while(it.hasNext()){
								String key=it.next().toString();
								String value=t.get(key)==null ? "":t.get(key).toString();
								if(!key.equals("rowkey")){
									p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
								}
							}
							addbulist.add(p);
						}
						dao.commPutMethods(tableName, addbulist);
					}
				}
				
				/****
				 * 经营异常  先删除这个公司所有的经营异常，在添加
				 */
				if(jsonobj.containsKey("Business_Abnormal")){
					JSONArray business=jsonobj.getJSONArray("Business_Abnormal");
	                if(business.size()>0){
						List<Put> addbulist=new ArrayList<Put>();
						Scan scan=new Scan();
						scan.addFamily(Bytes.toBytes("Business_Abnormal"));
						scan.setStartRow(Bytes.toBytes(companyName+"_01"));
						scan.setStopRow(Bytes.toBytes(companyName+"_99"));
						Map<String,byte[]> hashmap=new HashMap<String,byte[]>();
						ResultScanner resultbu=dao.commScanMethods(tableName,scan);
						for(Result r:resultbu){
							String business_event=Bytes.toString(r.getValue(Bytes.toBytes("Business_Abnormal"), Bytes.toBytes("abnormal_events")));
							String abnormal_datesin=Bytes.toString(r.getValue(Bytes.toBytes("Business_Abnormal"), Bytes.toBytes("abnormal_datesin")));
							String date=abnormal_datesin==""|| abnormal_datesin.equals("") ? "" :datautil.evaluate(abnormal_datesin);
							hashmap.put(business_event+"_"+date,r.getRow());
							
						}
						for(int i=0;i<business.size();i++){
							JSONObject t = business.getJSONObject(i);
							Put p=new Put(Bytes.toBytes(t.get("rowkey").toString()));
							String event=(String)t.get("Business_Abnormal:abnormal_events");
							String datesin=(String)t.get("Business_Abnormal:abnormal_datesin");
							String date=datesin==""|| datesin.equals("") ? "" :datautil.evaluate(datesin);
							if(hashmap.containsKey(event+"_"+date)){	
								p=new Put(hashmap.get(event+"_"+date));
							}
							Iterator it= t.keySet().iterator();
								while(it.hasNext()){
									String key=it.next().toString();
									String value=t.get(key)==null ? "":t.get(key).toString();
									if(!key.equals("rowkey")){
										p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
									}
								}
							addbulist.add(p);
						}
						dao.commPutMethods(tableName, addbulist);
						
					}
				}
				/***
				 * 抽查检查信息  先删除这个公司全部抽查检查，在添加
				 */
				if(jsonobj.containsKey("Spot_Check")){
					JSONArray business=jsonobj.getJSONArray("Spot_Check");
	                if(business.size()>0){
						List<Put> addbulist=new ArrayList<Put>();
						List<Delete> deletebulist=new ArrayList<Delete>();
						Scan scan=new Scan();
						scan.addFamily(Bytes.toBytes("Spot_Check"));
						scan.setStartRow(Bytes.toBytes(companyName+"_01"));
						scan.setStopRow(Bytes.toBytes(companyName+"_99"));
						ResultScanner resultbu=dao.commScanMethods(tableName,scan);
						for(int i=0;i<business.size();i++){
							JSONObject t = business.getJSONObject(i);
							Put p=new Put(Bytes.toBytes(t.get("rowkey").toString()));
										Iterator it= t.keySet().iterator();
							while(it.hasNext()){
								String key=it.next().toString();
								String value=t.get(key)==null ? "":t.get(key).toString();
								if(!key.equals("rowkey")){
									p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
								}
							}
							addbulist.add(p);
						}
						for(Result r:resultbu){
							Delete delete=new Delete(r.getRow());
							deletebulist.add(delete);
						}
						dao.commDeleteMethods(tableName, deletebulist);
						dao.commPutMethods(tableName, addbulist);
						
						
					}
				}
				/****
				 * 股东信息 
				 * 
				 */
				 if(jsonobj.containsKey("Shareholder_Info")){
					 JSONArray business=new JSONArray();
					 try{
						 business=jsonobj.getJSONArray("Shareholder_Info");
					 }catch(Exception e){
						 System.out.println(companyName+" has no shareholder");
					 }						
		                if(business!=null&&!business.equals("null")&&!business.equals("Null")&&!business.equals("NULL")&&business.size()>0){
							DeleteData.deleteHbaseData(companyName, tableName, "Shareholder_Info", dao);
							List<Put> addbulist=new ArrayList<Put>();
							Set<String> set=new HashSet<String>();
							for(int i=0;i<business.size();i++){
								JSONObject t = business.getJSONObject(i);
								Put p=new Put(Bytes.toBytes(t.get("rowkey").toString()));
								searchShareholdername+=(String)t.get("Shareholder_Info:shareholder_name");
								searchShareholdername+=" ";
								String	shareholder_certificationtype=t.get("Shareholder_Info:shareholder_certificationtype")==null ? "":t.get("Shareholder_Info:shareholder_certificationtype").toString();
								String  actualpaid_capital=t.get("Shareholder_Info:actualpaid_capital")==null ? "":t.get("Shareholder_Info:actualpaid_capital").toString();
								String ne4jshare=(String)t.get("Shareholder_Info:shareholder_name")+"|"+(String)t.get("Shareholder_Info:shareholder_type")+"|"+shareholder_certificationtype+"|"+actualpaid_capital+"|";
								listShare.add(ne4jshare.replace("\\", "/").replace("'", "\\\\'"));
								Iterator it= t.keySet().iterator();
								set.add((String)t.get("Shareholder_Info:shareholder_name")+"_"+(String)t.get("Shareholder_Info:shareholder_type"));
								while(it.hasNext()){
									String key=it.next().toString();
									String value=t.get(key)==null ? "":t.get(key).toString();
									if(!key.equals("rowkey")){
										if(key.equals("Shareholder_Info:subscripted_capital")){
											String money="";
											Pattern d=Pattern.compile("(\\d+\\.\\d+)|(\\d+)"); 
											  Matcher m=d.matcher(value);
											  String zb="";
											  if(m.find()){
												  zb=m.group(0);
											  } 
											    String regex="([\\u4e00-\\u9fa5]+)";
										    	Matcher matcher = Pattern.compile(regex).matcher(value);
										    	if(matcher.find()){
										    		money= parsemoney.evaluate(zb,matcher.group(1)); 
										    	}else{
										    		money= parsemoney.evaluate(zb,"万元"); 
										    	}
											p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
											p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes("resubscripted"), Bytes.toBytes(money));
		
										}else{
											p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
										}
									}
								}
								addbulist.add(p);
							}
							dao.commPutMethods(tableName, addbulist);
						} 
//				    }else{
//						DeleteData.deleteHbaseData(companyName, tableName, "Shareholder_Info", dao);
				    }

				/****
				 * 主要人员 先删除后添加
				 */
				if(jsonobj.containsKey("KeyPerson_Info")){
					JSONArray business=new JSONArray();
					try{
						business=jsonobj.getJSONArray("KeyPerson_Info");						
					}catch(Exception e){
						System.out.println(companyName+" has no KeyPerson");
					}				
	                if(business!=null&&!business.equals("null")&&!business.equals("Null")&&!business.equals("NULL")&&business.size()>0){
						DeleteData.deleteHbaseData(companyName, tableName, "KeyPerson_Info", dao);
						List<Put> addbulist=new ArrayList<Put>();
						for(int i=0;i<business.size();i++){
							JSONObject t = business.getJSONObject(i);
							Put p=new Put(Bytes.toBytes(t.get("rowkey").toString()));
							searchKeyname+=(String)t.get("KeyPerson_Info:keyperson_name");
							searchKeyname+=" ";
							String ne4jkeyperson=(String)t.get("KeyPerson_Info:keyperson_name")+"|"+(String)t.get("KeyPerson_Info:keyperson_position");
							listPerson.add(ne4jkeyperson.replace("\\", "/").replace("'", "\\\\'"));
							Iterator it= t.keySet().iterator();
							while(it.hasNext()){
								String key=it.next().toString();
								String value=t.get(key)==null ? "":t.get(key).toString();
								if(!key.equals("rowkey")){
									p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
								}
							}
							addbulist.add(p);
						}
						dao.commPutMethods(tableName, addbulist);
//					}else{
//						DeleteData.deleteHbaseData(companyName, tableName, "KeyPerson_Info", dao);
					}
//				}else{
//					DeleteData.deleteHbaseData(companyName, tableName, "KeyPerson_Info", dao);
				}
				/***
				 * 分支机构 先删除后添加
				 */
				if(jsonobj.containsKey("Branches")){
					JSONArray business=jsonobj.getJSONArray("Branches");
	                if(business.size()>0){
						List<Put> addbulist=new ArrayList<Put>();
						List<Delete> deletebulist=new ArrayList<Delete>();
						Scan scan=new Scan();
						scan.addFamily(Bytes.toBytes("Branches"));
						scan.setStartRow(Bytes.toBytes(companyName+"_01"));
						scan.setStopRow(Bytes.toBytes(companyName+"_99"));
						ResultScanner resultbu=dao.commScanMethods(tableName,scan);
						for(int i=0;i<business.size();i++){
							JSONObject t = business.getJSONObject(i);
							Put p=new Put(Bytes.toBytes((String)t.get("rowkey")));
							Iterator it= t.keySet().iterator();
							while(it.hasNext()){
								String key=it.next().toString();
								String value=t.get(key)==null ? "":t.get(key).toString();
								if(!key.equals("rowkey")){
									p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
								}
							}
							addbulist.add(p);
							
						}
						for(Result r:resultbu){
							Delete delete=new Delete(r.getRow());
							deletebulist.add(delete);
						}
						dao.commDeleteMethods(tableName, deletebulist);
						dao.commPutMethods(tableName, addbulist);
						
						
					}
				}
				/***
				 * 清算信息表
				 */
				if(jsonobj.containsKey("liquidation_Information")){
					JSONArray business=jsonobj.getJSONArray("liquidation_Information");
	                if(business.size()>0){
						List<Put> addbulist=new ArrayList<Put>();
						List<Delete> deletebulist=new ArrayList<Delete>();
						Scan scan=new Scan();
						scan.addFamily(Bytes.toBytes("liquidation_Information"));
						scan.setStartRow(Bytes.toBytes(companyName+"_01"));
						scan.setStopRow(Bytes.toBytes(companyName+"_99"));
						ResultScanner resultbu=dao.commScanMethods(tableName,scan);
						for(int i=0;i<business.size();i++){
							JSONObject t = business.getJSONObject(i);
							if(t.containsKey("rowkey")){
								Put p=new Put(Bytes.toBytes(t.get("rowkey").toString()));
											Iterator it= t.keySet().iterator();
								while(it.hasNext()){
									String key=it.next().toString();
									String value=t.get(key)==null ? "":t.get(key).toString();
									if(!key.equals("rowkey")){
										p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
									}
								}
								addbulist.add(p);
							}
						}
						for(Result r:resultbu){
							Delete delete=new Delete(r.getRow());
							deletebulist.add(delete);
						}
						dao.commDeleteMethods(tableName, deletebulist);
						dao.commPutMethods(tableName, addbulist);
						
					
					}
				}
				/*****
				 * 变更信息
				 */
				if(jsonobj.containsKey("Changed_Announcement")){
					JSONArray business=jsonobj.getJSONArray("Changed_Announcement");
	                if(business.size()>0){
						List<Put> addbulist=new ArrayList<Put>();
						Scan scan=new Scan();
						scan.addFamily(Bytes.toBytes("Changed_Announcement"));
						scan.setStartRow(Bytes.toBytes(companyName+"_01"));
						scan.setStopRow(Bytes.toBytes(companyName+"_99"));
						ResultScanner resultbu=dao.commScanMethods(tableName,scan);
//						Map<String,byte[]> hashmap=new HashMap<String,byte[]>();
						Set<String> hbaseRecordSet=new HashSet<String>();
						List<Delete> dlist=new ArrayList<Delete>();
						/*****
						 * 判断爬虫的时间点
						 */
						Get g=new Get(Bytes.toBytes(companyName));
				        Result result=dao.commGetMethods("LengJingThirdPartInterfaceRecordTemp", g);
				        String gsdata="";
				        String qccdata="";
						if(result!=null && !result.equals("") && result.getRow()!=null){
							gsdata=Bytes.toString(result.getValue(Bytes.toBytes("LastUpdateTime"), Bytes.toBytes("crawler_gs")));
							qccdata=Bytes.toString(result.getValue(Bytes.toBytes("LastUpdateTime"), Bytes.toBytes("QCC")));
						}
						if((qccdata!=null && !qccdata.equals(""))||(gsdata!=null && !gsdata.equals("null")&& !gsdata.equals("") && DataUtil.compare_date(gsdata,"2017-03-15")>0)){
							for(Result r:resultbu){
								String event=Bytes.toString(r.getValue(Bytes.toBytes("Changed_Announcement"), Bytes.toBytes("changedannouncement_events")));
								String date=Bytes.toString(r.getValue(Bytes.toBytes("Changed_Announcement"), Bytes.toBytes("changedannouncement_date")));
								event=event.replace('(','（').replace(')','）');
								String changeafter=Bytes.toString(r.getValue(Bytes.toBytes("Changed_Announcement"), Bytes.toBytes("changedannouncement_after")));
								changeafter=changeafter.replace('(','（').replace(')','）');
								String changebefore=Bytes.toString(r.getValue(Bytes.toBytes("Changed_Announcement"), Bytes.toBytes("changedannouncement_before")));
								changebefore=changebefore.replace('(','（').replace(')','）');
								hbaseRecordSet.add(event+"|"+date+"|"+changebefore+"|"+changeafter);
//								hashmap.put(event+"_"+date,r.getRow());
							}
							for(int i=0;i<business.size();i++){
								JSONObject t = business.getJSONObject(i);
								String cralwerevent=t.get("Changed_Announcement:changedannouncement_events")==null ? "":t.get("Changed_Announcement:changedannouncement_events").toString();
								String cralwerdate=t.get("Changed_Announcement:changedannouncement_date")==null ? "":t.get("Changed_Announcement:changedannouncement_date").toString();
								String cralwerchangebefore=t.get("Changed_Announcement:changedannouncement_before")==null ? "":t.get("Changed_Announcement:changedannouncement_before").toString();
								String cralwerchangeafter=t.get("Changed_Announcement:changedannouncement_after")==null ? "":t.get("Changed_Announcement:changedannouncement_after").toString();
								cralwerevent=cralwerevent.replace('(','（').replace(')','）');
								try{
									cralwerdate=datautil.evaluate(cralwerdate);
								}catch(Exception e){
									
								}
								String cralwerResultStr=cralwerevent+"|"+cralwerdate+"|"+cralwerchangebefore+"|"+cralwerchangeafter;
								if(!hbaseRecordSet.contains(cralwerResultStr)){
									Put p=new Put(Bytes.toBytes((String)t.get("rowkey")));
									Iterator it= t.keySet().iterator();
									while(it.hasNext()){
										String key=it.next().toString();
										String value=t.get(key)==null ? "":t.get(key).toString();
										if(!key.equals("rowkey")){
												if(!key.equals("Changed_Announcement:changedannouncement_date")){
												      p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
												}else{
												      p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(datautil.evaluate(value)));
	
												}
										}	
									}
									addbulist.add(p);
								}
							}
							dao.commPutMethods(tableName, addbulist);
						}else{
							for(Result r:resultbu){
							    dlist.add(new Delete(r.getRow()));
							}
							for(int i=0;i<business.size();i++){
								JSONObject t = business.getJSONObject(i);
								Put p=new Put(Bytes.toBytes((String)t.get("rowkey")));
								Iterator it= t.keySet().iterator();
									while(it.hasNext()){
										String key=it.next().toString();
										String value=t.get(key)==null ? "":t.get(key).toString();
										if(!key.equals("rowkey")){
												if(!key.equals("Changed_Announcement:changedannouncement_date")){
												      p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
												}else{
												      p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(datautil.evaluate(value)));
	
												}
										}
									
								}
								addbulist.add(p);
							}
							dao.commDeleteMethods(tableName, dlist);
							dao.commPutMethods(tableName, addbulist);
						}	
					}
				}
				/*****
				 * 股权出资  通过股权出资编号
				 */
				if(jsonobj.containsKey("Equity_Pledge")){
					JSONArray businessep=jsonobj.getJSONArray("Equity_Pledge");
	                if(businessep.size()>0){
						List<Put> addbulist=new ArrayList<Put>();
						Scan scan=new Scan();
						scan.addFamily(Bytes.toBytes("Equity_Pledge"));
						scan.setStartRow(Bytes.toBytes(companyName+"_01"));
						scan.setStopRow(Bytes.toBytes(companyName+"_99"));
						ResultScanner resultbu=dao.commScanMethods(tableName,scan);
						Map<String,byte[]> hashmap=new HashMap<String,byte[]>();
						for(Result r:resultbu){
							String equitypledgeno=Bytes.toString(r.getValue(Bytes.toBytes("Equity_Pledge"), Bytes.toBytes("equitypledge_no")));
							hashmap.put(equitypledgeno,r.getRow());	
						}
						for(int i=0;i<businessep.size();i++){
							JSONObject t = businessep.getJSONObject(i);
							Put p=new Put(Bytes.toBytes((String)t.get("rowkey")));
							String equitypledgeno="";
							if(t.containsKey("Equity_Pledge:equitypledge_no")){
								equitypledgeno=t.get("Equity_Pledge:equitypledge_no").toString();
							}
							if(hashmap.containsKey(equitypledgeno)){
								 p=new Put(hashmap.get(equitypledgeno));
							}
							Iterator it= t.keySet().iterator();
							while(it.hasNext()){
								String key=it.next().toString();
								String value=t.get(key)==null ? "":t.get(key).toString();
								if(!key.equals("rowkey")){
									p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
								}
							}	
							addbulist.add(p);
						}
						dao.commPutMethods(tableName, addbulist);
					}
				}
				
				/*****
				 * 动产抵押 是通过动产抵押编号判断
				 */
				if(jsonobj.containsKey("Chattel_Mortgage")){
					JSONArray businesscm=jsonobj.getJSONArray("Chattel_Mortgage");
	                if(businesscm.size()>0){
						List<Put> addbulist=new ArrayList<Put>();
						Scan scan=new Scan();
						scan.addFamily(Bytes.toBytes("Chattel_Mortgage"));
						scan.setStartRow(Bytes.toBytes(companyName+"_01"));
						scan.setStopRow(Bytes.toBytes(companyName+"_99"));
						ResultScanner resultbu=dao.commScanMethods(tableName,scan);
						Map<String,byte[]> hashmap=new HashMap<String,byte[]>();
						for(Result r:resultbu){
							String chattelmortgage_registrationno=Bytes.toString(r.getValue(Bytes.toBytes("Chattel_Mortgage"), Bytes.toBytes("chattelmortgage_registrationno")));
							hashmap.put(chattelmortgage_registrationno,r.getRow());
							
						}
						for(int i=0;i<businesscm.size();i++){
							JSONObject t = businesscm.getJSONObject(i);
							Put p=new Put(Bytes.toBytes(t.get("rowkey").toString()));
							String chattelmortgage_registrationno=(String)t.get("Chattel_Mortgage:chattelmortgage_registrationno");
							if(hashmap.containsKey(chattelmortgage_registrationno)){
								p=new Put(hashmap.get(chattelmortgage_registrationno));
							}
								Iterator it= t.keySet().iterator();
								while(it.hasNext()){
									String key=it.next().toString();
									String value=t.get(key)==null ? "":t.get(key).toString();
									if(!key.equals("rowkey")){
										p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
									}
								}
							addbulist.add(p);
						}
						dao.commPutMethods(tableName, addbulist);
						
					}
				}
				
				/****
				 * 投资人信息
				 */
				/*if(jsonobj.containsKey("Investor_Info")){
					JSONArray jsonarray=jsonobj.getJSONArray("Investor_Info");
			         if(jsonarray.size()>0){
							List<Put> addbulist=new ArrayList<Put>();
							List<Delete> deletebulist=new ArrayList<Delete>();
							Scan scan=new Scan();
							scan.addFamily(Bytes.toBytes("Shareholder_Info"));
							scan.setStartRow(Bytes.toBytes(companyName+"_01"));
							scan.setStopRow(Bytes.toBytes(companyName+"_99"));
							ResultScanner resultbu=dao.commScanMethods(tableName,scan);
							Set<String> set=new HashSet<String>();
							for(int i=0;i<jsonarray.size();i++){
								JSONObject t = jsonarray.getJSONObject(i);
								Put p=new Put(Bytes.toBytes(t.get("rowkey").toString()));
								searchShareholdername+=(String)t.get("Investor_Info:investor_name");
								searchShareholdername+=" ";
								String	shareholder_certificationtype=t.get("Investor_Info:investor_type")==null ? "":t.get("Investor_Info:investor_type").toString();
								String  actualpaid_capital=t.get("Investor_Info:ivt_subscripted_capital")==null ? "":t.get("Investor_Info:ivt_subscripted_capital").toString();
								listShare.add((String)t.get("Investor_Info:investor_name")+"|"+(String)t.get("Investor_Info:investor_type")+"|"+shareholder_certificationtype+"|"+actualpaid_capital+"|"+"");
								Iterator it= t.keySet().iterator();
								set.add((String)t.get("Investor_Info:investor_name")+"_"+(String)t.get("Shareholder_Info:shareholder_type"));
								while(it.hasNext()){
									String key=it.next().toString();
									String value=t.get(key)==null ? "":t.get(key).toString();
									if(!key.equals("rowkey")){
										if(key.equals("Investor_Info:ivt_subscripted_capital")){
											String money="";
											Pattern d=Pattern.compile("(\\d+\\.\\d+)|(\\d+)");
											  Matcher m=d.matcher(value);
											  String zb="";
											  if(m.find()){
												  zb=m.group(0);
											  } 
											    String regex="([\\u4e00-\\u9fa5]+)";
										    	Matcher matcher = Pattern.compile(regex).matcher(value);
										    	if(matcher.find()){
										    		money= parsemoney.evaluate(zb,matcher.group(1)); 
										    	}else{
										    		money= parsemoney.evaluate(zb,"万元"); 
										    	}
											p.add(Bytes.toBytes(investor.get(key).split(":")[0]),Bytes.toBytes(investor.get(key).split(":")[1]), Bytes.toBytes(value));
											p.add(Bytes.toBytes(investor.get(key).split(":")[0]),Bytes.toBytes("resubscripted"),Bytes.toBytes(money));
	
										}else{
											p.add(Bytes.toBytes(investor.get(key).split(":")[0]),Bytes.toBytes(investor.get(key).split(":")[1]), Bytes.toBytes(value));
										}
									}
								}
								addbulist.add(p);
							}
							for(Result r:resultbu){
								Delete delete=new Delete(r.getRow());
								deletebulist.add(delete);
							}
							dao.commDeleteMethods(tableName, deletebulist);
							dao.commPutMethods(tableName, addbulist);
						}
	
				}*/
				if(jsonobj.containsKey("Registered_Info")){
					JSONArray jsonarray=jsonobj.getJSONArray("Registered_Info");
					Scan scan=new Scan();
					String rowkey="";
					String businessscope="";
					String registrationstatus="";
					String establishmentdate="";
					String legalrepresentative="";
					String residenceaddress="";
					String registrationno="";
					String zczb="";
					String entstatus=""; 
					scan.addFamily(Bytes.toBytes("Registered_Info"));
					scan.setStartRow(Bytes.toBytes(companyName+"_01"));
					scan.setStopRow(Bytes.toBytes(companyName+"_99"));
					ResultScanner result=dao.commScanMethods(tableName,scan);
					JSONObject  t= jsonarray.getJSONObject(0);
						if(t.containsKey("Registered_Info:registeredcapital")){
							// Pattern p=Pattern.compile("(\\d+\\.\\d+|\\d+)");  //(\\d+\\.\\d+)|(\\d+)
							Pattern p=Pattern.compile("(\\d+\\.\\d+)|(\\d+)"); 
							String registeredcapital=t.get("Registered_Info:registeredcapital").toString().replace(",", "");
							  Matcher m=p.matcher(registeredcapital);
							  String zb="";
							  if(m.find()){
								  zb=m.group(0);
							  } 
							    String regex="[\u4e00-\u9fa5]+";
						    	Matcher matcher = Pattern.compile(regex).matcher(registeredcapital);
						    	if(matcher.find()){
						    		String reg = "[^\u4e00-\u9fa5]";   
						    		zczb= parsemoney.evaluate(zb,registeredcapital.replaceAll(reg,"").replaceAll(matcher.group(0), "")+matcher.group(0));   
						    	}
						}
						rowkey=(String)t.get("rowkey");
						if(t.containsKey("Registered_Info:businessscope")){
							businessscope=t.get("Registered_Info:businessscope")==null ? "":t.get("Registered_Info:businessscope").toString();
						}
						if(t.containsKey("Registered_Info:registrationstatus")){
							registrationstatus=t.get("Registered_Info:registrationstatus")==null ? "":t.get("Registered_Info:registrationstatus").toString();
						}
						if(t.containsKey("Registered_Info:establishmentdate")){
							establishmentdate=datautil.evaluate((String)t.get("Registered_Info:establishmentdate")).equals("") ? "" : datautil.evaluate((String)t.get("Registered_Info:establishmentdate"))+"T09:05:53.065Z";
						}
						if(t.containsKey("Registered_Info:legalrepresentative")){
							legalrepresentative=t.get("Registered_Info:legalrepresentative")==null ? "":t.get("Registered_Info:legalrepresentative").toString();
						}
						if(t.containsKey("Registered_Info:residenceaddress")){
						  	residenceaddress=t.get("Registered_Info:residenceaddress")==null ? "":t.get("Registered_Info:residenceaddress").toString();
						}
						if(t.containsKey("Registered_Info:registrationno")){
							registrationno=(String)t.get("Registered_Info:registrationno");
	
						}
						if(t.containsKey("Registered_Info:entstatus")){
							entstatus=t.get("Registered_Info:entstatus")==null ? "":t.get("Registered_Info:entstatus").toString();
						}
						Put p=new Put(Bytes.toBytes(rowkey));
						for(Result r:result){
							if(r.getRow()!=null){
							  p=new Put(r.getRow()); 
							}
							Get g=new Get(Bytes.toBytes(companyName));
					        Result resultgs=dao.commGetMethods("LengJingThirdPartInterfaceRecordTemp", g);
					        String gsredata="";
							if(resultgs!=null && !resultgs.equals("") && resultgs.getRow()!=null){
								gsredata=Bytes.toString(resultgs.getValue(Bytes.toBytes("LastUpdateTime"), Bytes.toBytes("crawler_gs")));
							}
							if(gsredata!=null && !gsredata.equals("null")&& !gsredata.equals("") && DataUtil.compare_date(gsredata,"2017-03-15")>0){
								
							}else{
							   dao.commDeleteMethods(tableName, new Delete(r.getRow()));
							}
						}
						Iterator it= t.keySet().iterator();
						while(it.hasNext()){
							String key=it.next().toString();
							String value=t.get(key)==null ? "":t.get(key).toString();
							if(!key.equals("rowkey") && !key.equals("Registered_Info:registeredcapital")){
								if(!key.equals("Registered_Info:establishmentdate")){
									p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(value));
								}else{
									p.add(Bytes.toBytes(key.split(":")[0]),Bytes.toBytes(key.split(":")[1]), Bytes.toBytes(datautil.evaluate(value)));
								}
							}
						}
						p.add(Bytes.toBytes("Registered_Info"),Bytes.toBytes("registeredcapital"), Bytes.toBytes(zczb));
						if(t.containsKey("Registered_Info:registeredcapital")){
							p.add(Bytes.toBytes("Registered_Info"),Bytes.toBytes("orgregisteredcapital"), Bytes.toBytes(t.get("Registered_Info:registeredcapital")+""));
						}else{
							p.add(Bytes.toBytes("Registered_Info"),Bytes.toBytes("orgregisteredcapital"), Bytes.toBytes(""));
						}
						dao.commPutMethods(tableName, p);
					
					/****
					 * 处理搜索问题
					 */
					Scan scansearch=new Scan();
					scansearch.addFamily(Bytes.toBytes("keyword"));
					scansearch.setStartRow(Bytes.toBytes(companyName+"_01"));
					scansearch.setStopRow(Bytes.toBytes(companyName+"_02"));
					ResultScanner resultSearch=dao.commScanMethods("search",scansearch);
					String serachkey="";
					for(Result r:resultSearch){
						serachkey=Bytes.toString(r.getRow());
						if(!serachkey.equals("")){
							 dao.commDeleteMethods("search", new Delete(Bytes.toBytes(serachkey)));
						}
					}
					Put psearch=null;
					if(serachkey.equals("")){
						psearch=new Put(Bytes.toBytes(rowkey));
					}else{
						psearch=new Put(Bytes.toBytes(serachkey));
					}							
					psearch.add(Bytes.toBytes("keyword"), Bytes.toBytes("businessscope"), Bytes.toBytes(businessscope));
					psearch.add(Bytes.toBytes("keyword"), Bytes.toBytes("registrationstatus"), Bytes.toBytes(registrationstatus));
					psearch.add(Bytes.toBytes("keyword"), Bytes.toBytes("establishmentdate"), Bytes.toBytes(establishmentdate));
					psearch.add(Bytes.toBytes("keyword"), Bytes.toBytes("keypersonname"), Bytes.toBytes(searchKeyname.trim()));
					psearch.add(Bytes.toBytes("keyword"), Bytes.toBytes("legalrepresentative"), Bytes.toBytes(legalrepresentative));
					psearch.add(Bytes.toBytes("keyword"), Bytes.toBytes("residenceaddress"), Bytes.toBytes(residenceaddress));
					psearch.add(Bytes.toBytes("keyword"), Bytes.toBytes("shareholdername"), Bytes.toBytes(searchShareholdername.trim()));
					psearch.add(Bytes.toBytes("keyword"), Bytes.toBytes("enterprisename"), Bytes.toBytes(companyName));
					if(zczb.equals("")){
						zczb="-100";
					}
					psearch.add(Bytes.toBytes("keyword"), Bytes.toBytes("registeredcapital"), Bytes.toBytes(zczb));
					
					dao.commPutMethods("search", psearch);
					
					/****
					 * 处理Ne4J代码
					 * 
					 */
					Map<String,Object> ne4jmap=new HashMap<String,Object>();
					NeoHelper ne=new NeoHelper();
					UpdateNeo4j updateNeo4j=new UpdateNeo4j();
					ne4jmap.put("companyName", companyName);
					ne4jmap.put("registrationno",registrationno);
					ne4jmap.put("establishmentdate", establishmentdate);
					ne4jmap.put("registeredcapital",zczb);
					ne4jmap.put("entstatus", entstatus);
					ne4jmap.put("frname", legalrepresentative);
					ne4jmap.put("personList", listPerson);
					ne4jmap.put("listShare", listShare);
					ne.pudateEntname(ne4jmap, updateNeo4j);
					ne.pudateStepRegistration(ne4jmap, updateNeo4j);
					ne.pudatePosition(ne4jmap, updateNeo4j);
					ne.pudateShare(ne4jmap, updateNeo4j);
					updateNeo4j.commit();
				}
		     /*  Put p=new Put(Bytes.toBytes(companyName));
		       p.add(Bytes.toBytes("LastUpdateTime"),Bytes.toBytes("crawler_gs"), Bytes.toBytes(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
		       dao.commPutMethods("LengJingThirdPartInterfaceRecordTemp2", p);*/
			
		       Put p1=new Put(Bytes.toBytes(companyName));
		       p1.add(Bytes.toBytes("LastUpdateTime"),Bytes.toBytes("crawler_gs"), Bytes.toBytes(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
		       dao.commPutMethods("LengJingThirdPartInterfaceRecordTemp", p1);
		       Log.info("companyname:"+":"+sdf.format(new Date())+":"+companyName);
		}
	       /****
	        * 更新成功后，进行回调数据
	        */
	        /*//现网环境
			String url="http://ljzd3.lengjing.info/updateStatusFeedback?companyName="+
						URLEncoder.encode(companyName, "GBK")+"&progress=1&type=GS";
			HttpConnectHelper hih=new HttpConnectHelper();
			hih.feedback(url);
			//试用环境
			String urlSHIYONG="http://172.16.0.100:8080/lengjing/updateStatusFeedback?companyName="+
						URLEncoder.encode(companyName, "GBK")+"&progress=1&type=GS";
			hih.feedback(urlSHIYONG);*/
		} catch (Exception e) {
			e.printStackTrace();
			//SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");			
			Log.info("errorcompanyname:"+sdf.format(new Date())+":"+companyName);
			
		/*String url="http://ljzd3.lengjing.info/updateStatusFeedback?companyName="+
					URLEncoder.encode(companyName, "GBK")+"&progress=false&type=GS";
		HttpConnectHelper hih=new HttpConnectHelper();
		hih.feedback(url);
		//试用环境
		String urlSHIYONG="http://172.16.0.100:8080/lengjing/updateStatusFeedback?companyName="+
					URLEncoder.encode(companyName, "GBK")+"&progress=false&type=GS";
		hih.feedback(urlSHIYONG);*/
		//测试环境
			
		}
	}
	public static void main(String[] args) throws Exception {
		String json="{\"inputCompanyName\": \"善林（上海）金融信息服务有限公司\", \"Changed_Announcement\": [{\"Changed_Announcement:id\": \"1\", \"Changed_Announcement:changedannouncement_after\": \"善林（上海）金融信息服务有限公司沧州黄河西路分公司;善林（上海）金融信息服务有限公司西安电子五路分公司;\", \"Changed_Announcement:changedannouncement_date\": \"2016年7月26日\", \"Changed_Announcement:enterprisename\": \"善林（上海）金融信息服务有限公司\", \"rowkey\": \"善林（上海）金融信息服务有限公司_05_91310115086217015E_201704281\", \"Changed_Announcement:changedannouncement_events\": \"分公司设立备案\", \"Changed_Announcement:registrationno\": \"91310115086217015E\", \"Changed_Announcement:changedannouncement_before\": \"善林（上海）金融信息服务有限公司沧州黄河西路分公司;\"}, {\"Changed_Announcement:id\": \"2\", \"Changed_Announcement:changedannouncement_after\": \"善林（上海）金融信息服务有限公司沧州黄河西路分公司;\", \"Changed_Announcement:changedannouncement_date\": \"2015年11月20日\", \"Changed_Announcement:enterprisename\": \"善林（上海）金融信息服务有限公司\", \"rowkey\": \"善林（上海）金融信息服务有限公司_05_91310115086217015E_201704282\", \"Changed_Announcement:changedannouncement_events\": \"分公司设立备案\", \"Changed_Announcement:registrationno\": \"91310115086217015E\", \"Changed_Announcement:changedannouncement_before\": \"无\"}, {\"Changed_Announcement:id\": \"3\", \"Changed_Announcement:changedannouncement_after\": \"2014-08-20章程修正案\", \"Changed_Announcement:changedannouncement_date\": \"2014年9月18日\", \"Changed_Announcement:enterprisename\": \"善林（上海）金融信息服务有限公司\", \"rowkey\": \"善林（上海）金融信息服务有限公司_05_91310115086217015E_201704283\", \"Changed_Announcement:changedannouncement_events\": \"章程修正案备案\", \"Changed_Announcement:registrationno\": \"91310115086217015E\", \"Changed_Announcement:changedannouncement_before\": \"无\"}, {\"Changed_Announcement:id\": \"4\", \"Changed_Announcement:changedannouncement_after\": \"周伯云;\", \"Changed_Announcement:changedannouncement_date\": \"2014年9月18日\", \"Changed_Announcement:enterprisename\": \"善林（上海）金融信息服务有限公司\", \"rowkey\": \"善林（上海）金融信息服务有限公司_05_91310115086217015E_201704284\", \"Changed_Announcement:changedannouncement_events\": \"投资人(股权)变更\", \"Changed_Announcement:registrationno\": \"91310115086217015E\", \"Changed_Announcement:changedannouncement_before\": \"周伯云;\"}, {\"Changed_Announcement:id\": \"5\", \"Changed_Announcement:changedannouncement_after\": \"120000.000000万人民币\", \"Changed_Announcement:changedannouncement_date\": \"2014年9月18日\", \"Changed_Announcement:enterprisename\": \"善林（上海）金融信息服务有限公司\", \"rowkey\": \"善林（上海）金融信息服务有限公司_05_91310115086217015E_201704285\", \"Changed_Announcement:changedannouncement_events\": \"注册资本(金)变更\", \"Changed_Announcement:registrationno\": \"91310115086217015E\", \"Changed_Announcement:changedannouncement_before\": \"8888.888800万人民币\"}, {\"Changed_Announcement:id\": \"6\", \"Changed_Announcement:changedannouncement_after\": \"中国（上海）自由贸易试验区基隆路1号裙楼3层B部位\", \"Changed_Announcement:changedannouncement_date\": \"2014年5月28日\", \"Changed_Announcement:enterprisename\": \"善林（上海）金融信息服务有限公司\", \"rowkey\": \"善林（上海）金融信息服务有限公司_05_91310115086217015E_201704286\", \"Changed_Announcement:changedannouncement_events\": \"住所变更\", \"Changed_Announcement:registrationno\": \"91310115086217015E\", \"Changed_Announcement:changedannouncement_before\": \"中国（上海）自由贸易试验区富特西一路477号4层B63室\"}, {\"Changed_Announcement:id\": \"7\", \"Changed_Announcement:changedannouncement_after\": \"金融信息服务（除金融许可业务），资产管理，投资管理，投资咨询、财务咨询（不得从事代理记账）、商务咨询、企业管理咨询（以上咨询均除经纪），市场信息咨询与调查（不得从事社会调查、社会调研、民意调查、民意测验），企业形象策划、会务会展服务、电子商务（不得从事增值电信、金融业务），设计、制作各类广告，接受金融机构委托从事金融信息技术外包，接受金融机构委托从事金融业务流程外包，接受金融机构委托从事金融知识流程外包，网络科技（除科技中介）和计算机技术领域内的技术开发、技术转让、技术咨询、技术服务，企业登记代理，证券、保险咨询（不得从事金融、证券、保险业务）。【依法须经批准的项目，经相关部门批准后方可开展经营活动】\", \"Changed_Announcement:changedannouncement_date\": \"2014年5月28日\", \"Changed_Announcement:enterprisename\": \"善林（上海）金融信息服务有限公司\", \"rowkey\": \"善林（上海）金融信息服务有限公司_05_91310115086217015E_201704287\", \"Changed_Announcement:changedannouncement_events\": \"经营范围变更\", \"Changed_Announcement:registrationno\": \"91310115086217015E\", \"Changed_Announcement:changedannouncement_before\": \"金融信息服务（除金融许可业务），资产管理，投资管理，投资咨询、财务咨询（不得从事代理记账）、商务咨询、企业管理咨询（以上咨询均除经纪），市场信息咨询与调查（不得从事社会调查、社会调研、民意调查、民意测验），企业形象策划、会务会展服务、电子商务（不得从事增值电信、金融业务），设计、制作各类广告，接受金融机构委托从事金融信息技术外包，接受金融机构委托从事金融业务流程外包，接受金融机构委托从事金融知识流程外包，网络科技（除科技中介）和计算机技术领域内的技术开发、技术转让、技术咨询、技术服务，企业登记代理，证券、保险咨询（不得从事金融、证券、保险业务）。【经营项目涉及行政许可的，凭许可证件经营】\"}], \"KeyPerson_Info\": [{\"KeyPerson_Info:keyperson_position\": \"执行董事\", \"KeyPerson_Info:registrationno\": \"91310115086217015E\", \"KeyPerson_Info:keyperson_name\": \"周伯云\", \"KeyPerson_Info:id\": \"1\", \"KeyPerson_Info:enterprisename\": \"善林（上海）金融信息服务有限公司\", \"rowkey\": \"善林（上海）金融信息服务有限公司_06_91310115086217015E_201704281\"}, {\"KeyPerson_Info:keyperson_position\": \"监事\", \"KeyPerson_Info:registrationno\": \"91310115086217015E\", \"KeyPerson_Info:keyperson_name\": \"高越强\", \"KeyPerson_Info:id\": \"2\", \"KeyPerson_Info:enterprisename\": \"善林（上海）金融信息服务有限公司\", \"rowkey\": \"善林（上海）金融信息服务有限公司_06_91310115086217015E_201704282\"}], \"Registered_Info\": [{\"Registered_Info:registrationno\": \"91310115086217015E\", \"Registered_Info:registeredcapital\": \"120000万人民币\", \"Registered_Info:validityfrom\": \"2013年12月14日\", \"Registered_Info:legalrepresentative\": \"周伯云\", \"Registered_Info:tyshxy_code\": \"91310115086217015E\", \"Registered_Info:approvaldate\": \"2013年12月14日\", \"Registered_Info:residenceaddress\": \"中国（上海）自由贸易试验区基隆路1号裙楼3层B部位\", \"Registered_Info:validityto\": \"2043年12月13日\", \"Registered_Info:establishmentdate\": \"2013年12月14日\", \"Registered_Info:province\": \"上海市\", \"rowkey\": \"善林（上海）金融信息服务有限公司_01_91310115086217015E_\", \"Registered_Info:enterprisetype\": \"有限责任公司(自然人独资)\", \"Registered_Info:registrationinstitution\": \"自贸区市场监督管理局\", \"Registered_Info:businessscope\": \"金融信息服务（除金融许可业务），资产管理，投资管理，投资咨询、财务咨询（不得从事代理记账）、贸易咨询、企业管理咨询（以上咨询均除经纪），市场信息咨询与调查（不得从事社会调查、社会调研、民意调查、民意测验），企业形象策划、会务会展服务、电子商务（不得从事增值电信、金融业务），设计、制作各类广告，接受金融机构委托从事金融信息技术外包，接受金融机构委托从事金融业务流程外包，接受金融机构委托从事金融知识流程外包，网络科技（除科技中介）和计算机技术领域内的技术开发、技术转让、技术咨询、技术服务，企业登记代理，证券、保险咨询（不得从事金融、证券、保险业务）。\n【依法须经批准的项目，经相关部门批准后方可开展经营活动】\", \"Registered_Info:registrationstatus\": \"存续（在营、开业、在册）\", \"Registered_Info:enterprisename\": \"善林（上海）金融信息服务有限公司\"}], \"Shareholder_Info\": [{\"Shareholder_Info:id\": 1, \"Shareholder_Info:registrationno\": \"91310115086217015E\", \"Shareholder_Info:shareholder_name\": \"周伯云\", \"Shareholder_Info:enterprisename\": \"善林（上海）金融信息服务有限公司\", \"Shareholder_Info:shareholder_certificationtype\": \"中华人民共和国居民身份证\", \"Shareholder_Info:shareholder_certificationno\": \"非公示项\", \"rowkey\": \"善林（上海）金融信息服务有限公司_04_91310115086217015E_201704281\", \"Shareholder_Info:shareholder_details\": \"\", \"Shareholder_Info:shareholder_type\": \"自然人股东\"}], \"taskId\": \"null\", \"accountId\": \"null\"}";
		loadData(json);
		
	}

}
