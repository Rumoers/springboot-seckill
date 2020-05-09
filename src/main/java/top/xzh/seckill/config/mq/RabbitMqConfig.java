package top.xzh.seckill.config.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.xzh.seckill.common.Constant;

import java.util.HashMap;


@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue miaoshaQueue() {
        return new Queue(Constant.MIAOSHA_QUEUE, true);
    }

    /**
     * Direct模式,exchange
     */
    @Bean
    public Queue queue() {
        return new Queue(Constant.DEFAULT_QUEUE_NAME, true);
    }

    /**
     * Topic模式, exchange
     * 订阅模式
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(Constant.TOPIC_EXCHANGE);
    }

    @Bean
    public Queue topicQueue1() {
        return new Queue(Constant.TOPIC_QUEUE_1, true);
    }

    @Bean
    public Queue topicQueue2() {
        return new Queue(Constant.TOPIC_QUEUE_2, true);
    }

    /*
     * 通过key, 将队列和交换机进行绑定
     */
    @Bean
    public Binding topicBinding1() {
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(Constant.ROUTING_KEY_1);
    }

    @Bean
    public Binding topicBinding2() {
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(Constant.ROUTING_KEY_2);
    }


    /**
     * Fanout模式, exchange
     * 广播模式
     */
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(Constant.FANOUT_EXCHANGE);
    }

    @Bean Queue fanoutQueue2() {
        return new Queue(Constant.FANOUT_QUEUE_2, true);
    }

    @Bean Queue fanoutQueue1() {
        return new Queue(Constant.FANOUT_QUEUE_1, true);
    }

    @Bean
    public Binding fanoutBinding1() {
        return BindingBuilder.bind(fanoutQueue1()).to(fanoutExchange());
    }

    @Bean
    public Binding fanoutBinding2() {
        return BindingBuilder.bind(fanoutQueue2()).to(fanoutExchange());
    }




}
