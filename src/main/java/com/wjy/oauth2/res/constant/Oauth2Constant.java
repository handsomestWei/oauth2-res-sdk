package com.wjy.oauth2.res.constant;

public class Oauth2Constant {
	
    /** 包扫描目录 */
    public static final String SCAN_PACKAGES = "com.wjy.oauth2.res";
	
    /** 令牌缓存key名 */
	public static final String STORE_UINFO_KEY = "%s:TOKEN:%s";
    /** 访问域缓存key名 */
	public static final String STORE_SCOPE_KEY = "%s:SCOPE:%s";
	
    /** Oauth2认证模式：授权码模式 */
	public static final String GRANT_TYPE_CODE = "authorization_code";
    /** Oauth2令牌校验：http请求url */
	public static final String URL_OAUTH_UINFO = "%s/oauth2/checkAccessToken";
	
    /** 认证请求头key名 */
	public static final String KEY_HEADER_AUTH = "Authorization";
    /** request请求参数key：用户id */
    public static final String KEY_PARAM_USERID = "oauth_userId";
    /** request请求参数key：用户手机号 */
    public static final String KEY_PARAM_MOBILE = "oauth_mobile";
	
    /** 存储模式：使用内存缓存 */
	public static final String STORE_MODE_MEM = "memory";
    /** 存储模式：使用redis缓存 */
	public static final String STORE_MODE_REDIS = "redis";

    /** 访问控制key */
    public static final String ACCESS_LIMIT_KEY = "%s:IP:%s";

}
