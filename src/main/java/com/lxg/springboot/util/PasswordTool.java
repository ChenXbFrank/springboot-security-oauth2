package com.lxg.springboot.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author 小石潭记
 * @date 2020/7/5 19:09
 * @Description: 加解密的工具
 */
public class PasswordTool {

    public static void main(String[] args) {
        String pass_admin = "admin";
        String pass_123456 = "123456";
        BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
        String hashPass1 = bcryptPasswordEncoder.encode(pass_admin);
        System.out.println(hashPass1);
        String p_admin = "$2a$10$cAVX9v/h/YhHlKGrMP4Xwuf2Buc1ksN35jCzw0Bov5zQzjfLEyPVC";
        String hashPass2 = bcryptPasswordEncoder.encode(pass_123456);
        System.out.println(hashPass2);
        String p_123456 = "$2a$10$jH.e74/Oy41OfUsSgi3KQu5MlC/9ISytTS0mMl0YiXM0RdbfijmS.";

        // 解密判断密码是否相等
        boolean f = bcryptPasswordEncoder.matches("admin",p_admin);
        System.out.println(f);

        boolean f1 = bcryptPasswordEncoder.matches("123456",p_123456);
        System.out.println(f1);

    }

}
