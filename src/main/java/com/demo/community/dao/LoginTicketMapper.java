package com.demo.community.dao;

import com.demo.community.entity.LoginTicket;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Ricardo
* @description 针对表【login_ticket】的数据库操作Mapper
* @createDate 2022-05-02 19:15:53
* @Entity com.demo.community.entity.LoginTicket
*/

@Mapper
@Deprecated
public interface LoginTicketMapper {


    int insertLoginTicket(LoginTicket record);

    LoginTicket selectByTicket(String ticket);

    int updateStatus(String ticket, int status);

}
