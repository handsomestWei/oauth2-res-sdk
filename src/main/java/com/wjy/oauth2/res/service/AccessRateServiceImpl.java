package com.wjy.oauth2.res.service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wjy.oauth2.res.config.Oauth2Properties;
import com.wjy.oauth2.res.constant.Oauth2Constant;
import com.wjy.oauth2.res.interfaces.IAccessRateService;

@Service
public class AccessRateServiceImpl implements IAccessRateService, InitializingBean {

    private Cache<String, AtomicInteger> AccessRateLimitCache;

    @Autowired
    private Oauth2Properties prop;

    @Override
    public boolean isAccessRateLimit(String ipAddr) {
        String storeKey = String.format(Oauth2Constant.ACCESS_LIMIT_KEY, prop.getSysCode(), ipAddr);
        AtomicInteger count = AccessRateLimitCache.getIfPresent(storeKey);
        if (count == null) {
            return false;
        }
        if (count.get() > prop.getLimitAccessCount()) {
            // 超过请求限制次数，禁止访问
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void saveAccessRateLimit(String ipAddr) {
        String storeKey = String.format(Oauth2Constant.ACCESS_LIMIT_KEY, prop.getSysCode(), ipAddr);
        AtomicInteger count = AccessRateLimitCache.getIfPresent(storeKey);
        if (count == null) {
            count = new AtomicInteger(1);
            AccessRateLimitCache.put(storeKey, count);
        } else {
            count.incrementAndGet();
        }
    }

    @Override
    public void removeAccessRateLimit(String ipAddr) {
        String storeKey = String.format(Oauth2Constant.ACCESS_LIMIT_KEY, prop.getSysCode(), ipAddr);
        AccessRateLimitCache.invalidate(storeKey);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        AccessRateLimitCache = CacheBuilder.newBuilder()
                .maximumSize(prop.getStoreMemMaxSize())
                .expireAfterWrite(prop.getExpireScAccessLimit(), TimeUnit.SECONDS)
                .build();
    }

    public void setAccessRateLimitCache() {
        AccessRateLimitCache = CacheBuilder.newBuilder()
                .maximumSize(prop.getStoreMemMaxSize())
                .expireAfterWrite(prop.getExpireScAccessLimit(), TimeUnit.SECONDS)
                .build();
    }

    public void setProp(Oauth2Properties prop) {
        this.prop = prop;
    }

}
