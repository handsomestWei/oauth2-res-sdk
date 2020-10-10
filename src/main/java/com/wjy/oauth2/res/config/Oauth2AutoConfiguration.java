package com.wjy.oauth2.res.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.wjy.oauth2.res.constant.Oauth2Constant;
import com.wjy.oauth2.res.interfaces.IAccessRateService;
import com.wjy.oauth2.res.interfaces.IOauth2CacheService;
import com.wjy.oauth2.res.interfaces.IOauth2Service;
import com.wjy.oauth2.res.service.AccessRateServiceImpl;
import com.wjy.oauth2.res.service.Oauth2CacheServiceImpl;
import com.wjy.oauth2.res.service.Oauth2ServiceImpl;

@Configuration
@EnableConfigurationProperties(Oauth2Properties.class)
@ConditionalOnProperty(prefix = "com.wjy.oauth2", name = "enable", havingValue = "true")
@Import({ Oauth2WebConfig.class })
public class Oauth2AutoConfiguration {

    private Oauth2Properties prop;
    private RedisTemplate<String, Object> redisTpl;
    private RestTemplate restTpl;

    public Oauth2AutoConfiguration(Oauth2Properties prop) {
        this.prop = prop;
    }
	
    @Bean(name = "oauth2RedisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        RedisSerializer<String> stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        RedisStandaloneConfiguration jedisConfig = new RedisStandaloneConfiguration();
        jedisConfig.setHostName(prop.getRedisHostName());
        jedisConfig.setDatabase(prop.getRedisDatabaseIndex());
        jedisConfig.setPort(prop.getRedisPort());
        jedisConfig.setPassword(prop.getRedisPassword());

        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(jedisConfig);
        template.setConnectionFactory(connectionFactory);
        redisTpl = template;
        return template;
    }
 
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);
        factory.setConnectTimeout(5000);
        return factory;
    }
    
    @Bean(name = "oauth2RestTemplate")
    public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
        return new RestTemplate(factory);
    }
    
    @Bean(name = "accessRateService")
    @ConditionalOnMissingBean(IAccessRateService.class)
    public IAccessRateService AccessRateServiceImpl() {
        AccessRateServiceImpl accessRateService = new AccessRateServiceImpl();
        accessRateService.setProp(prop);
        accessRateService.setAccessRateLimitCache();
        return accessRateService;
    }

    @Bean(name = "oauth2CacheService")
    @ConditionalOnMissingBean(IOauth2CacheService.class)
    public IOauth2CacheService OauthCacheServiceImpl() {
        Oauth2CacheServiceImpl cacheServiceImpl = new Oauth2CacheServiceImpl();
        cacheServiceImpl.setProp(prop);
        switch (prop.getStoreMode()) {
            case Oauth2Constant.STORE_MODE_MEM:
                cacheServiceImpl.setScopeCache();
                cacheServiceImpl.setUserInfoCache();
                break;
            case Oauth2Constant.STORE_MODE_REDIS:
                cacheServiceImpl.setRedisTemplate(redisTpl);
                break;
            default:
                throw new RuntimeException(
                        "init oauth2CacheService error: store mode " + prop.getStoreMode() + " unsupport");
        }
        return cacheServiceImpl;
    }

    @Bean(name = "oauth2Service")
    @ConditionalOnMissingBean(IOauth2Service.class)
    public IOauth2Service OauthServiceImpl(IAccessRateService accessRateService, IOauth2CacheService cacheService) {
        Oauth2ServiceImpl oauthService = new Oauth2ServiceImpl();
        oauthService.setProp(prop);
        oauthService.setRestTemplate(restTpl);
        oauthService.setClientIdSet();
        oauthService.setGetUInfoUrl();
        oauthService.setAccessRateService(accessRateService);
        oauthService.setCacheService(cacheService);
        return oauthService;
    }

}
