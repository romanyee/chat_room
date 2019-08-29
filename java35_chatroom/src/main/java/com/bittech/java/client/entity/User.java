package com.bittech.java.client.entity;

import lombok.Data;

import java.util.Set;

/**
 * @Author: yuisama
 * @Date: 2019-08-12 09:58
 * @Description:实体类
 */
@Data
public class User {
    private Integer id;
    private String userName;
    private String password;
    private String brief;
    private Set<String> userNames;
}
