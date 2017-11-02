package doInput;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.mortbay.log.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;


public class LogConsumer {
	private ConsumerConfig config;
	private String topic;
	private int partitionsNum;
	private MessageExecutor executor;
	private ConsumerConnector connector;
	private ExecutorService threadPool;

	
	public LogConsumer(String topic,int partitionsNum,MessageExecutor executor) throws Exception{
		Properties properties = new Properties();
		properties.load(ClassLoader.getSystemResourceAsStream("consumer.properties"));
		properties.put("auto.offset.reset", "smallest");
		config = new ConsumerConfig(properties);
		this.topic = topic;
		this.partitionsNum = partitionsNum;
		this.executor = executor;
	}
	
	public void start() throws Exception{
		connector = Consumer.createJavaConsumerConnector(config);
		Map<String,Integer> topics = new HashMap<String,Integer>();
		topics.put(topic, partitionsNum);
		Map<String, List<KafkaStream<byte[], byte[]>>> streams = connector.createMessageStreams(topics);
		List<KafkaStream<byte[], byte[]>> partitions = streams.get(topic);
		threadPool = Executors.newFixedThreadPool(partitionsNum);
		for(KafkaStream<byte[], byte[]> partition : partitions){
			threadPool.execute(new MessageRunner(partition));
		} 
	}

    	
	public void close(){
		try{
			threadPool.shutdownNow();
		}catch(Exception e){
			//
		}finally{
			connector.shutdown();
		}
		
	}
	
	class MessageRunner implements Runnable{
		private KafkaStream<byte[], byte[]> partition;
		
		MessageRunner(KafkaStream<byte[], byte[]> partition) {
			this.partition = partition;
		}
		
		public void run(){
			ConsumerIterator<byte[], byte[]> it = partition.iterator();
			while(it.hasNext()){
				MessageAndMetadata<byte[],byte[]> item = it.next();
				String str=new String(item.message());
				LoadHbaseData data=new LoadHbaseData();
				try {	
					data.loadData(str);
					Calendar c=Calendar.getInstance();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String date=sdf.format(new Date()); 
					File dayfile=new File("/home/zyx/hbaseBatch/gscrawlerresulttest/dt/"+date); 
					if(!dayfile.exists()){
						dayfile.mkdirs();	
					}
					String hourfile=new SimpleDateFormat("HH").format(new Date());
					try {
						FileChannel fc = new RandomAccessFile(dayfile+"/"+hourfile,"rw").getChannel();
						fc.position(fc.size());        //定位到文件末尾  
					    fc.write(ByteBuffer.wrap((str+"\n").getBytes()));  
					    fc.close(); 
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
	
	interface MessageExecutor {
		
		public void execute(String message);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LogConsumer consumer = null;

		try{		
			MessageExecutor executor = new MessageExecutor() {			
				public void execute(String message) {
					System.out.println("begin :"+message);																
				}
			};
			consumer = new LogConsumer("GSCrawlerResultTest", 30, executor);
			consumer.start();
		}catch(Exception e){
			e.printStackTrace();
		}finally{

		}

	}
	
	

}
