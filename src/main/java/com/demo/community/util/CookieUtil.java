package com.demo.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name){
        if(request == null || name == null){
            throw new IllegalArgumentException("参数为空");
        }
        if(request.getCookies()!=null){
            for (Cookie cookie : request.getCookies()) {
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return  null;

    }
}
