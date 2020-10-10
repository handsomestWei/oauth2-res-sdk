package com.wjy.oauth2.res.filter;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.wjy.oauth2.res.annotation.Oauth2ReqUserInfo;
import com.wjy.oauth2.res.constant.Oauth2Constant;
import com.wjy.oauth2.res.vo.UserInfo;

/** request参数自定义注解识别器 */
public class Oauth2ReqUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(UserInfo.class)
                && parameter.hasParameterAnnotation(Oauth2ReqUserInfo.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(new Integer(webRequest.getParameter(Oauth2Constant.KEY_PARAM_USERID)));
        userInfo.setMobile(webRequest.getParameter(Oauth2Constant.KEY_PARAM_MOBILE));
        return userInfo;
	}

}
