package com.wjy.oauth2.res.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wjy.oauth2.res.config.Oauth2Properties;
import com.wjy.oauth2.res.constant.Oauth2Constant;
import com.wjy.oauth2.res.constant.RspEnum;
import com.wjy.oauth2.res.interfaces.IAccessRateService;
import com.wjy.oauth2.res.interfaces.IOauth2CacheService;
import com.wjy.oauth2.res.interfaces.IOauth2Service;
import com.wjy.oauth2.res.vo.Oauth2UserInfo;
import com.wjy.oauth2.res.vo.UserInfo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("oauth2Service")
public class Oauth2ServiceImpl implements IOauth2Service, InitializingBean {
		
	@Autowired
	private Oauth2Properties prop;
    @Resource(name = "oauth2RestTemplate")
	private RestTemplate restTemplate;
	@Resource
	private IOauth2CacheService cacheService;
    @Resource
    IAccessRateService accessRateService;
	
	private String getUInfoUrl;
	private HashSet<String> clientIdSet;
	
	@Override
	public Oauth2UserInfo getUserInfoByAccessToken(String clientId, String token) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("client_id", clientId);
		params.add("secret", prop.getSecret());
		params.add("sysCode", prop.getSysCode());
		params.add("access_token", token);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params,
                headers);
        Oauth2UserInfo info;
        try {
        	ResponseEntity<String> response = restTemplate.exchange(this.getUInfoUrl, HttpMethod.POST, requestEntity, String.class);
        	info = new ObjectMapper().readValue(response.getBody(), Oauth2UserInfo.class);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			info = new Oauth2UserInfo();
			info.setRspCode(RspEnum.RSP_FAIL.getRspCode());
		}
		return info;		
	}
	
	@Override
    public UserInfo getUserInfo(String token) {
        return cacheService.getUserInfo(token);
	}
	
	@Override
	public boolean verifyClientId(String clientId) {
		if (StringUtils.isEmpty(clientId)) {
			return false;
		}
		return clientIdSet.contains(clientId);
	}
	
	@Override
	public boolean verifyToken(String clientId, String token) {
		if (StringUtils.isEmpty(token)) {
			return false;
		}
		if (cacheService.isExistToken(token)) {
			return true;
		}
		Oauth2UserInfo uInfo = getUserInfoByAccessToken(clientId, token);
		if (!RspEnum.RSP_SUCCESS.getRspCode().equals(uInfo.getRspCode())) {
			return false;
		}
		if (!uInfo.isAuthorized()) {
			return false;
		}
		cacheService.storeUserInfo(token, uInfo);
		cacheService.storeClientScope(clientId, uInfo.getScope(), uInfo.getExpires());
		return true;
	}
	
	@Override
	public boolean verifyScope(String clientId, String[] reqRes) {
		if (StringUtils.isEmpty(clientId)) {
			return false;
		}
		try {
			Set<Object> scopeSet = cacheService.getScopes(clientId);
			if (scopeSet == null) {
				return false;
        	}
			return null == Arrays.stream(reqRes).filter(scope->!scopeSet.contains(scope)).findAny().orElse(null) 
					? true:false;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

    @Override
    public boolean isAccessRateLimit(String ipAddr) {
        return accessRateService.isAccessRateLimit(ipAddr);
    }

    @Override
    public void saveAccessRateLimit(String ipAddr) {
        accessRateService.saveAccessRateLimit(ipAddr);
    }

    @Override
    public void removeAccessRateLimit(String ipAddr) {
        accessRateService.removeAccessRateLimit(ipAddr);
    }

	@Override
	public void afterPropertiesSet() throws Exception {
		clientIdSet = (HashSet<String>) Stream.of(prop.getClientIds().split("\\|"))
		        .collect(Collectors.toSet());
        getUInfoUrl = String.format(Oauth2Constant.URL_OAUTH_UINFO, prop.getOauth2Url());
	}

    public void setProp(Oauth2Properties prop) {
        this.prop = prop;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setCacheService(IOauth2CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public void setAccessRateService(IAccessRateService accessRateService) {
        this.accessRateService = accessRateService;
    }

    public void setGetUInfoUrl() {
        getUInfoUrl = String.format(Oauth2Constant.URL_OAUTH_UINFO, prop.getOauth2Url());
    }

    public void setClientIdSet() {
        clientIdSet = (HashSet<String>) Stream.of(prop.getClientIds().split("\\|"))
                .collect(Collectors.toSet());
    }
}
