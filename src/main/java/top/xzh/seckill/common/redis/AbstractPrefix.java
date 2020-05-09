package top.xzh.seckill.common.redis;

/**
 * 前缀类的父类
 */
public abstract class AbstractPrefix implements KeyPrefix {

    private int expireSeconds;

    private String prefix;
//    只带前缀名的构造方法，过期时间设置为0，永不过期
    AbstractPrefix(String prefix) {
        this(0, prefix);
    }
//  带过期时间和前缀名的构造方法
    AbstractPrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    /**
     * 获取过期时间
     * @return expire
     */
    @Override
    public int getExpireSeconds() {
        return this.expireSeconds;//定义为0則永不过期
    }
    @Override
    public String getRealKey(String key) {
        return getPrefix() + key;
    }
    /**
     * 获取key前缀
     *
     * @return key prefix
     */
    @Override
    public String getPrefix() {
        String simpleName = getClass().getSimpleName();
        return simpleName + ":" + this.prefix;
    }


}
