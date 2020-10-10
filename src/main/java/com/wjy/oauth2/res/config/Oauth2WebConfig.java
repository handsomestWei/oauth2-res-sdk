package com.wjy.oauth2.res.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wjy.oauth2.res.filter.Oauth2ReqUserHandlerMethodArgumentResolver;
import com.wjy.oauth2.res.filter.Oauth2TokenFilter;

@Configuration
public class Oauth2WebConfig implements WebMvcConfigurer {
	
	@Autowired
	private Oauth2Properties prop;
	
    public Oauth2WebConfig(Oauth2Properties prop) {
        this.prop = prop;
    }

	@Bean
    public FilterRegistrationBean<Oauth2TokenFilter> registerTokenFilter() {
        FilterRegistrationBean<Oauth2TokenFilter> filterRegistrationBean = new FilterRegistrationBean<Oauth2TokenFilter>();
    	Oauth2TokenFilter filter = new Oauth2TokenFilter();
        filterRegistrationBean.setFilter(filter);
        filterRegistrationBean.addUrlPatterns(prop.getFilterUrls().split(","));
        filterRegistrationBean.setName("oauth2TokenFilter");
        filterRegistrationBean.setOrder(prop.getFilterOrder());
        return filterRegistrationBean;
    }
	
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new Oauth2ReqUserHandlerMethodArgumentResolver());
    }

}
