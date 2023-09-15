package com.cos.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터(SecurityConfig)가 스프링 필터체인에 등록됨
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable);
        http
                .authorizeHttpRequests(
                        authorize -> authorize
                                .requestMatchers("/user/**").authenticated()
                                .requestMatchers("/admin/**").hasRole("ROLE_ADMIN")
                                .anyRequest()
                                .permitAll()

                ).exceptionHandling((exceptionConfig) ->
                        exceptionConfig
                                .authenticationEntryPoint(unauthorizedEntryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                ) // 401 403 관련 예외처리
                .formLogin((formLogin) ->
                        formLogin
                                .loginPage("/login/login")
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .loginProcessingUrl("/login/login-proc")
                                .defaultSuccessUrl("/", true)
                )
                .logout((logoutConfig) ->
                        logoutConfig.logoutSuccessUrl("/")
                )
        return http.build();
    }
}

/*
    CSRF
    Cross site Request forgery로 사이트 간 위조 요청인데,
    공격자가 인증된 브라우저에 저장된 쿠키의 세션 정보를 활용하여 웹 서버에 사용자가 의도하지 않은 요청을 전달하는 것으로
    즉 정상적인 사용자가 의도치 않은 위조요청을 보내는 것을 의미한다.
    예를 들어 A라는 도메인에서, 인증된 사용자 H가 위조된 request를 포함한 link, email을 사용하였을 경우(클릭, 또는 사이트 방문만으로도)
    , A 도메인에서는 이 사용자가 일반 유저인지, 악용된 공격인지 구분할 수가 없다.
    CSRF protection은 spring security에서 default로 설정된다.
    즉, protection을 통해 GET요청을 제외한 상태를 변화시킬 수 있는 POST, PUT, DELETE 요청으로부터 보호한다.
    csrf protection을 적용하였을 때, html에서 다음과 같은 csrf 토큰이 포함되어야 요청을 받아들이게 됨으로써, 위조 요청을 방지하게 됩니다.
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    Rest api에서의 CSRF
    spring security documentation에 non-browser clients 만을 위한 서비스라면 csrf를 disable 하여도 좋다고 한다.
    이 이유는 rest api를 이용한 서버라면, session 기반 인증과는 다르게 stateless하기 때문에 서버에 인증정보를 보관하지 않는다.
    rest api에서 client는 권한이 필요한 요청을 하기 위해서는 요청에 필요한 인증 정보를(OAuth2, jwt토큰 등)을 포함시켜야 한다.
    따라서 서버에 인증정보를 저장하지 않기 때문에 굳이 불필요한 csrf 코드들을 작성할 필요가 없다.
    rest api에서는 csrf 공격으로부터 안전하고 매번 api 요청으로부터 csrf 토큰을 받지 않아도 되어 이 기능을 disable() 하는 것이 더 좋은 판단
    https://velog.io/@wonizizi99/SpringSpring-security-CSRF%EB%9E%80-disable
    https://covenant.tistory.com/279
    https://velog.io/@woosim34/Spring-Spring-Security-%EC%84%A4%EC%A0%95-%EB%B0%8F-%EA%B5%AC%ED%98%84SessionSpring-boot3.0-%EC%9D%B4%EC%83%81
 */