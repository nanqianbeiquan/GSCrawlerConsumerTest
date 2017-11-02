package query;

import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.lang.StringUtils;

public class Neo4jRestAPIClient {


	/**
	 * 拓扑图查询单公司查询初始结果
	 * 输入企业名称，返回一度内的所有关系
	 * @param companyName 公司名称
	 * @return 查询公司指定深度内的所有关系，json格式
	 * @throws Exception 
	 */
	public String function1(String companyName) throws Exception
	{
		String query=String.format("match p=(c:Company{公司名称:'%s'})-[*0..1]-(m) return p",companyName);
//		System.out.println(query);
		return Neo4j.execute(query,"graph");
	}
	
	/**
	 * 两家公司所有关系
	 * 查询两家企业之间的所有最短路径
	 * @param companyName1 公司名称
	 * @param companyName2 公司名称
	 * @param depth 最大深度
	 * @return 两个公司全部最短路径，json格式
	 * @throws Exception 
	 */
	public static String function2(String companyName1,String companyName2,int depth) throws Exception
	{
		if(depth>10)
		{
			depth=10;
		}
//		String query=String.format("match (c1:Company{公司名称:'%s'}),(c2:Company{公司名称:'%s'}),p=allShortestPaths(c1-[*0..%d]-c2) return p",companyName1,companyName2,depth);
		String query=String.format("with ['%s','%s'] as companyArr "
				+ "match p=allShortestPaths((c1:Company)-[*0..%d]-(c2:Company)) "
				+ "where c1.公司名称 in companyArr "
				+ "and c2.公司名称 in companyArr "
				+ "and id(c1)<=id(c2) "
				+ "return p",companyName1,companyName2,depth);
//		System.out.println(query);
		return Neo4j.execute(query,"graph");
	}
	
	/**
	 * 多公司关联查询
	 * 返回一批企业之间的最小关系网
	 * @param companyListStr 公司名列表，逗号分隔
	 * @param depth 查询最大深度
	 * @return 指定深度内包含所有查询公司的最小子图，json格式
	 * @throws Exception 
	 */
	public String function3(String companyListStr, int depth) throws Exception
	{
		if(depth>10)
		{
			depth=10;
		}
		companyListStr=companyListStr.replace(",","','");
		String query=String.format("with ['%s'] as companyArr "
				+ "match p=allShortestPaths((c1:Company)-[*0..%d]-(c2:Company)) "
				+ "where c1.公司名称 in companyArr "
				+ "and c2.公司名称 in companyArr "
				+ "and id(c1)<=id(c2) "
				+ "return p",companyListStr,depth);
//		System.out.println(query);
		return Neo4j.execute(query,"graph");
	}

	/**
	 * Description：节点裁剪功能，裁剪掉只与传入节点的相连的孤立点
	 * @param  nodeId 需要进行裁剪的节点id
	 * @param  relationships 图上所有的关系，n1-r1-n2,n1-r2-n3,...
	 * @return 待删除的节点id，逗号分隔
	 */
	public static String function7(String nodeId,String relationships)
	{
		String[] relationshipArr=StringUtils.split(relationships,",");
		HashSet<String> set1=new HashSet<String>();
		HashSet<String> set2=new HashSet<String>();
		
		for(String relationship:relationshipArr)
		{
			String[] idArr=StringUtils.split(relationship,"-");
			String nodeId1=idArr[0];
			String nodeId2=idArr[2];
	
			if(nodeId1.equals(nodeId))
			{
				set1.add(nodeId2);
			}
			else if(nodeId2.equals(nodeId))
			{
				set1.add(nodeId1);
			}
			else
			{
				set2.add(nodeId1);
				set2.add(nodeId2);
			}
		}
		set1.removeAll(set2);
		return StringUtils.join(set1,","); 
	}

	
	/**
	 * 下探功能
	 * 
	 * 每次下探都要触发棱镜一下的计算
	 * 
	 * 根据上一次的棱镜一下结果来
	 * 下探后自动触发棱镜一下
	 * 
	 * @param companyName
	 * @param nodeLabel
	 * @return
	 * @throws Exception 
	 */
	public String function10(String nodeIds) throws Exception
	{
		String[] idArr=nodeIds.split(",",2);
		String startNode=idArr[0];
		String otherNodes="";
		if(idArr.length>1)
		{
			otherNodes=idArr[1];
		}
		String queryTemplate="with [%s] as idArr "+
							"start n=node(%s) "+
							"match p=(n-[]-(n1)-[*0..1]-(n2)) "+
							"where id(n2)=id(n1) or id(n2) in idArr "+
							"return p";
		String query=String.format(queryTemplate,otherNodes, startNode);
//		System.out.println(query);
		return Neo4j.execute(query,"graph");
	}

	public static void main(String[] args) throws IOException
	{
		
	}
	
}
