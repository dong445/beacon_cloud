package com.dong.config;

import com.dong.exception.AccessDeniedHandlerImpl;
import com.dong.exception.AuthenticationEntryPointImpl;
import com.dong.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    @Autowired
    private AccessDeniedHandlerImpl accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                // 允许同源 iframe：后台是 AdminLTE 布局，功能页(sys/*.html)通过 iframe 展示。
                // Spring Security 默认 X-Frame-Options: DENY 会把 iframe 全挡住，改成 SAMEORIGIN 放行同源、仍防点击劫持
                .headers().frameOptions().sameOrigin().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 放行登录和验证码
                .antMatchers("/sys/login", "/captchaImage").permitAll()
                // 放行静态资源：入口页 + 静态目录 + 常见静态文件类型
                .antMatchers("/", "/login.html", "/index.html", "/favicon.ico").permitAll()
                .antMatchers("/public/**", "/json/**").permitAll()
                .antMatchers("/**/*.html", "/**/*.js", "/**/*.css", "/**/*.png", "/**/*.jpg",
                        "/**/*.jpeg", "/**/*.gif", "/**/*.ico", "/**/*.woff", "/**/*.woff2",
                        "/**/*.ttf", "/**/*.eot", "/**/*.svg", "/**/*.map").permitAll()
                // 其它系统接口需要登录
                .antMatchers("/sys/**").authenticated()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
