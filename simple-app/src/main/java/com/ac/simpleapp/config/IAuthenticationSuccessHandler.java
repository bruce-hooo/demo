package com.ac.simpleapp.config;

import com.ac.simpleapp.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class IAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(IAuthenticationSuccessHandler.class);

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(response.isCommitted()){
            log.info("Response has already been committed");
            return;
        }
        Map<String, Object> map = new HashMap<>(5);
        map.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put("flag", "success");
        User principal = (User) authentication.getPrincipal();
        JwtPair jwtPair = this.jwtUtils.generateToken(principal);
        map.put("access", jwtPair.getAccessToken());
        map.put("refresh", jwtPair.getRefreshToken());
        ResponseUtil.responseJsonWriter(response, map);
    }
}
