package com.wjy.oauth2.res.vo;

import java.io.Serializable;

import lombok.Data;

/** Oauth2用户信息 */
@Data
public class UserInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4154289053371223630L;
    /** 用户id */
    private Integer userId;
    /** 用户手机号 */
    private String mobile;

}
