package com.dio.personapi.config;

import com.dio.personapi.service.PersonUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PersonUserDetailsService personUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
//                csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .and()
                .httpBasic();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        log.info("Password encoded {} ", passwordEncoder.encode("projectSpringBoot"));

        auth.inMemoryAuthentication()
                .withUser("Michele2")
                .password(passwordEncoder.encode("projectSpringBoot"))
                .roles("USER", "ADMIN")
                .and()
                .withUser("Visit2")
                .password(passwordEncoder.encode("projectSpringBoot"))
                .roles("USER");

        auth.userDetailsService(personUserDetailsService)
                .passwordEncoder(passwordEncoder);
    }
}
