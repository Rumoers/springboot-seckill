package top.xzh.seckill.config.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


@Configuration
public class JedisConfigution {

    private final RedisConfig redisConfig;

    @Autowired
    public JedisConfigution(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }
    @Bean
    public JedisPool jedisPool(JedisPoolConfig poolConfig) {
        String password = "".equals(redisConfig.getPassword()) ? null : redisConfig.getPassword();
        return new JedisPool(poolConfig, redisConfig.getHost(), redisConfig.getPort(),
                redisConfig.getTimeout(), password, redisConfig.getDatabase());
    }
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(redisConfig.getPoolMaxTotal());
        poolConfig.setMaxIdle(redisConfig.getPoolMaxIdle());
        poolConfig.setMaxWaitMillis(redisConfig.getPoolMaxWait());
        return poolConfig;
    }


}
