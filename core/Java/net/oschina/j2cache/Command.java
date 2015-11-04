/**
 * 
 */
package net.oschina.j2cache;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.oschina.j2cache.util.SerializationUtils;

/**
 * 命令消息封装
 * 格式：
 * 第1个字节为命令代码，长度1 [OPT]
 * 第2、3个字节为region长度，长度2 [R_LEN]
 * 第4、N 为 region 值，长度为 [R_LEN]
 * 第N+1、N+2 为 key 长度，长度2 [K_LEN]
 * 第N+3、M为 key值，长度为 [K_LEN]
 * 
 * @author winterlau
 */
public class Command {		

	private final static Logger log = LoggerFactory.getLogger(Command.class);

	public final static byte OPT_DELETE_KEY = 0x01; //删除缓存
	
	private byte operator;
	private String region;
	private Object key;

	public static void main(String[] args) {
		Command cmd = new Command(OPT_DELETE_KEY, "users", "ld");
		byte[] bufs = cmd.toBuffers();
		for(byte b : bufs){
			System.out.printf("[%s]",Integer.toHexString(b));			
		}
		System.out.println();
		Command cmd2 = Command.parse(bufs);
		System.out.printf("%d:%s:%s\n", cmd2.getOperator(), cmd2.getRegion(), cmd2.getKey());
	}

	public Command(byte o, String r, Object k){
		this.operator = o;
		this.region = r;
		this.key = k;
	}
	
	public byte[] toBuffers(){
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
		buffers[++idx] = (byte)(r_len >> 8);
		buffers[++idx] = (byte)(r_len & 0xFF);
		System.arraycopy(region.getBytes(), 0, buffers, ++idx, r_len);
		idx += r_len;
		buffers[idx++] = (byte)(k_len >> 8);
		buffers[idx++] = (byte)(k_len & 0xFF);
		System.arraycopy(keyBuffers, 0, buffers, idx, k_len);
		return buffers;
	}
	
	public static Command parse(byte[] buffers) {
		Command cmd = null;
		try{
			int idx = 0;
			byte opt = buffers[idx];
			int r_len = buffers[++idx] << 8;
			r_len += buffers[++idx];
			if(r_len > 0){
				String region = new String(buffers, ++idx, r_len);
				idx += r_len;
				int k_len = buffers[idx++] << 8;
				k_len += buffers[idx++];
				if(k_len > 0){
					//String key = new String(buffers, idx, k_len);
					byte[] keyBuffers = new byte[k_len];
					System.arraycopy(buffers, idx, keyBuffers, 0, k_len);
					Object key = SerializationUtils.deserialize(keyBuffers);
					cmd = new Command(opt, region, key);
				}
			}
		}catch(Exception e){
			log.error("Unabled to parse received command.", e);
		}
		return cmd;
	}

	public byte getOperator() {
		return operator;
	}

	public void setOperator(byte operator) {
		this.operator = operator;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}
}
