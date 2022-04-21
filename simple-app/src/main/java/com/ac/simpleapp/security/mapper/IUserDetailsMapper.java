package com.ac.simpleapp.security.mapper;

import com.ac.simpleapp.security.domain.AUser;
import com.ac.simpleapp.security.domain.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IUserDetailsMapper extends BaseMapper<AUser> {

    List<UserRole> getRole();
}
