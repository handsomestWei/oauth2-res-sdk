package com.wjy.oauth2.res.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSONObject;
import com.wjy.oauth2.res.config.Oauth2Properties;
import com.wjy.oauth2.res.constant.Oauth2Constant;
import com.wjy.oauth2.res.constant.RspEnum;
import com.wjy.oauth2.res.interfaces.IOauth2Service;
import com.wjy.oauth2.res.utils.IpUtil;
import com.wjy.oauth2.res.vo.Oauth2BaseRsp;
import com.wjy.oauth2.res.vo.UserInfo;

import lombok.extern.slf4j.Slf4j;

/** Oauth2令牌过滤器 */
@Slf4j
public class Oauth2TokenFilter implements Filter {
	
	private Oauth2Properties prop;
    private IOauth2Service oauth2Service;
	private Pattern pt = Pattern.compile("Bearer\\s{1}(.*)\\.(.*)");
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ServletContext sc = filterConfig.getServletContext();
		WebApplicationContext cxt = WebApplicationContextUtils.getWebApplicationContext(sc);
        if (cxt != null && cxt.getBean("oauth2Service") != null && oauth2Service == null) {
            oauth2Service = (IOauth2Service) cxt.getBean("oauth2Service");
		}
		if (cxt != null && cxt.getBean(Oauth2Properties.class) != null && prop == null) {
			prop = cxt.getBean(Oauth2Properties.class);
		}
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		AuthorizationResult result = this.verifyAuthorization(request);
		if (result.result) {
			chain.doFilter(result.wrapper, response);
		} else {
			rtAuthFail(response);
		}
	}
	
    /** 校验请求头 */
	public AuthorizationResult verifyAuthorization(ServletRequest request) {
		AuthorizationResult result = new AuthorizationResult();
		try {
			HttpServletRequest req = (HttpServletRequest) request;
			String authorization = req.getHeader(Oauth2Constant.KEY_HEADER_AUTH);
			if (StringUtils.isEmpty(authorization)) {
				return result;
			}
            // 从请求头获取客户端id和Oauth2令牌
			Matcher m = pt.matcher(authorization);
			if (!m.find()) {
				log.debug("authorization={} verify fail", authorization);
				return result;
			}
			String clientId = new String(Base64.getDecoder().decode(m.group(1)));
			String token = new String(Base64.getDecoder().decode(m.group(2)));
            if (!oauth2Service.verifyClientId(clientId)) {
				log.debug("clientId={} verify fail", clientId);
				return result;
			}
            String ipAddr = IpUtil.getIpAddr(req);
            if (oauth2Service.isAccessRateLimit(ipAddr)) {
                // 重刷，限制访问
                log.debug("ipAddr={} access limit", ipAddr);
                return result;
            }
            if (!oauth2Service.verifyToken(clientId, token)) {
				log.debug("token={} verify fail", token);
                // 记录验证失败次数。达到一定次数时，一段时间内禁止访问
                oauth2Service.saveAccessRateLimit(ipAddr);
				return result;
			}
			String uri = ((HttpServletRequest) request).getRequestURI();
            if (!oauth2Service.verifyScope(clientId, new String[] { uri })) {
				log.debug("scope={} verify fail", uri);
				return result;
			}
            // 验证通过，移除访问限制
            oauth2Service.removeAccessRateLimit(ipAddr);

            // 把用户信息放入请求参数中
            Oauth2ParamReqWrapper wrapper = setUserInfo2ParamReq(req, oauth2Service.getUserInfo(token));
			result.result = true;
			result.wrapper = wrapper;
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
	}
	
    /** 用令牌交换出用户信息，写入请求参数中 */
    public Oauth2ParamReqWrapper setUserInfo2ParamReq(HttpServletRequest req, UserInfo userInfo) {
		Oauth2ParamReqWrapper wrapper = new Oauth2ParamReqWrapper(req);
        if (userInfo != null) {
            wrapper.addParameter(Oauth2Constant.KEY_PARAM_USERID, userInfo.getUserId());
            wrapper.addParameter(Oauth2Constant.KEY_PARAM_MOBILE, userInfo.getMobile());
		}
        return wrapper;
	}
	
    /** Oauth2校验失败返回结果 */
    @SuppressWarnings("deprecation")
    private void rtAuthFail(ServletResponse response) {
		try {
			Oauth2BaseRsp rsp = new Oauth2BaseRsp();
			rsp.setRspCode(RspEnum.RSP_UNAUTHORIZED.getRspCode());
			rsp.setRspInfo(RspEnum.RSP_UNAUTHORIZED.getRspInfo());
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.setStatus(HttpStatus.UNAUTHORIZED.value());
			resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
			resp.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
			PrintWriter writer = resp.getWriter();
			writer.write(JSONObject.toJSONString(rsp));
			writer.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}	
	}
	
    /** 请求头校验结果 */
	private class AuthorizationResult {
		private boolean result;
		private Oauth2ParamReqWrapper wrapper;
	}
}
