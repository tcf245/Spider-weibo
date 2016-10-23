package com.bfd.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tcf24 on 2016/10/23.
 */
public class StringUtils {

    public static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static void main(String[] args) throws IOException {
        String str = FileUtils.readFileToString(new File("etc/target.txt"));
        String s = unicodeToString(str);
        System.out.println(s);
    }
}
