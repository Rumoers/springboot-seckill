package top.xzh.seckill.utils;

import java.util.UUID;


public class UUIDUtil {

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
//    e6d47eb8-3dc6-4457-8855-b830616e91fc
    public static void main(String[] args){
        System.out.println(uuid());
    }
}
