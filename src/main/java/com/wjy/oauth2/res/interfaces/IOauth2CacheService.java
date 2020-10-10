package com.wjy.oauth2.res.interfaces;

import java.util.Set;

import com.wjy.oauth2.res.vo.Oauth2UserInfo;
import com.wjy.oauth2.res.vo.UserInfo;

/**
 * Oauth2缓存接口</br>
 * 支持内存和redis两种存储模式
 */
public interface IOauth2CacheService {
	
    /** 保存用户信息 */
	public void storeUserInfo(String token, Oauth2UserInfo uInfo);
	
    /** 保存客户端可访问域 */
	public void storeClientScope(String clientId, String scope, long expires);
	
    /** 获取客户端可访问域 */
	public Set<Object> getScopes(String clientId);
	
    /** 获取用户信息 */
    public UserInfo getUserInfo(String token);
	
    /** 令牌是否已缓存 */
	public boolean isExistToken(String token);

}
