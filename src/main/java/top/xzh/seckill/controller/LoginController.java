package top.xzh.seckill.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import top.xzh.seckill.result.Result;
import top.xzh.seckill.service.MiaoshaUserService;
import top.xzh.seckill.vo.LoginVo;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Controller
@Slf4j
@RequestMapping("login")
public class LoginController {

    private final MiaoshaUserService userService;

    @Autowired
    public LoginController(MiaoshaUserService userService) {
        this.userService = userService;
    }

    @GetMapping({"/login"})
    public String login() {
        return "login";
    }

    /**
     * 参数校验
     * */
    @PostMapping("/do_login")
    @ResponseBody
    public Result login(HttpServletResponse response, @Valid LoginVo loginVo) {
        boolean login = userService.login(response, loginVo);
        return Result.success(true);
    }


}
