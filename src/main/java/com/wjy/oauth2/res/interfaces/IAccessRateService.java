package com.wjy.oauth2.res.interfaces;

/**
 * 访问控制接口</br>
 * 防止令牌重刷
 */
public interface IAccessRateService {

    /** 是否达到访问限制 */
    public boolean isAccessRateLimit(String ipAddr);

    /** 保存访问次数 */
    public void saveAccessRateLimit(String ipAddr);

    /** 移除访问次数 */
    public void removeAccessRateLimit(String ipAddr);

}
