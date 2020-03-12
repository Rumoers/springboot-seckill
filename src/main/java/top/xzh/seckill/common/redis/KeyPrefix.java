package top.xzh.seckill.common.redis;


public interface KeyPrefix {

    /**
     * 获取过期时间
     * @return expire
     */
    int getExpireSeconds();

    /**
     * 获取key前缀
     * @return key prefix
     */
    String getPrefix();

    /**
     * 获取真正的key, 业务key
     * @param key redis key
     * @return 真正的key
     */
    String getRealKey(String key);
}
