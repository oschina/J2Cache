package net.oschina.j2cache;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 缓存多播通道
 * @author Winter Lau
 */
public class CacheChannel extends ReceiverAdapter implements CacheExpiredListener {
	
	private final static Logger log = LoggerFactory.getLogger(CacheChannel.class);
	private final static String CONFIG_XML = "/network.xml";

	private final static byte OPT_DELETE_KEY = 0x01;
	private final static byte OPT_DELETE_KEYS = 0x02;
	
	public final static byte LEVEL_1 = 1;
	public final static byte LEVEL_2 = 2;
	
	private String name;
	private JChannel channel;
	private static CacheChannel instance;
	
	/**
	 * 单例方法
	 * @return
	 */
	public final static CacheChannel getInstance(){
		if(instance == null){
			synchronized(CacheChannel.class){
				if(instance == null){
					instance = new CacheChannel("default");
				}
			}
		}
		return instance;
	}
	
	/**
	 * 初始化缓存通道并连接
	 * @param name
	 * @throws CacheException
	 */
	private CacheChannel(String name) throws CacheException {
		this.name = name;
		try{
			URL xml = CacheChannel.class.getResource(CONFIG_XML);
			if(xml == null)
				xml = getClass().getClassLoader().getParent().getResource(CONFIG_XML);
			channel = new JChannel(xml);
			channel.setReceiver(this);
			channel.connect(this.name);

			CacheManager.initCacheProvider(this);
			
		}catch(Exception e){
			throw new CacheException(e);
		}
	}

    /**
	 * 获取缓存中的数据
	 * @param <T>
	 * @param level
	 * @param resultClass
	 * @param region
	 * @param key
	 * @return
	 */
	public CacheObject get(String region, String key){
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
        if(region!=null && key != null){
        	obj.setValue(CacheManager.get(LEVEL_1, region, key));
            if(obj.getValue() == null) {
            	obj.setValue(CacheManager.get(LEVEL_2, region, key));
                if(obj.getValue() != null){
                	obj.setLevel(LEVEL_2);
                    CacheManager.set(LEVEL_1, region, key, (Serializable)obj.getValue());
                }
            }
            else
            	obj.setLevel(LEVEL_1);
        }
        return obj;
	}
	
	/**
	 * 写入缓存
	 * @param level
	 * @param region
	 * @param key
	 * @param value
	 */
	public void set(String region, String key, Serializable value){
		if(region!=null && key != null){
			if(value == null)
				evict(region, key);
			else{
				//分几种情况
				//Object obj1 = CacheManager.get(LEVEL_1, region, key);
				//Object obj2 = CacheManager.get(LEVEL_2, region, key);
				//1. L1 和 L2 都没有
				//2. L1 有 L2 没有（这种情况不存在，除非是写 L2 的时候失败
				//3. L1 没有，L2 有
				//4. L1 和 L2 都有
				_sendEvictCmd(region, key);//清除原有的一级缓存的内容
				CacheManager.set(LEVEL_1, region, key, value);
				CacheManager.set(LEVEL_2, region, key, value);
			}
		}
		//log.info("write data to cache region="+region+",key="+key+",value="+value);
	}
	
	/**
	 * 删除缓存
	 * @param region
	 * @param key
	 */
	public void evict(String region, String key) {
		CacheManager.evict(LEVEL_1, region, key); //删除一级缓存
		CacheManager.evict(LEVEL_2, region, key); //删除二级缓存
		_sendEvictCmd(region, key); //发送广播
	}

	/**
	 * 批量删除缓存
	 * @param region
	 * @param keys
	 */
	public void batchEvict(String region, List<String> keys) {
		CacheManager.batchEvict(LEVEL_1, region, keys);
		CacheManager.batchEvict(LEVEL_2, region, keys);
		_sendBatchEvictCmd(region, keys);
	}
	
	/**
	 * 为了保证每个节点缓存的一致，当某个缓存对象因为超时被清除时，应该通知群组其他成员
	 */
	@Override
	public void notifyElementExpired(String region, String key) {

		log.debug("Cache data expired, region="+region+",key="+key);
		
		//删除二级缓存
		CacheManager.evict(LEVEL_2, region, key);
		
		//发送广播
		_sendEvictCmd(region, key);
	}
	
	/**
	 * 发送清除缓存的广播命令
	 * @param region
	 * @param key
	 */
	private void _sendEvictCmd(String region, String key) {
		//发送广播
		Command cmd = new Command(OPT_DELETE_KEY, region, key);
		try {
			Message msg = new Message(null, null, cmd.toBuffers());
			channel.send(msg);
		} catch (Exception e) {
			log.error("Unable to delete cache,region="+region+",key="+key, e);
		}
	}

	/**
	 * 发送批量清除缓存的广播命令
	 * @param region
	 * @param keys
	 */
	private void _sendBatchEvictCmd(String region, List<String> keys) {
		//发送广播
		Command cmd = new Command(OPT_DELETE_KEYS, region, listToString(keys));
		try {
			Message msg = new Message(null, null, cmd.toBuffers());
			channel.send(msg);
		} catch (Exception e) {
			log.error("Unable to batch delete cache,region="+region, e);
		}
	}
	
	/**
	 * 删除一级缓存的键对应内容
	 * @param region
	 * @param key
	 */
	protected void onDeleteCacheKey(String region, String key){
		CacheManager.evict(LEVEL_1, region, key);
		log.debug("Received cache evict message, region="+region+",key="+key);
	}

	/**
	 * 删除一级缓存的键对应内容
	 * @param region
	 * @param key
	 */
	protected void onBatchDeleteCacheKey(String region, List<String> keys){
		CacheManager.batchEvict(LEVEL_1, region, keys);
		log.debug("Received cache evict message, region="+region+",keys="+keys);
	}

	/**
	 * 消息接收
	 */
	@Override
	public void receive(Message msg) {
		//无效消息
		byte[] buffers = msg.getBuffer();
		if(buffers.length < 1){
			log.warn("Message is empty.");
			return;
		}
		
		//不处理发送给自己的消息
		if(msg.getSrc().equals(channel.getAddress()))
			return ;
		
		try{
			Command cmd = Command.parse(buffers);
			//int region_name
			switch(cmd.operator){
			case OPT_DELETE_KEY:
				onDeleteCacheKey(cmd.region, cmd.key);
				break;
			case OPT_DELETE_KEYS:
				List<String> keys = stringToList(cmd.key);
				onBatchDeleteCacheKey(cmd.region, keys);
				break;
			default:
				log.warn("Unknown message type = " + cmd.operator);
			}
		}catch(Exception e){
			log.error("Unable to handle received msg" , e);
		}
	}
	/**
	 * 关闭到通道的连接
	 */
	public void close(){
		channel.close();
	}
	
	private final static String separator = "\n";
	
	private static List<String> stringToList(String keys) {
		List<String> lkeys = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(keys, separator);
		while(st.hasMoreTokens()){
			lkeys.add(st.nextToken());
		}
		return lkeys;
	}
	
	private static String listToString(List<String> keys) {
		StringBuffer sb = new StringBuffer();
		for(String key : keys){
			if(sb.length() > 0)
				sb.append(separator);
			sb.append(key);
		}
		return sb.toString();
	}
	
	/**
	 * 命令消息封装
	 * 格式：
	 * 第1个字节为命令代码，长度1 [OPT]
	 * 第2、3个字节为region长度，长度2 [R_LEN]
	 * 第4、N 为 region 值，长度为 [R_LEN]
	 * 第N+1、N+2 为 key 长度，长度2 [K_LEN]
	 * 第N+3、M为 key值，长度为 [K_LEN]
	 */
	private static class Command {		
		
		private byte operator;
		private String region;
		private String key;
		
		public Command(byte o, String r, String k){
			this.operator = o;
			this.region = r;
			this.key = k;
		}
		
		public byte[] toBuffers(){
			int r_len = region.getBytes().length;
			int k_len = key.getBytes().length;

			byte[] buffers = new byte[5 + r_len + k_len];
			int idx = 0;
			buffers[idx] = operator;
			buffers[++idx] = (byte)(r_len >> 8);
			buffers[++idx] = (byte)(r_len & 0xFF);
			System.arraycopy(region.getBytes(), 0, buffers, ++idx, r_len);
			idx += r_len;
			buffers[idx++] = (byte)(k_len >> 8);
			buffers[idx++] = (byte)(k_len & 0xFF);
			System.arraycopy(key.getBytes(), 0, buffers, idx, k_len);
			return buffers;
		}
		
		public static Command parse(byte[] buffers) {
			int idx = 0;
			byte opt = buffers[idx];
			int r_len = buffers[++idx] << 8;
			r_len += buffers[++idx];
			String region = new String(buffers, ++idx, r_len);
			idx += r_len;
			int k_len = buffers[idx++] << 8;
			k_len += buffers[idx++];
			String key = new String(buffers, idx, k_len);
			return new Command(opt, region, key);
		}
	}
	
	public static void main(String[] args) {
		Command cmd = new Command(OPT_DELETE_KEY, "users", "ld");
		byte[] bufs = cmd.toBuffers();
		for(byte b : bufs){
			System.out.printf("[%s]",Integer.toHexString(b));			
		}
		System.out.println();
		Command cmd2 = Command.parse(bufs);
		System.out.printf("%d:%s:%s\n", cmd2.operator, cmd2.region, cmd2.key);
	}

}
