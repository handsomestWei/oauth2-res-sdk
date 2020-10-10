package com.wjy.oauth2.res.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.wjy.oauth2.res.constant.Oauth2Constant;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "com.wjy.oauth2")
public class Oauth2Properties {
	
	private String clientIds = "";
    private String oauth2Url;
	private String sysCode;
	private String secret;
	private String filterUrls;
	private int filterOrder;

    private String storeMode = Oauth2Constant.STORE_MODE_REDIS;
	private long expireScAfterWrite = 300;

    private long storeMemMaxSize = 100000;
    private long expireScAccessLimit = 120;
    private int limitAccessCount = 2;

    private String redisHostName;
    private int redisDatabaseIndex;
    private int redisPort;
    private String redisPassword;

}
