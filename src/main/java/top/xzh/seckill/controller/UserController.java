package top.xzh.seckill.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.xzh.seckill.domain.MiaoshaUser;
import top.xzh.seckill.rabbitmq.MqSender;
import top.xzh.seckill.result.Result;

/**
 *
 */
@RestController
@RequestMapping("user")
public class UserController {

    private final MqSender sender;

    @Autowired
    public UserController(MqSender sender) {
        this.sender = sender;
    }

    @GetMapping("/info")
    public Result<MiaoshaUser> info(MiaoshaUser user) {
        return Result.success(user);
    }

    @GetMapping("mqSender")
    public void mqSender(String msg) {
        sender.sender(msg);
    }

    @GetMapping("topicSender")
    public void topicSender(String msg) {
        sender.topicSender(msg);
    }

    @GetMapping("fanoutSender")
    public void fanoutSender(String msg) {
        sender.fanoutSender(msg);
    }

    @GetMapping("headersSender")
    public void headersSender(String msg) {
        sender.headersSender(msg);
    }
}
