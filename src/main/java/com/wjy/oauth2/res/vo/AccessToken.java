package com.wjy.oauth2.res.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/** Oauth2令牌对象 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {
	
    /** 接口返回码 */
	private String rspCode;
    /** 令牌内容 */
	@JsonProperty("access_token")
	private String accessToken;
    /** 刷新用的令牌内容 */
	@JsonProperty("refresh_token")
	private String refreshToken;
    /** 令牌有效期 */
	@JsonProperty("expires_in")
	private Long expiresIn;
}