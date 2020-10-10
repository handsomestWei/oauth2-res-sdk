package com.wjy.oauth2.res.interfaces;

import com.wjy.oauth2.res.vo.Oauth2UserInfo;
import com.wjy.oauth2.res.vo.UserInfo;

/**
 * Oauth2资源服务认证接口</br>
 * 授权码模式
 */
public interface IOauth2Service {
	
    /** 根据令牌获取用户信息 */
	public Oauth2UserInfo getUserInfoByAccessToken(String clientId, String token);
	
    /** 获取用户信息 */
    public UserInfo getUserInfo(String token);
	
    /** 校验客户端id */
	public boolean verifyClientId(String clientId);
	
    /** 校验令牌 */
	public boolean verifyToken(String clientId, String token);
	
    /** 校验客户端是否有权限访问该域 */
	public boolean verifyScope(String clientId, String[] reqRes);

    /** 是否达到访问限制 */
    public boolean isAccessRateLimit(String ipAddr);

    /** 保存访问次数 */
    public void saveAccessRateLimit(String ipAddr);

    /** 移除访问次数 */
    public void removeAccessRateLimit(String ipAddr);

}
