package top.xzh.seckill.common.redis;


public class MiaoshaUserKey extends AbstractPrefix {

    private static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey TOKEN = new MiaoshaUserKey(TOKEN_EXPIRE, "token");
    public static MiaoshaUserKey getById = new MiaoshaUserKey(0, "getById");

}
