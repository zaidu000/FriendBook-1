package com.friendbook.configuration;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class NoCacheFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse httpResp = (HttpServletResponse) response; 
		httpResp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		httpResp.setHeader("Pragma", "no-cache");
		httpResp.setHeader("Expires", "0");
		chain.doFilter(request, response);
	}
	
}
