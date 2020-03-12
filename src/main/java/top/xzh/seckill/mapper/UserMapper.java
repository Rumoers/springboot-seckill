package top.xzh.seckill.mapper;

import org.apache.ibatis.annotations.Select;
import top.xzh.seckill.domain.User;


public interface UserMapper {

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Integer id);
}
