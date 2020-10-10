package com.wjy.oauth2.res.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.wjy.oauth2.res.config.Oauth2Properties;
import com.wjy.oauth2.res.constant.Oauth2Constant;
import com.wjy.oauth2.res.interfaces.IOauth2CacheService;
import com.wjy.oauth2.res.vo.Oauth2UserInfo;
import com.wjy.oauth2.res.vo.UserInfo;

@Service("oauth2CacheService")
public class Oauth2CacheServiceImpl implements IOauth2CacheService, InitializingBean {
	
	private Cache<Object, Object> scopeCache;
	private Cache<Object, Object> userInfoCache;

	@Autowired
	private Oauth2Properties prop;
	@Autowired(required=false)
    @Qualifier(value = "oauth2RedisTemplate")
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public void afterPropertiesSet() throws Exception {
		String storeMode = prop.getStoreMode();
		if (!verifyStoreMode(storeMode)) {
            throw new RuntimeException(
                    "init oauth2CacheService error: store mode " + prop.getStoreMode() + " unsupport");
		}
		if (Oauth2Constant.STORE_MODE_MEM.equals(storeMode)) {
			scopeCache = CacheBuilder.newBuilder()
                    .maximumSize(prop.getStoreMemMaxSize())
                    .expireAfterWrite(prop.getExpireScAfterWrite(), TimeUnit.SECONDS)
                    .build();
			userInfoCache = CacheBuilder.newBuilder()
                    .maximumSize(prop.getStoreMemMaxSize())
                    .expireAfterWrite(prop.getExpireScAfterWrite(), TimeUnit.SECONDS)
                    .build();
		}
	}

	@Override
    public void storeUserInfo(String token, Oauth2UserInfo oauth2UserInfo) {
		String storeKey = String.format(Oauth2Constant.STORE_UINFO_KEY, prop.getSysCode(), token);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(oauth2UserInfo.getUserId());
        userInfo.setMobile(oauth2UserInfo.getMobile());
		switch (prop.getStoreMode()) {
		case Oauth2Constant.STORE_MODE_REDIS:
                redisTemplate.opsForValue().set(storeKey, userInfo, oauth2UserInfo.getExpires(), TimeUnit.SECONDS);
                break;
		case Oauth2Constant.STORE_MODE_MEM:
                userInfoCache.put(storeKey, userInfo);
                break;
		default:
			break;
		}
	}

	@Override
	public void storeClientScope(String clientId, String scope, long expires) {
		HashSet<String> scopeSet = Stream.of(scope.split("\\|")).collect(Collectors.toCollection(HashSet::new));
		String storeKey = String.format(Oauth2Constant.STORE_SCOPE_KEY, prop.getSysCode(), clientId);
		switch (prop.getStoreMode()) {
		case Oauth2Constant.STORE_MODE_REDIS:
                redisTemplate.opsForSet().add(storeKey, scopeSet.toArray());
                redisTemplate.expire(storeKey, expires, TimeUnit.SECONDS);
                break;
		case Oauth2Constant.STORE_MODE_MEM:
                scopeCache.put(storeKey, scopeSet);
                break;
		default:
                break;
		}
	}

    @SuppressWarnings("unchecked")
    @Override
	public Set<Object> getScopes(String clientId) {
		String storeKey = String.format(Oauth2Constant.STORE_SCOPE_KEY, prop.getSysCode(), clientId);
		switch (prop.getStoreMode()) {
		case Oauth2Constant.STORE_MODE_REDIS:
                return redisTemplate.opsForSet().members(storeKey);
		case Oauth2Constant.STORE_MODE_MEM:
                return (Set<Object>) scopeCache.getIfPresent(storeKey);
		default:
                return null;
        }
	}

	@Override
    public UserInfo getUserInfo(String token) {
		String storeKey = String.format(Oauth2Constant.STORE_UINFO_KEY, prop.getSysCode(), token);
		switch (prop.getStoreMode()) {
		case Oauth2Constant.STORE_MODE_REDIS:
                return (UserInfo) redisTemplate.opsForValue().get(storeKey);
		case Oauth2Constant.STORE_MODE_MEM:
                return (UserInfo) userInfoCache.getIfPresent(storeKey);
		default:
                return null;
		}
	}
	
	@Override
    public boolean isExistToken(String token) {
		String storeKey = String.format(Oauth2Constant.STORE_UINFO_KEY, prop.getSysCode(), token);
		switch (prop.getStoreMode()) {
		case Oauth2Constant.STORE_MODE_REDIS:
			return redisTemplate.hasKey(storeKey);
		case Oauth2Constant.STORE_MODE_MEM:
			return userInfoCache.getIfPresent(storeKey) != null;
		default:
			return false;
		}
	}

	private boolean verifyStoreMode(String storeMode) {
		if (Oauth2Constant.STORE_MODE_MEM.equals(storeMode) || 
				Oauth2Constant.STORE_MODE_REDIS.equals(storeMode)) {
			return true;
		} else {
			return false;
		}
	}

    public void setScopeCache() {
        scopeCache = CacheBuilder.newBuilder()
                .maximumSize(prop.getStoreMemMaxSize())
                .expireAfterWrite(prop.getExpireScAfterWrite(), TimeUnit.SECONDS)
                .build();
    }

    public void setUserInfoCache() {
        userInfoCache = CacheBuilder.newBuilder()
                .maximumSize(prop.getStoreMemMaxSize())
                .expireAfterWrite(prop.getExpireScAfterWrite(), TimeUnit.SECONDS)
                .build();
    }

    public void setProp(Oauth2Properties prop) {
        this.prop = prop;
    }

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

}
