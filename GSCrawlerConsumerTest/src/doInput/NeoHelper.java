package doInput;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import update.UpdateNeo4j;

public class NeoHelper {
	public static String testStr="<?xml version=\"1.0\" encoding=\"UTF-8\"?><DATA><ORDERLIST><ITEM><UID>F12A149BC9ECA4C9</UID><ORDERNO>160629131602961365824</ORDERNO><KEY>游匣互动(北京)科技有限公司</KEY><KEYTYPE>2</KEYTYPE><STATUS>1</STATUS><FINISHTIME>2016-06-29 13:16:03</FINISHTIME></ITEM></ORDERLIST><BASIC><ITEM><ENTNAME>游匣互动(北京)科技有限公司</ENTNAME><FRNAME>张晓威</FRNAME><REGNO>110228018616764</REGNO><ORIREGNO></ORIREGNO><ORGCODES></ORGCODES><REGCAP>1100.000000</REGCAP><RECCAP>0.000000</RECCAP><REGCAPCUR>人民币</REGCAPCUR><ESDATE>2015-02-05</ESDATE><OPFROM>2015-02-05</OPFROM><OPTO>长期</OPTO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><ENTSTATUS>在营（开业）</ENTSTATUS><CHANGEDATE>2015-09-09</CHANGEDATE><CANDATE></CANDATE><REVDATE></REVDATE><DOM>北京市密云县太师屯镇太师屯村237号</DOM><ABUITEM>利用信息网络经营游戏产品（含网络游戏虚拟货币发行）；互联网信息服务。</ABUITEM><CBUITEM>技术开发、技术转让、技术服务；技术进出口；软件开发。</CBUITEM><OPSCOPE></OPSCOPE><OPSCOANDFORM></OPSCOANDFORM><ZSOPSCOPE>利用信息网络经营游戏产品（含网络游戏虚拟货币发行）；互联网信息服务。</ZSOPSCOPE><REGORG>北京市密云县工商行政管理局</REGORG><REGORGCODE>110228</REGORGCODE><REGORGPROVINCE>北京市</REGORGPROVINCE><ANCHEYEAR></ANCHEYEAR><ANCHEDATE></ANCHEDATE><ENTNAMEENG>游匣互动(北京)科技有限公司</ENTNAMEENG><CREDITCODE></CREDITCODE></ITEM></BASIC><SHAREHOLDER><ITEM><SHANAME>蒋涛</SHANAME><SUBCONAM>330.000000</SUBCONAM><REGCAPCUR>人民币</REGCAPCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>30.00%</FUNDEDRATIO><CONDATE>2054-02-02</CONDATE><INVTYPE>自然人股东</INVTYPE><COUNTRY>中国</COUNTRY><INVAMOUNT>7</INVAMOUNT><SUMCONAM>1100.000000</SUMCONAM><INVSUMFUNDEDRATIO>100.00%</INVSUMFUNDEDRATIO></ITEM><ITEM><SHANAME>段炜</SHANAME><SUBCONAM>330.000000</SUBCONAM><REGCAPCUR>人民币</REGCAPCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>30.00%</FUNDEDRATIO><CONDATE>2054-02-02</CONDATE><INVTYPE>自然人股东</INVTYPE><COUNTRY>中国</COUNTRY><INVAMOUNT>7</INVAMOUNT><SUMCONAM>1100.000000</SUMCONAM><INVSUMFUNDEDRATIO>100.00%</INVSUMFUNDEDRATIO></ITEM><ITEM><SHANAME>张晓威</SHANAME><SUBCONAM>198.000000</SUBCONAM><REGCAPCUR>人民币</REGCAPCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>18.00%</FUNDEDRATIO><CONDATE>2054-02-02</CONDATE><INVTYPE>自然人股东</INVTYPE><COUNTRY>中国</COUNTRY><INVAMOUNT>7</INVAMOUNT><SUMCONAM>1100.000000</SUMCONAM><INVSUMFUNDEDRATIO>100.00%</INVSUMFUNDEDRATIO></ITEM><ITEM><SHANAME>陈小芬</SHANAME><SUBCONAM>110.000000</SUBCONAM><REGCAPCUR>人民币</REGCAPCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>10.00%</FUNDEDRATIO><CONDATE>2054-02-02</CONDATE><INVTYPE>自然人股东</INVTYPE><COUNTRY>中国</COUNTRY><INVAMOUNT>7</INVAMOUNT><SUMCONAM>1100.000000</SUMCONAM><INVSUMFUNDEDRATIO>100.00%</INVSUMFUNDEDRATIO></ITEM><ITEM><SHANAME>周穗</SHANAME><SUBCONAM>44.000000</SUBCONAM><REGCAPCUR>人民币</REGCAPCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>4.00%</FUNDEDRATIO><CONDATE>2054-02-02</CONDATE><INVTYPE>自然人股东</INVTYPE><COUNTRY>中国</COUNTRY><INVAMOUNT>7</INVAMOUNT><SUMCONAM>1100.000000</SUMCONAM><INVSUMFUNDEDRATIO>100.00%</INVSUMFUNDEDRATIO></ITEM><ITEM><SHANAME>张嘉</SHANAME><SUBCONAM>44.000000</SUBCONAM><REGCAPCUR>人民币</REGCAPCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>4.00%</FUNDEDRATIO><CONDATE>2054-02-02</CONDATE><INVTYPE>自然人股东</INVTYPE><COUNTRY>中国</COUNTRY><INVAMOUNT>7</INVAMOUNT><SUMCONAM>1100.000000</SUMCONAM><INVSUMFUNDEDRATIO>100.00%</INVSUMFUNDEDRATIO></ITEM><ITEM><SHANAME>李子鑫</SHANAME><SUBCONAM>44.000000</SUBCONAM><REGCAPCUR>人民币</REGCAPCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>4.00%</FUNDEDRATIO><CONDATE>2054-02-02</CONDATE><INVTYPE>自然人股东</INVTYPE><COUNTRY>中国</COUNTRY><INVAMOUNT>7</INVAMOUNT><SUMCONAM>1100.000000</SUMCONAM><INVSUMFUNDEDRATIO>100.00%</INVSUMFUNDEDRATIO></ITEM></SHAREHOLDER><PERSON><ITEM><PERNAME>张晓威</PERNAME><POSITION>董事长</POSITION><SEX>男</SEX><NATDATE>1985</NATDATE></ITEM><ITEM><PERNAME>张嘉</PERNAME><POSITION>监事</POSITION><SEX>男</SEX><NATDATE>1980</NATDATE></ITEM><ITEM><PERNAME>段炜</PERNAME><POSITION>董事</POSITION><SEX>女</SEX><NATDATE>1951</NATDATE></ITEM><ITEM><PERNAME>蒋涛</PERNAME><POSITION>董事</POSITION><SEX>男</SEX><NATDATE>1970</NATDATE></ITEM><ITEM><PERNAME>张晓威</PERNAME><POSITION>经理</POSITION><SEX>男</SEX><NATDATE>1985</NATDATE></ITEM></PERSON><FRINV><ITEM><NAME>张晓威</NAME><PINVAMOUNT>2</PINVAMOUNT><ENTNAME>斧子科技(北京)有限公司</ENTNAME><REGNO>110107017479549</REGNO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><REGCAP>2000.000000</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>北京市石景山区工商行政管理局</REGORG><SUBCONAM>0.000000</SUBCONAM><CURRENCY>人民币</CURRENCY><CONFORM>货币</CONFORM><FUNDEDRATIO>〈0.01%</FUNDEDRATIO><ESDATE>2014-06-27</ESDATE><REGORGCODE>110107</REGORGCODE></ITEM><ITEM><NAME>张晓威</NAME><PINVAMOUNT>2</PINVAMOUNT><ENTNAME>锤子科技(北京)有限公司</ENTNAME><REGNO>110108014942072</REGNO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><REGCAP>2567.272000</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>北京市海淀区工商行政管理局</REGORG><SUBCONAM>0.970900</SUBCONAM><CURRENCY>人民币</CURRENCY><CONFORM>货币</CONFORM><FUNDEDRATIO>0.04%</FUNDEDRATIO><ESDATE>2012-05-28</ESDATE><REGORGCODE>110108</REGORGCODE></ITEM></FRINV><FRPOSITION><ITEM><NAME>张晓威</NAME><PPVAMOUNT>8</PPVAMOUNT><ENTNAME>白色(北京)软件有限责任公司</ENTNAME><REGNO>110105019696887</REGNO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><REGCAP>37.500000</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>北京市朝阳区工商行政管理局</REGORG><POSITION>董事</POSITION><LEREPSIGN>否</LEREPSIGN><ESDATE>2015-08-17</ESDATE><REGORGCODE>110105</REGORGCODE></ITEM><ITEM><NAME>张晓威</NAME><PPVAMOUNT>8</PPVAMOUNT><ENTNAME>斧子科技(北京)有限公司</ENTNAME><REGNO>110107017479549</REGNO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><REGCAP>2000.000000</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>北京市石景山区工商行政管理局</REGORG><POSITION>经理</POSITION><LEREPSIGN>是</LEREPSIGN><ESDATE>2014-06-27</ESDATE><REGORGCODE>110107</REGORGCODE></ITEM><ITEM><NAME>张晓威</NAME><PPVAMOUNT>8</PPVAMOUNT><ENTNAME>北京游匣互动信息技术有限公司</ENTNAME><REGNO>110000450285274</REGNO><ENTTYPE>有限责任公司(外国法人独资)</ENTTYPE><REGCAP>2350.000000</REGCAP><REGCAPCUR>美元</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>北京市石景山区工商行政管理局</REGORG><POSITION>董事长</POSITION><LEREPSIGN>是</LEREPSIGN><ESDATE>2015-04-27</ESDATE><REGORGCODE>110107</REGORGCODE></ITEM><ITEM><NAME>张晓威</NAME><PPVAMOUNT>8</PPVAMOUNT><ENTNAME>斧子科技(北京)有限公司</ENTNAME><REGNO>110107017479549</REGNO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><REGCAP>2000.000000</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>北京市石景山区工商行政管理局</REGORG><POSITION>执行董事</POSITION><LEREPSIGN>是</LEREPSIGN><ESDATE>2014-06-27</ESDATE><REGORGCODE>110107</REGORGCODE></ITEM><ITEM><NAME>张晓威</NAME><PPVAMOUNT>8</PPVAMOUNT><ENTNAME>北京游匣互动信息技术有限公司</ENTNAME><REGNO>110000450285274</REGNO><ENTTYPE>有限责任公司(外国法人独资)</ENTTYPE><REGCAP>2350.000000</REGCAP><REGCAPCUR>美元</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>北京市石景山区工商行政管理局</REGORG><POSITION>经理</POSITION><LEREPSIGN>是</LEREPSIGN><ESDATE>2015-04-27</ESDATE><REGORGCODE>110107</REGORGCODE></ITEM><ITEM><NAME>张晓威</NAME><PPVAMOUNT>8</PPVAMOUNT><ENTNAME>上海周目信息科技有限公司</ENTNAME><REGNO>310105000475404</REGNO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><REGCAP>100.000000</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>上海市长宁区工商行政管理局</REGORG><POSITION>董事</POSITION><LEREPSIGN>否</LEREPSIGN><ESDATE>2015-01-22</ESDATE><REGORGCODE>310105</REGORGCODE></ITEM><ITEM><NAME>张晓威</NAME><PPVAMOUNT>8</PPVAMOUNT><ENTNAME>北京玩思维文化发展有限公司</ENTNAME><REGNO>110101017311462</REGNO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><REGCAP>142.857200</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>北京市东城区工商行政管理局</REGORG><POSITION>董事</POSITION><LEREPSIGN>否</LEREPSIGN><ESDATE>2014-05-28</ESDATE><REGORGCODE>110101</REGORGCODE></ITEM><ITEM><NAME>张晓威</NAME><PPVAMOUNT>8</PPVAMOUNT><ENTNAME>斧子科技(深圳)有限公司</ENTNAME><REGNO>440301112317221</REGNO><ENTTYPE>有限责任公司（非自然人投资或控股的法人独资）</ENTTYPE><REGCAP>1000.000000</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>广东省深圳市南山区工商行政管理局</REGORG><POSITION>其他人员</POSITION><LEREPSIGN>是</LEREPSIGN><ESDATE>2015-03-09</ESDATE><REGORGCODE>440305</REGORGCODE></ITEM></FRPOSITION><ENTINV><ITEM><ENTNAME>斧子科技(深圳)有限公司</ENTNAME><REGNO>440301112317221</REGNO><ENTTYPE>有限责任公司（非自然人投资或控股的法人独资）</ENTTYPE><REGCAP>1000.000000</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>广东省深圳市南山区工商行政管理局</REGORG><SUBCONAM>3000.000000</SUBCONAM><CONGROCUR>人民币</CONGROCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>300.00%</FUNDEDRATIO><ESDATE>2015-03-09</ESDATE><NAME>张晓威</NAME><BINVVAMOUNT>5</BINVVAMOUNT><REGORGCODE>440305</REGORGCODE></ITEM><ITEM><ENTNAME>北京玩思维文化发展有限公司</ENTNAME><REGNO>110101017311462</REGNO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><REGCAP>142.857200</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>北京市东城区工商行政管理局</REGORG><SUBCONAM>21.428600</SUBCONAM><CONGROCUR>人民币</CONGROCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>15.00%</FUNDEDRATIO><ESDATE>2014-05-28</ESDATE><NAME>姚堃</NAME><BINVVAMOUNT>5</BINVVAMOUNT><REGORGCODE>110101</REGORGCODE></ITEM><ITEM><ENTNAME>上海周目信息科技有限公司</ENTNAME><REGNO>310105000475404</REGNO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><REGCAP>100.000000</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>上海市长宁区工商行政管理局</REGORG><SUBCONAM>10.000000</SUBCONAM><CONGROCUR>人民币</CONGROCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>10.00%</FUNDEDRATIO><ESDATE>2015-01-22</ESDATE><NAME>夏飞</NAME><BINVVAMOUNT>5</BINVVAMOUNT><REGORGCODE>310105</REGORGCODE></ITEM><ITEM><ENTNAME>白色(北京)软件有限责任公司</ENTNAME><REGNO>110105019696887</REGNO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><REGCAP>37.500000</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>北京市朝阳区工商行政管理局</REGORG><SUBCONAM>7.500000</SUBCONAM><CONGROCUR>人民币</CONGROCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>20.00%</FUNDEDRATIO><ESDATE>2015-08-17</ESDATE><NAME>刘灿琳</NAME><BINVVAMOUNT>5</BINVVAMOUNT><REGORGCODE>110105</REGORGCODE></ITEM><ITEM><ENTNAME>锤子科技(北京)有限公司</ENTNAME><REGNO>110108014942072</REGNO><ENTTYPE>有限责任公司(自然人投资或控股)</ENTTYPE><REGCAP>2567.272000</REGCAP><REGCAPCUR>人民币</REGCAPCUR><ENTSTATUS>在营（开业）</ENTSTATUS><CANDATE></CANDATE><REVDATE></REVDATE><REGORG>北京市海淀区工商行政管理局</REGORG><SUBCONAM>4.854400</SUBCONAM><CONGROCUR>人民币</CONGROCUR><CONFORM>货币</CONFORM><FUNDEDRATIO>0.19%</FUNDEDRATIO><ESDATE>2012-05-28</ESDATE><NAME>罗永浩</NAME><BINVVAMOUNT>5</BINVVAMOUNT><REGORGCODE>110108</REGORGCODE></ITEM></ENTINV><ALTER></ALTER><FILIATION></FILIATION><SHARESIMPAWN></SHARESIMPAWN><MORDETAIL></MORDETAIL><MORGUAINFO></MORGUAINFO><PUNISHBREAK></PUNISHBREAK><SHARESFROST></SHARESFROST><LIQUIDATION></LIQUIDATION><CASEINFO></CASEINFO><PUNISHED></PUNISHED></DATA>";
			
	public static void main(String[] arg) throws DocumentException, SQLException{
		NeoHelper neoHelper=new NeoHelper();
//		neoHelper.updateNeoInfo(testStr);
	}
	
	public void updateNeoInfo(String xmlStr, UpdateNeo4j updateNeo4j) throws Exception{
		Document document = DocumentHelper.parseText(xmlStr);
		String status=document.selectSingleNode("DATA/ORDERLIST/ITEM/STATUS").getText();
//		String companyName=document.selectSingleNode("DATA/ORDERLIST/ITEM/KEY").getText();
		if(status.equals("1")){
			pudateStep1(document,updateNeo4j);
			pudateStep2(document,updateNeo4j);
			pudateStep3(document,updateNeo4j);
			//pudateStep4(document,updateNeo4j);
		}
	}
	
	public void pudateStep4(Document document, UpdateNeo4j updateNeo4j) throws SQLException{
		List ItemNodes  = document.selectNodes("DATA/ENTINV/*");
		String companyName=document.selectSingleNode("DATA/BASIC/ITEM/ENTNAME").getText();
		List<String> lines=new ArrayList<String>();
		for (Object eachItem : ItemNodes ) {
			Element thisItem=(Element) eachItem;
			String ENTNAME=thisItem.selectSingleNode("ENTNAME").getText();
			String rEGNO=thisItem.selectSingleNode("REGNO").getText();
			String ESDATE=thisItem.selectSingleNode("ESDATE").getText();
			String REGCAP=thisItem.selectSingleNode("REGCAP").getText();
			String ENTSTATUS=thisItem.selectSingleNode("ENTSTATUS").getText();
			String NAME=thisItem.selectSingleNode("NAME").getText();
			String SUBCONAM=thisItem.selectSingleNode("SUBCONAM").getText();
			String FUNDEDRATIO=thisItem.selectSingleNode("FUNDEDRATIO").getText();
			String updateInfo="";
			try{
				FUNDEDRATIO=FUNDEDRATIO.replace("%", "");	
//				System.out.println("_+_+_+_+_+_+"+FUNDEDRATIO);
				updateInfo=ENTNAME+"|"+rEGNO+"|"+ESDATE+"|"+REGCAP+"|"+ENTSTATUS+"|"+NAME+"|"+SUBCONAM+"|"+Double.parseDouble(FUNDEDRATIO)/100;
			}catch(Exception e){
				//特殊字符原封不动写入
				updateInfo=ENTNAME+"|"+rEGNO+"|"+ESDATE+"|"+REGCAP+"|"+ENTSTATUS+"|"+NAME+"|"+SUBCONAM+"|"+FUNDEDRATIO;
			}
			
			
			updateInfo=replaceQuotes(updateInfo);
//			UpdateNeo4j.updateBase(updateInfo);
			lines.add(updateInfo);
		}
		updateNeo4j.updateInvestment(companyName, lines);
	}
	
	public void pudateStep1(Document document, UpdateNeo4j updateNeo4j) throws SQLException{
		String companyName=document.selectSingleNode("DATA/BASIC/ITEM/ENTNAME").getText();
		String code=document.selectSingleNode("DATA/BASIC/ITEM/REGNO").getText();
		String opentime=document.selectSingleNode("DATA/BASIC/ITEM/ESDATE").getText();
		String REGCAP=document.selectSingleNode("DATA/BASIC/ITEM/REGCAP").getText();
		String ENTSTATUS=document.selectSingleNode("DATA/BASIC/ITEM/ENTSTATUS").getText();
		String FRNAME=document.selectSingleNode("DATA/BASIC/ITEM/FRNAME").getText();
		String updateInfo=companyName+"|"+code+"|"+opentime+"|"+REGCAP+"|"+ENTSTATUS+"|"+FRNAME+"|||";
		updateInfo=replaceQuotes(updateInfo);
//		updateInfo=updateInfo.replace("'", "");
//		System.out.println("neo1 result is "+updateInfo);
		updateNeo4j.updateBase(updateInfo);
	}
	
	
	
	public void pudateStep2(Document document, UpdateNeo4j updateNeo4j){
		List<String> updateList=new ArrayList<String>();
		String companyName=document.selectSingleNode("DATA/BASIC/ITEM/ENTNAME").getText();
		List ItemNodes = document.selectNodes("DATA/PERSON/*");
		for (Object eachItem : ItemNodes ) {
			Element thisItem=(Element) eachItem;
			List<Element> listElement=thisItem.elements();
			String PERNAME="";
			String POSITION="";
			for(Element e:listElement){				
				if(e.getName().equals("PERNAME")){
					PERNAME=e.getTextTrim();
				}else if(e.getName().equals("POSITION")){
					POSITION=e.getTextTrim();
				}
			}
			updateList.add(replaceQuotes(PERNAME+"|"+POSITION));
		}
//		System.out.println("neo2 result is "+updateList);
		updateNeo4j.updateKeyPerson(companyName, updateList);
	}
	
	public void pudateStep3(Document document, UpdateNeo4j updateNeo4j) throws Exception{
		List<String> updateList=new ArrayList<String>();
		String companyName=document.selectSingleNode("DATA/BASIC/ITEM/ENTNAME").getText();
		List ItemNodes = document.selectNodes("DATA/SHAREHOLDER/*");
		for (Object eachItem : ItemNodes ) {
			Element thisItem=(Element) eachItem;
			List<Element> listElement=thisItem.elements();
			String SHANAME="";
			String INVTYPE="";
			String SUBCONAM="";
			String FUNDEDRATIO="";
			float FUNDEDRATIOpercent=0;
			for(Element e:listElement){				
				if(e.getName().equals("SHANAME")){
					SHANAME=e.getTextTrim();
				}else if(e.getName().equals("INVTYPE")){
					INVTYPE=e.getTextTrim();
				}else if(e.getName().equals("SUBCONAM")){
					SUBCONAM=e.getTextTrim();
				}else if(e.getName().equals("FUNDEDRATIO")){
					FUNDEDRATIO=e.getTextTrim();
					if(FUNDEDRATIO!=null&&!FUNDEDRATIO.equals("")&&FUNDEDRATIO.equals("null")){
						FUNDEDRATIO=FUNDEDRATIO.replace("%", "");
						FUNDEDRATIOpercent=new Float(FUNDEDRATIO);
						FUNDEDRATIOpercent=FUNDEDRATIOpercent/100;
						updateList.add(replaceQuotes(SHANAME+"|"+INVTYPE+"||"+SUBCONAM+"|"+FUNDEDRATIOpercent));
					}else{
						updateList.add(replaceQuotes(SHANAME+"|"+INVTYPE+"||"+SUBCONAM+"|"+""));
					}
					
				}
			}
			
		}
//		System.out.println("neo3 result is "+updateList);
		updateNeo4j.updateShareholder(companyName, updateList);
	}
	
	public void pudateStepRegistration(Map<String,Object> map, UpdateNeo4j updateNeo4j) throws SQLException{
		String companyName=map.get("companyName").toString();
		String registrationno=map.get("registrationno").toString();
		String establishmentdate=map.get("establishmentdate").toString();
		String registeredcapital=map.get("registeredcapital").toString();
		String entstatus=map.get("entstatus").toString();
		String frname=map.get("frname").toString();
		String updateInfo=companyName+"|"+registrationno+"|"+establishmentdate+"|"+registeredcapital+"|"+entstatus+"|"+frname+"|||";
		updateInfo=replaceQuotes(updateInfo);

		updateNeo4j.updateBase(updateInfo);
	}
	
	public void pudatePosition(Map<String,Object> map, UpdateNeo4j updateNeo4j){
		String companyname=map.get("companyName").toString();
		List<String> updatelist=(List<String>) map.get("personList");
		updateNeo4j.updateKeyPerson(companyname, updatelist);
	}
	
	public void pudateShare(Map<String,Object> map, UpdateNeo4j updateNeo4j) throws Exception{
		String companyname=map.get("companyName").toString();
		List<String> listShare=(List<String>) map.get("listShare");
		updateNeo4j.updateShareholder(companyname, listShare);
	}
	public void pudateEntname(Map<String,Object> map, UpdateNeo4j updateNeo4j){
		String companyname=map.get("companyName").toString();
		List<String> updatelist=new ArrayList<String>();
		updateNeo4j.updateKeyPerson(companyname, updatelist);
	}
	
	public String replaceQuotes(String input){
		return input.replace("'", "").replace(" ", "");		
	}
}
