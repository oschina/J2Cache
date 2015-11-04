package net.oschina.j2cache;

import java.io.IOException;
import java.util.List;

import net.oschina.j2cache.redis.RedisCacheProvider;
import net.oschina.j2cache.util.SerializationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * 缓存Redis PUB/SUB监听通道
 * 
 * 2015年10月31日 下午5:27:07
 * flyfox 330627517@qq.com
 */
public class CacheRedisChannel extends JedisPubSub implements CacheExpiredListener {

	private final static Logger log = LoggerFactory.getLogger(CacheRedisChannel.class);

	private final static byte OPT_DELETE_KEY = 0x01;

	public final static byte LEVEL_1 = 1;
	public final static byte LEVEL_2 = 2;

	private String name;
	private static String channel = "cahce_channel";
	private static boolean flag = true;
	private final static CacheRedisChannel instance = new CacheRedisChannel("default");

	/**
	 * 单例方法
	 * 
	 * @return 返回 CacheChannel 单实例
	 */
	public final static CacheRedisChannel getInstance() {
		return instance;
	}

	/**
	 * 初始化缓存通道并连接
	 * 
	 * @param name
	 *            : 缓存实例名称
	 */
	private CacheRedisChannel(String name) throws CacheException {
		this.name = name;
		try {
			long ct = System.currentTimeMillis();
			if (flag) {
				CacheManager.initCacheProvider(this);
				flag = false;
			}

			new Thread(new Runnable() {
				@Override
				public void run() {
					Jedis jedis = RedisCacheProvider.getResource();
					jedis.subscribe(CacheRedisChannel.getInstance(), CacheRedisChannel.channel);
					RedisCacheProvider.returnResource(jedis, false);
				}
			}).start();

			log.info("Connected to channel:" + this.name + ", time " + (System.currentTimeMillis() - ct) + " ms.");

		} catch (Exception e) {
			throw new CacheException(e);
		}
	}

	/**
	 * 获取缓存中的数据
	 * 
	 * @param region
	 *            : Cache Region name
	 * @param key
	 *            : Cache key
	 * @return cache object
	 */
	public CacheObject get(String region, Object key) {
		CacheObject obj = new CacheObject();
		obj.setRegion(region);
		obj.setKey(key);
		if (region != null && key != null) {
			obj.setValue(CacheManager.get(LEVEL_1, region, key));
			if (obj.getValue() == null) {
				obj.setValue(CacheManager.get(LEVEL_2, region, key));
				if (obj.getValue() != null) {
					obj.setLevel(LEVEL_2);
					CacheManager.set(LEVEL_1, region, key, obj.getValue());
				}
			} else
				obj.setLevel(LEVEL_1);
		}
		return obj;
	}

	/**
	 * 写入缓存
	 * 
	 * @param region
	 *            : Cache Region name
	 * @param key
	 *            : Cache key
	 * @param value
	 *            : Cache value
	 */
	public void set(String region, Object key, Object value) {
		if (region != null && key != null) {
			if (value == null)
				evict(region, key);
			else {
				// 分几种情况
				// Object obj1 = CacheManager.get(LEVEL_1, region, key);
				// Object obj2 = CacheManager.get(LEVEL_2, region, key);
				// 1. L1 和 L2 都没有
				// 2. L1 有 L2 没有（这种情况不存在，除非是写 L2 的时候失败
				// 3. L1 没有，L2 有
				// 4. L1 和 L2 都有
				_sendEvictCmd(region, key);// 清除原有的一级缓存的内容
				CacheManager.set(LEVEL_1, region, key, value);
				CacheManager.set(LEVEL_2, region, key, value);
			}
		}
		// log.info("write data to cache region="+region+",key="+key+",value="+value);
	}

	/**
	 * 删除缓存
	 * 
	 * @param region
	 *            : Cache Region name
	 * @param key
	 *            : Cache key
	 */
	public void evict(String region, Object key) {
		CacheManager.evict(LEVEL_1, region, key); // 删除一级缓存
		CacheManager.evict(LEVEL_2, region, key); // 删除二级缓存
		_sendEvictCmd(region, key); // 发送广播
	}

	/**
	 * 批量删除缓存
	 * 
	 * @param region
	 *            : Cache region name
	 * @param keys
	 *            : Cache key
	 */
	@SuppressWarnings({ "rawtypes" })
	public void batchEvict(String region, List keys) {
		CacheManager.batchEvict(LEVEL_1, region, keys);
		CacheManager.batchEvict(LEVEL_2, region, keys);
		_sendEvictCmd(region, keys);
	}

	/**
	 * Clear the cache
	 * 
	 * @param region
	 *            : Cache region name
	 */
	public void clear(String region) throws CacheException {
		CacheManager.clear(LEVEL_1, region);
		CacheManager.clear(LEVEL_2, region);
	}

	/**
	 * Get cache region keys
	 * 
	 * @param region
	 *            : Cache region name
	 * @return key list
	 */
	@SuppressWarnings("rawtypes")
	public List keys(String region) throws CacheException {
		return CacheManager.keys(LEVEL_1, region);
	}

	/**
	 * 为了保证每个节点缓存的一致，当某个缓存对象因为超时被清除时，应该通知群组其他成员
	 * 
	 * @param region
	 *            : Cache region name
	 * @param key
	 *            : cache key
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void notifyElementExpired(String region, Object key) {

		log.debug("Cache data expired, region=" + region + ",key=" + key);

		// 删除二级缓存
		if (key instanceof List)
			CacheManager.batchEvict(LEVEL_2, region, (List) key);
		else
			CacheManager.evict(LEVEL_2, region, key);

		// 发送广播
		_sendEvictCmd(region, key);
	}

	/**
	 * 发送清除缓存的广播命令
	 * 
	 * @param region
	 *            : Cache region name
	 * @param key
	 *            : cache key
	 */
	private void _sendEvictCmd(String region, Object key) {
		// 发送广播
		Command cmd = new Command(OPT_DELETE_KEY, region, key);
		Jedis jedis = RedisCacheProvider.getResource();
		try {
			jedis.publish(channel, new String(cmd.toBuffers(), "UTF-8"));
		} catch (Exception e) {
			log.error("Unable to delete cache,region=" + region + ",key=" + key, e);
		} finally {
			RedisCacheProvider.returnResource(jedis, false);
		}
	}

	/**
	 * 删除一级缓存的键对应内容
	 * 
	 * @param region
	 *            : Cache region name
	 * @param key
	 *            : cache key
	 */
	@SuppressWarnings("rawtypes")
	protected void onDeleteCacheKey(String region, Object key) {
		if (key instanceof List)
			CacheManager.batchEvict(LEVEL_1, region, (List) key);
		else
			CacheManager.evict(LEVEL_1, region, key);
		log.debug("Received cache evict message, region=" + region + ",key=" + key);
	}

	/**
	 * 消息接收
	 * 
	 * @param channel
	 * @param message 接收到的消息
	 */
	@Override
	public void onMessage(String channel, String message) {
		System.out.println(channel + ":" + message);
		// 无效消息
		if (message != null && message.length() <= 0) {
			log.warn("Message is empty.");
			return;
		}

		try {
			Command cmd = Command.parse(message.getBytes("UTF-8"));

			if (cmd == null)
				return;

			switch (cmd.operator) {
			case OPT_DELETE_KEY:
				onDeleteCacheKey(cmd.region, cmd.key);
				break;
			default:
				log.warn("Unknown message type = " + cmd.operator);
			}
		} catch (Exception e) {
			log.error("Unable to handle received msg", e);
		}
	}

	/**
	 * 关闭到通道的连接
	 */
	public void close() {
		CacheManager.shutdown(LEVEL_1);
		CacheManager.shutdown(LEVEL_2);
	}

	/**
	 * 命令消息封装 格式： 第1个字节为命令代码，长度1 [OPT] 第2、3个字节为region长度，长度2 [R_LEN] 第4、N 为
	 * region 值，长度为 [R_LEN] 第N+1、N+2 为 key 长度，长度2 [K_LEN] 第N+3、M为 key值，长度为
	 * [K_LEN]
	 */
	private static class Command {

		private byte operator;
		private String region;
		private Object key;

		public Command(byte o, String r, Object k) {
			this.operator = o;
			this.region = r;
			this.key = k;
		}

		public byte[] toBuffers() {
			byte[] keyBuffers = null;
			try {
				keyBuffers = SerializationUtils.serialize(key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int r_len = region.getBytes().length;
			int k_len = keyBuffers.length;

			byte[] buffers = new byte[5 + r_len + k_len];
			int idx = 0;
			buffers[idx] = operator;
			buffers[++idx] = (byte) (r_len >> 8);
			buffers[++idx] = (byte) (r_len & 0xFF);
			System.arraycopy(region.getBytes(), 0, buffers, ++idx, r_len);
			idx += r_len;
			buffers[idx++] = (byte) (k_len >> 8);
			buffers[idx++] = (byte) (k_len & 0xFF);
			System.arraycopy(keyBuffers, 0, buffers, idx, k_len);
			return buffers;
		}

		public static Command parse(byte[] buffers) {
			Command cmd = null;
			try {
				int idx = 0;
				byte opt = buffers[idx];
				int r_len = buffers[++idx] << 8;
				r_len += buffers[++idx];
				if (r_len > 0) {
					String region = new String(buffers, ++idx, r_len);
					idx += r_len;
					int k_len = buffers[idx++] << 8;
					k_len += buffers[idx++];
					if (k_len > 0) {
						// String key = new String(buffers, idx, k_len);
						byte[] keyBuffers = new byte[k_len];
						System.arraycopy(buffers, idx, keyBuffers, 0, k_len);
						Object key = SerializationUtils.deserialize(keyBuffers);
						cmd = new Command(opt, region, key);
					}
				}
			} catch (Exception e) {
				log.error("Unabled to parse received command.", e);
			}
			return cmd;
		}
	}

	public static void main(String[] args) {
		Command cmd = new Command(OPT_DELETE_KEY, "users", "ld");
		byte[] bufs = cmd.toBuffers();
		for (byte b : bufs) {
			System.out.printf("[%s]", Integer.toHexString(b));
		}
		System.out.println();
		Command cmd2 = Command.parse(bufs);
		System.out.printf("%d:%s:%s\n", cmd2.operator, cmd2.region, cmd2.key);
	}

}
