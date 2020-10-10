package com.wjy.oauth2.res.vo;

import lombok.Builder;

/** Oauth2接口响应对象 */
@Builder(toBuilder=true)
public class Oauth2Rsp extends Oauth2BaseRsp {
	
    /** 令牌内容 */
	private String accessToken;
    /** 令牌有效期 */
	private long expiresIn;
    /** 用户id */
	private int userId;
    /** 用户手机号 */
	private String mobile;
}
