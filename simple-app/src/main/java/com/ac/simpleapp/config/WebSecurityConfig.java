package com.ac.simpleapp.config;

import com.ac.simpleapp.security.service.IUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SpringBootConfiguration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final IUserDetailsService iUserDetailsService;
    private final IAuthenticationSuccessHandler iAuthenticationSuccessHandler;
    private final IAuthenticationFailureHandler iAuthenticationFailureHandler;
    private final IAuthenticationEntryPoint iAuthenticationEntryPoint;
    private final ILogoutHandler iLogoutHandler;
    private final ILogoutSuccessHandler iLogoutSuccessHandler;

    @Autowired
    public WebSecurityConfig(IUserDetailsService iUserDetailsService, IAuthenticationSuccessHandler iAuthenticationSuccessHandler, IAuthenticationFailureHandler iAuthenticationFailureHandler, IAuthenticationEntryPoint iAuthenticationEntryPoint, ILogoutHandler iLogoutHandler, ILogoutSuccessHandler iLogoutSuccessHandler) {
        this.iUserDetailsService = iUserDetailsService;
        this.iAuthenticationSuccessHandler = iAuthenticationSuccessHandler;
        this.iAuthenticationFailureHandler = iAuthenticationFailureHandler;
        this.iAuthenticationEntryPoint = iAuthenticationEntryPoint;
        this.iLogoutHandler = iLogoutHandler;
        this.iLogoutSuccessHandler = iLogoutSuccessHandler;
    }

    @Bean
    public IOncePerRequestFilter oncePerRequestFilter(){ return new IOncePerRequestFilter(); }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    };

    //认证配置
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(iUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    //HTTP请求的安全配置
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().headers().cacheControl();

        http.formLogin()
                .loginProcessingUrl("/user/login").permitAll()
                .successHandler(this.iAuthenticationSuccessHandler).failureHandler(this.iAuthenticationFailureHandler)
                .and().authorizeRequests().anyRequest().authenticated();

        http.addFilterBefore(oncePerRequestFilter(), UsernamePasswordAuthenticationFilter.class);

        http.logout().addLogoutHandler(this.iLogoutHandler).logoutSuccessHandler(this.iLogoutSuccessHandler);

        http.exceptionHandling().authenticationEntryPoint(this.iAuthenticationEntryPoint);
    }
}
