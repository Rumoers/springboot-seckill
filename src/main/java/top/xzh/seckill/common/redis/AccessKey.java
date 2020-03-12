package top.xzh.seckill.common.redis;


public class AccessKey extends AbstractPrefix {
    private AccessKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
    public static AccessKey withExpireSeconds(int expireSeconds) {
        return new AccessKey(expireSeconds, "access");
    }
}
