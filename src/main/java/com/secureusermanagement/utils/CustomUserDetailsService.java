package com.secureusermanagement.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.secureusermanagement.entity.User;
import com.secureusermanagement.repository.UserRepository;


//@Service
//public class CustomUserDetailsService implements UserDetailsService
//{
//    @Autowired
//    private UserRepository userRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String email)throws UsernameNotFoundException
//    {
//        User user = userRepository.findByEmail(email).orElseThrow(() ->new UsernameNotFoundException("User not found"));
//
//        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
//
//        return new CustomUserPrincipal(user.getUserId(),user.getEmail(),user.getPassword(),authorities);
//    }
//}

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)throws UsernameNotFoundException
    {
        User user = userRepository.findByEmail(email).orElseThrow(() ->new UsernameNotFoundException("User not found"));
        return buildPrincipal(user);
    }

    public UserDetails loadUserById(Long userId)
    {
        User user = userRepository.findById(userId).orElseThrow(() ->new UsernameNotFoundException("User not found"));
        return buildPrincipal(user);
    }

    private UserDetails buildPrincipal(User user)
    {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole().name()));
        return new CustomUserPrincipal(user.getUserId(),user.getEmail(),user.getPassword(),authorities);
    }
}