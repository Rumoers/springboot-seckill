package top.xzh.seckill.access;

import top.xzh.seckill.domain.MiaoshaUser;

/**
 * 用户上下文
 * */
public class UserContext {

    private static final ThreadLocal<MiaoshaUser> USER_HOLDER = new ThreadLocal <>();

    public static MiaoshaUser getUser() {
        return USER_HOLDER.get();
    }

    public static void setUser(MiaoshaUser user) {
        USER_HOLDER.set(user);
    }

    public static void remove() {
        USER_HOLDER.remove();
    }
}
