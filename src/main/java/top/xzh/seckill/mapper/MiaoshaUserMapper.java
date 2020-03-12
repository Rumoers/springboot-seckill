package top.xzh.seckill.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import top.xzh.seckill.domain.MiaoshaUser;


public interface MiaoshaUserMapper {

    @Select("SELECT * FROM miaosha.miaosha_user WHERE id = #{id} ")
    MiaoshaUser getById(@Param("id") long id);

    @Update("UPDATE miaosha_user SET password = #{password} WHERE id = #{id} ")
    void update(MiaoshaUser updateUser);

    @Select("INSERT INTO miaosha_user (id, nickname, password, salt, head, redister_date, login_count) " +
            "VALUES (#{id}, #{nickname}, #{password}, #{salt}, #{head}, #{registerDate}, #{loginCount} )")
    MiaoshaUser insert(MiaoshaUser user);
}
