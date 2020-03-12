package top.xzh.seckill.service;

import cn.hutool.core.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.xzh.seckill.common.redis.MiaoshaUserKey;
import top.xzh.seckill.domain.MiaoshaUser;
import top.xzh.seckill.exception.GlobalException;
import top.xzh.seckill.mapper.MiaoshaUserMapper;
import top.xzh.seckill.result.CodeMsg;
import top.xzh.seckill.utils.MD5Util;
import top.xzh.seckill.utils.UUIDUtil;
import top.xzh.seckill.vo.LoginVo;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;


@Service
public class MiaoshaUserService {

    private final MiaoshaUserMapper userMapper;
    private final RedisService redisService;

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    public MiaoshaUserService(MiaoshaUserMapper userMapper, RedisService redisService) {
        this.userMapper = userMapper;
        this.redisService = redisService;
    }

    public MiaoshaUser getById(long id) {
        //取缓存
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (null != miaoshaUser) {
            return miaoshaUser;
        }

        miaoshaUser = userMapper.getById(id);

        if (null != miaoshaUser) {
            redisService.set(MiaoshaUserKey.getById, "" + id, miaoshaUser);
        }

        return miaoshaUser;
    }

    public MiaoshaUser register (MiaoshaUser user) {

        if (null == user) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        MiaoshaUser registerUser = new MiaoshaUser();
        BeanUtils.copyProperties(user, registerUser);

        String formPass = user.getPassword();
        //生成随机盐值
        String salt = RandomUtil.randomString(10);
        //密码加密
        String dbPass = MD5Util.formPassToDbPass(formPass, salt);
        registerUser.setPassword(dbPass);
        registerUser.setSalt(salt);

        return userMapper.insert(registerUser);
    }

    public MiaoshaUser updatePassword(String token, long id, String form) {

        MiaoshaUser miaoshaUser = this.getById(id);
        if (null == miaoshaUser) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        //更新密码
        MiaoshaUser updateUser = new MiaoshaUser();
        updateUser.setId(id);
        updateUser.setPassword(MD5Util.formPassToDbPass(form,miaoshaUser.getSalt()));
        userMapper.update(updateUser);

        miaoshaUser.setPassword(updateUser.getPassword());

        // 更新缓存
        redisService.delete(MiaoshaUserKey.getById, ""+id);
        redisService.set(MiaoshaUserKey.TOKEN, token, miaoshaUser);

        return miaoshaUser;
    }


    public boolean login(HttpServletResponse response, LoginVo loginVo) {

        if (null == loginVo) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        MiaoshaUser user = this.getById(Long.parseLong(mobile));

        if (null == user) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        String dbSalt = user.getSalt();
        String dbPass = user.getPassword();

        String formPassToDBPass = MD5Util.formPassToDbPass(formPass, dbSalt);

        if (!Objects.equals(dbPass, formPassToDBPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        //每次登陆将信息写入到cookie
        String token = UUIDUtil.uuid();
        writeCookie(response, token, user);

        return true;
    }

    private void writeCookie(HttpServletResponse response, String token, MiaoshaUser user) {

        // 存入redis
        redisService.set(MiaoshaUserKey.TOKEN, token, user);

        // 写入cookie
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.TOKEN.getExpireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    //通过token获取用户信息
    public MiaoshaUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.TOKEN, token, MiaoshaUser.class);
        // 续签
        if (null != miaoshaUser) {
            writeCookie(response, token, miaoshaUser);
        }
        return miaoshaUser;
    }
}
