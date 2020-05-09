package top.xzh.seckill.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.xzh.seckill.domain.User;
import top.xzh.seckill.mapper.UserMapper;

/**
 *
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    public User getById(Integer id) {
        return userMapper.findById(id);
    }
}
