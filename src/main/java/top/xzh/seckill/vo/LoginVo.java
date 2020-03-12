package top.xzh.seckill.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import top.xzh.seckill.validator.IsMobile;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
public class LoginVo implements Serializable{

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @Length(min = 6)
    private String password;
}
