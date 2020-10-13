# oauth2-res-sdk
Oauth2资源服务器端sdk，授权码模式认证。功能包括：
+ 客户端clientId校验
+ 令牌有效性校验
+ 令牌缓存
+ 令牌防重刷基于ip地址
+ 令牌访问权限scope校验
+ 令牌交换用户信息
+ 用户信息缓存
+ 优雅的封装用户信息并提交给控制器处理

# 授权码模式认证流程
![流程图](https://github.com/handsomestWei/oauth2-res-sdk/blob/main/design/Oauth2-AuthorizationCode-Flow.png)

# Usage

## maven依赖
```
<dependency>
	<groupId>com.wjy</groupId>
	<artifactId>oauth2-res-spring-boot-starter</artifactId>
	<version>1.0.0</version>
</dependency>
```

## 启用
springboot项目引入maven依赖，开启配置可直接使用
```
com.wjy.oauth2.enable=true
```

## 使用`@Oauth2ReqUserInfo UserInfo`
利用自定义mvc请求注解，获取令牌的用户信息。例：
```
import com.wjy.oauth2.res.annotation.Oauth2ReqUserInfo;
import com.wjy.oauth2.res.vo.UserInfo;

@RequestMapping(value = "/payList")
public String getPayList(@Oauth2ReqUserInfo UserInfo userInfo) {
	...
}
```

## 配置说明

### 通用配置
```
// 认证服务地址
com.wjy.oauth2.oauth2-url=https://xxxxx
// 资源服务接入认证服务的密钥
com.wjy.oauth2.secret=xxxxxx
// 资源服务接入认证服务的系统编码
com.wjy.oauth2.sys-code=appName
```

### 客户端限制
支持多客户端接入，竖线`|`分隔。只有指定的客户端才能访问该资源
```
com.wjy.oauth2.client-ids=clientA|clientB
```

### 过滤器配置
支持多url拦截，逗号`,`分隔。拦截指定的请求，校验令牌和权限
```
com.wjy.oauth2.filter-order=1
com.wjy.oauth2.filter-urls=/api/v1/trade/*,/api/v1/help
```

### 令牌和用户信息存储
支持redis和内存两种方式。默认使用redis

#### redis存储配置
```
// 指定redis存储
com.wjy.oauth2.store-mode=redis
// redis连接配置
com.wjy.oauth2.redis-host-name=xxxx
com.wjy.oauth2.redis-database-index=5
com.wjy.oauth2.redis-port=6379
com.wjy.oauth2.redis-password=xxxx
```

#### 内存存储配置
```
// 指定内存存储
com.wjy.oauth2.store-mode=memory
// 记录最大条数，超过将清除最旧数据
com.wjy.oauth2.store-mem-max-size=100000
// 缓存有效期，单位秒
com.wjy.oauth2.expire-sc-after-write=300
```
