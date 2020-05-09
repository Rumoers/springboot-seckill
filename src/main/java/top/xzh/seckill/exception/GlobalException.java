package top.xzh.seckill.exception;

import lombok.Getter;
import top.xzh.seckill.result.CodeMsg;

/***
 * 全局异常类
 */
@Getter
public class GlobalException extends RuntimeException {

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }
}
