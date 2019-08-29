package com.bittech.java.vo;

import lombok.Data;

/**
 * @Author: yuisama
 * @Date: 2019-08-13 10:26
 * @Description:服务器与客户端传递信息载体
 */
@Data
public class MessageVO {
    /**
     * 表示告知服务器要进行的动作,1表示用户注册,2表示私聊
     */
    private String type;
    /**
     * 发送到服务器的具体内容
     */
    private String content;
    /**
     * 私聊告知服务器要将信息发给哪个用户
     */
    private String to;
}
