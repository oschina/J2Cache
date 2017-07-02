package net.oschina.j2cache.redis.support;

/**
 * Created by qixiaobo on 2017/7/2.
 */
public class BlockSupportConfig {
    private int timeOutMillis = 60000; //加锁超时时间
    private int timeLockMillis = 60000;//加锁锁定时间
    private int stripes = 1024;//默认锁的个数
    private int timeWaitMillis = 300;//加锁过程每次恢复时间
    private boolean block = false;//是否阻塞

    public int getTimeOutMillis() {
        return timeOutMillis;
    }

    public void setTimeOutMillis(int timeOutMillis) {
        this.timeOutMillis = timeOutMillis;
    }

    public int getTimeLockMillis() {
        return timeLockMillis;
    }

    public void setTimeLockMillis(int timeLockMillis) {
        this.timeLockMillis = timeLockMillis;
    }

    public int getStripes() {
        return stripes;
    }

    public void setStripes(int stripes) {
        this.stripes = stripes;
    }

    public int getTimeWaitMillis() {
        return timeWaitMillis;
    }

    public void setTimeWaitMillis(int timeWaitMillis) {
        this.timeWaitMillis = timeWaitMillis;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }
}
