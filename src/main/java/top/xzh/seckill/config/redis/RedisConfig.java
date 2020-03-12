package top.xzh.seckill.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * redis相关属性
* */

@ConfigurationProperties("redis")
@Data
@Component
public class RedisConfig {
//    连接池相关属性
    private String host;
    private Integer port;

    private Integer timeout;//单位秒
    private String password;
    private Integer poolMaxTotal;
    private Integer poolMaxIdle;
    private Integer poolMaxWait;
    private Integer database;
}
