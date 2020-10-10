package com.wjy.oauth2.res.vo;

import lombok.Data;

/** Oauth2接口响应基类对象 */
@Data
public class Oauth2BaseRsp {
	
    /** 接口返回码 */
	private String rspCode;
    /** 接口返回信息 */
	private String rspInfo;

}
