package com.wjy.oauth2.res.constant;

public enum RspEnum {
	
	RSP_SUCCESS("0000", "处理完成"),
	RSP_FAIL("2998", "处理失败"),
	RSP_UNAUTHORIZED("6006", "资源所有者拒绝该请求(访问令牌已失效或不存在)");
	
	private String rspCode;
	private String rspInfo;
	
	RspEnum(String rspCode, String rspInfo) {
		this.rspCode = rspCode;
		this.rspInfo = rspInfo;
	}
	
	public String getRspCode() {
		return rspCode;
	}
	
	public String getRspInfo() {
		return rspInfo;
	}

}
