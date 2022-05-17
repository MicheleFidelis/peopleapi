package com.dio.personapi.service;

import com.dio.personapi.repository.PersonUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonUserRepository personUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return Optional.ofNullable(personUserRepository.findByUsername(username))
                                    .orElseThrow(()-> new UsernameNotFoundException("Username not found"));
    }
}
