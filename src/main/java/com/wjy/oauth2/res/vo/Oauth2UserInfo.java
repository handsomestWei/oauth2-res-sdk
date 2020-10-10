package com.wjy.oauth2.res.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/** Oauth2用户信息 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Oauth2UserInfo {
	
    /** 接口返回码 */
	private String rspCode;
    /** 接口返回信息 */
	private String rspInfo;
    /** 令牌校验结果 */
	private boolean authorized;
    /** 令牌可访问域 */
	private String scope;
    /** 令牌有效期 */
	private Long expires;
    /** 用户id */
	private Integer userId;
    /** 用户手机号 */
	private String mobile;
}
