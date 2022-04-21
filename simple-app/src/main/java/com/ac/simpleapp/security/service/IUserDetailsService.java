package com.ac.simpleapp.security.service;

import com.ac.simpleapp.security.domain.AUser;
import com.ac.simpleapp.security.domain.UserRole;
import com.ac.simpleapp.security.mapper.IUserDetailsMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Service
public class IUserDetailsService implements UserDetailsService {

    private final IUserDetailsMapper mapper;

    @Autowired
    public IUserDetailsService(IUserDetailsMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<AUser> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(AUser::getUsername, username);
        AUser user = this.mapper.selectOne(lambdaQueryWrapper);
        Assert.notNull(user, "Not Found!!!");
        List<UserRole> userRoles = this.mapper.getRole();
        List<SimpleGrantedAuthority> sga = new ArrayList<>();
        userRoles.forEach(item->{
            sga.add(new SimpleGrantedAuthority(item.getId()));
        });
        return new User(user.getUsername(), new BCryptPasswordEncoder().encode(user.getPassword()), sga);
    }
}
