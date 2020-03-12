package top.xzh.seckill.config.web;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import top.xzh.seckill.domain.MiaoshaUser;
import top.xzh.seckill.access.UserContext;

/**
 * 自定义参数解析器
 * 实现此方法, 将自动对方法入参包含MiaoshaUser的对象的进行注入Controller
 */
@Component
//HandlerMethodArgumentResolver = HandlerMethod + Argument(参数) + Resolver(解析器)
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    //是否支持参数，当返回true 才会执行resolveArgument方法
    public boolean supportsParameter(MethodParameter parameter) {
        Class <?> parameterType = parameter.getParameterType();
        return parameterType == MiaoshaUser.class;
    }

    @Override
    //处理参数分解的方法，返回的Object就是controller方法上的形参对象。
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        //拦截器拦截到user=》将user放入ThreadLocal =》取出解析到方法参数上
        //从TheadLocal中获取miaoshaUser，并自动注入到方法参数中
        return UserContext.getUser();
    }
}
