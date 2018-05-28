package com.trackingbus.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import resources.Assets;

public class AllRequestInterceptor extends HandlerInterceptorAdapter {

	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		HttpSession session = request.getSession() ;
		
		if(session.getAttribute(Assets.LANGUAGE) == null ) {
			session.setAttribute(Assets.LANGUAGE, "en")  ;
		}
		
		return true;
	}
	
	
	
}
