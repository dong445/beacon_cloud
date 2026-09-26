package com.dong.api.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//手机号码格式检验
public class PhoneFormatCheckUtil{


    // 国内手机号正则：覆盖 13x / 14x / 15x / 16x / 17x / 18x / 19x
    private static final Pattern CHINA_PATTERN = Pattern.compile(
            "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$");

    public static boolean isChinaPhone(String number) {
        if (number == null) {
            return false;
        }
        Matcher matcher = CHINA_PATTERN.matcher(number);
        return matcher.matches();
    }

}
