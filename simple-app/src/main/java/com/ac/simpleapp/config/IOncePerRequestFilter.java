package com.ac.simpleapp.config;

import com.ac.simpleapp.security.service.IUserDetailsService;
import com.ac.simpleapp.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class IOncePerRequestFilter extends OncePerRequestFilter {

    private static final String HEADER = "Authorization";

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private IUserDetailsService iUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String headerToken = request.getHeader(HEADER);
        if (StringUtils.hasLength(headerToken)) {
            //postMan测试时，自动假如的前缀，要去掉。
            String token = headerToken.replace("Bearer ", "").trim();
            System.out.println("token = " + token);
            try {
                String username = this.jwtUtils.getUsernameForToken(token);
                UserDetails userDetails = this.iUserDetailsService.loadUserByUsername(username);
                boolean flag = this.jwtUtils.validateToken(token, userDetails);
                if (flag) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);
    }
}
