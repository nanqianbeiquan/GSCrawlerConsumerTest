package query;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.ws.rs.core.MediaType;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class Neo4j {
	//测试
	static String host="172.16.0.81";
	//正式
//	static String host="172.16.0.17";
	static int port=7474;
	static String user="neo4j";
	static String pwd="1qaz@WSX3edc";
	static String SERVER_ROOT_URI=String.format("http://%s:%d/db/data/",host,port);
	static HTTPBasicAuthFilter authFilter=new HTTPBasicAuthFilter(user,pwd);
	static String txUri = SERVER_ROOT_URI + "transaction/commit";
	static HashMap<String,String> cypherPoolMap= new HashMap<String,String>();
	static HashSet<String> updateMcSet=new HashSet<String>();	

	public static void addToUpdateMcSet(String mc)
	{
		updateMcSet.add(mc);
	}
	
	public static void addToPoolMap(String key,String cypher)
	{
		cypherPoolMap.put(key,cypher);
	}

	public static String execute(String query,String resultDataContents) throws Exception
	{
		return execute(query,resultDataContents,0);
	}
	
	public static String execute(String query,String resultDataContents,int t) throws Exception
	{
		try
		{
			WebResource resource = Client.create().resource(txUri);
			resource.addFilter(authFilter);
			String payload=null;
			
			if (resultDataContents.equals("graph"))
			{
//				payload = "{\"statements\": [{\"statement\" : \"" +query + "\",\"resultDataContents\" : [ \"row\", \"graph\" ]}]}";
				payload = "{\"statements\": [{\"statement\" : \"" +query + "\",\"resultDataContents\" : [ \"graph\" ]}]}";
			}
			else if (resultDataContents.equals("default"))
			{
				payload = "{\"statements\": [{\"statement\" : \"" +query + "\"}]}";
			}
			
			ClientResponse response = resource
			        .accept( MediaType.APPLICATION_JSON )
			        .type( MediaType.APPLICATION_JSON )
			        .entity(payload)
			        .post( ClientResponse.class );
			String res = response.getEntity(String.class);
			response.close();
			JSONObject resJsonObj=JSONObject.parseObject(res);
			JSONArray errors=resJsonObj.getJSONArray("errors");
			if(errors.size()>0)
			{
				throw new Exception(errors.getString(0));
			}
			return res;
		}
		catch (Exception e)
		{
			if(t==5)
			{
				throw e;
			}
			return execute(query,resultDataContents,t+1);
		}
	}
	
	public static String execute(List<String> queryList,String resultDataContents) throws Exception
	{
		return execute(queryList,resultDataContents,0);
	}
	
	public static String execute(List<String> queryList,String resultDataContents,int t) throws Exception
	{
		try
		{
			WebResource resource = Client.create().resource(txUri);
			resource.addFilter(authFilter);
			
			StringBuilder payload=new StringBuilder("{\"statements\": [");
			for(String cypher:queryList)
			{
//				System.out.println(cypher);
				if (resultDataContents.equals("graph"))
				{
//					payload = "{\"statements\": [{\"statement\" : \"" +query + "\",\"resultDataContents\" : [ \"row\", \"graph\" ]}]}";
					payload.append( "{\"statement\" : \"" +cypher + "\",\"resultDataContents\" : [ \"graph\" ]},");
				}
				else if (resultDataContents.equals("default"))
				{
					payload.append("{\"statement\":\""+cypher+"\"},");
				}
//				payload.append("{\"statement\":\""+cypher+"\"},");
			}
			if(payload.charAt(payload.length()-1)==',')
			{
				payload.setLength(payload.length()-1);
			}
			payload.append("]}");
//			System.out.println(payload);
			ClientResponse response = resource
			        .accept( MediaType.APPLICATION_JSON )
			        .type( MediaType.APPLICATION_JSON )
			        .entity(payload.toString())
			        .post( ClientResponse.class );
			String res = response.getEntity(String.class);
//			System.out.println(res);
			response.close();
			
			JSONObject resJsonObj=JSONObject.parseObject(res);
			JSONArray errors=resJsonObj.getJSONArray("errors");
			if(errors.size()>0)
			{
				throw new Exception(errors.getString(0));
			}
			return res;	
		}
		catch (Exception e)
		{
			if(t==5) throw e;
			return execute(queryList,resultDataContents,t+1);
		}
	}
}
