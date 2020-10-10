package com.wjy.oauth2.res.filter;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/** request请求自定义封装器 */
public class Oauth2ParamReqWrapper extends HttpServletRequestWrapper {
	
	private Map<String, String[]> params = new HashMap<>();

	public Oauth2ParamReqWrapper(HttpServletRequest request) {
		super(request);
		this.params.putAll(request.getParameterMap());
	}
	
	public Oauth2ParamReqWrapper(HttpServletRequest request , Map<String, Object> extendParams){
        this(request);
        addAllParameters(extendParams);
    }

    @Override
    public String getParameter(String name) {
        String[] values = params.get(name);
        if (values == null) {
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name){
        String[] values = params.get(name);
        if (values == null || values.length == 0) {
            return null;
        }
        return values;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Enumeration<String> getParameterNames() {
        return new Vector(params.keySet()).elements();
    }

    public void addAllParameters(Map<String, Object> extendParams) {
        for (Map.Entry<String , Object> entry : extendParams.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }          
    }

    public void addParameter(String key, Object value) {
        if (value != null) {
            if (value instanceof String[]) {
                params.put(key, (String[])value);
            } else if (value instanceof String) {
                params.put(key, new String[]{(String)value});
            } else {
                params.put(key, new String[]{String.valueOf(value)}); 
            }
        }
    }

}
