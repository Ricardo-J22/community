package com.demo.community.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName login_ticket
 */
@Data
public class LoginTicket implements Serializable {
    /**
     * 
     */
    private Integer id;

    /**
     * 
     */
    private Integer userId;

    /**
     * 
     */
    private String ticket;

    /**
     * 0-有效; 1-无效;
     */
    private Integer status;

    /**
     * 
     */
    private Date expired;

    private static final long serialVersionUID = 1L;
}