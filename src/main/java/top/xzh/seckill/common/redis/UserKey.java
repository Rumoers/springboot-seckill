package top.xzh.seckill.common.redis;


public class UserKey extends AbstractPrefix {

    private UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey GET_BY_ID = new UserKey("id");
    public static UserKey GET_BY_NAME = new UserKey("name");

}
