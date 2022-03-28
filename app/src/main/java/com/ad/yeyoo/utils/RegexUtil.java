package com.ad.yeyoo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by endyc on 2016-03-29.
 */
public class RegexUtil {

    private static final String HOSTNAME_REGEXP = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
            + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";

    /// <summary>
    ///  判断输入的是否为车牌号码
    /// </summary>
    /// <param name="input"></param>
    /// <returns></returns>
    public static boolean IsCarNo(String input) {
        Pattern regex = Pattern.compile("^[\u4e00-\u9fa5]{1}[A-Z]{1}[A-Z_0-9]{5}$");
        Matcher matcher = regex.matcher(input);
        return matcher.matches();
    }

    /// <summary>
    /// 判断输入的身份证号码
    /// </summary>
    /// <param name="input"></param>
    /// <returns></returns>
    public static boolean IsIdCard(String input) {
        Pattern regex = Pattern.compile("^[1-9]([0-9]{14}|[0-9]{16}([A-Z]{1}|[0-9]{1}))$");
        Matcher matcher = regex.matcher(input);
        return matcher.matches();
    }

    /// <summary>
    /// 判断输入的字符串只包含数字
    /// 可以匹配整数和浮点数
    /// ^-?\d+$|^(-?\d+)(\.\d+)?$
    /// </summary>
    /// <param name="input"></param>
    /// <returns></returns>
    public static boolean IsNumber(String input) {
        Pattern regex = Pattern.compile("^-?\\d+$|^(-?\\d+)(\\.\\d+)?$");
        Matcher matcher = regex.matcher(input);
        return matcher.matches();
    }

    /// <summary>
    /// 判断输入的字符串是否只包含数字
    /// </summary>
    /// <param name="input"></param>
    /// <returns></returns>
    public static boolean IsDec(String input) {
        Pattern regex = Pattern.compile("^[0-9]+$");
        Matcher matcher = regex.matcher(input);
        return matcher.matches();
    }

    /// <summary>
    /// 判断输入的字符串是否IP4
    /// </summary>
    /// <param name="input"></param>
    /// <returns></returns>
    public static boolean IsIP4(String input) {
        return input.matches(HOSTNAME_REGEXP);
    }

}
