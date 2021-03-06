package com.demo.community.config;

import com.demo.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.demo.community.util.CommunityConstant.*;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow")
                .hasAnyAuthority(AUTHORITY_USER, AUTHORITY_ADMIN, AUTHORITY_MODERATOR)
                .antMatchers("/discuss/top", "/discuss/wonderful")
                .hasAnyAuthority(AUTHORITY_MODERATOR)
                .antMatchers("/discuss/delete")
                .hasAnyAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll().and().csrf().disable();

        //权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    //没有登录
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        String xRequestWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter printWriter = response.getWriter();
                            printWriter.write(CommunityUtil.getJSONString(403, "你还没有登录"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        String xRequestWith = request.getHeader("x-requested-with");
                        if ("XMLHttpRequest".equals(xRequestWith)) {
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter printWriter = response.getWriter();
                            printWriter.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限"));
                        } else {
                            response.sendRedirect(request.getContextPath() + "/denied");
                        }
                    }
                });
        http.logout().logoutUrl("/securityLogout");
    }
}
