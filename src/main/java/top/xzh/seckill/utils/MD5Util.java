package top.xzh.seckill.utils;


import org.springframework.util.DigestUtils;

public class MD5Util {
//    固定盐值
    private static final String SALT="xzh";
    public static String md5(String str){
        return DigestUtils.md5DigestAsHex(str.getBytes());
    }

    //使用固定盐值，对输入的密码加密
//    在前端进行加密后传输
    public static String  inputPassToFromPass(String str){
        str = ""+SALT.charAt(1)+SALT.charAt(0)+str+SALT.charAt(1)+SALT.charAt(1);
        return md5(str);
    }
    //使用随机盐值，对输入的密码和数据库随机盐值生成密码
    public static String formPassToDbPass(String str, String dbSalt){
        str=inputPassToFromPass(str);
        String dbPass = ""+dbSalt.charAt(0)+dbSalt.charAt(1)+str+dbSalt.charAt(0)+dbSalt.charAt(0);
        return md5(dbPass);
    }
    public static void main(String[] args) {
        System.out.println(formPassToDbPass("123456","asdfgh"));
    }

}
