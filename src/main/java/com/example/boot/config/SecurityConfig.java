package com.example.boot.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity  //
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    DataSource dataSource;

    // 根据用户名 找出 用户信息（username password enabled）
    // 根据用户名找出 该用户 所用的所有权限
    @Autowired  // why?
    public void configAuthentication(AuthenticationManagerBuilder authBuilder) throws Exception {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        authBuilder.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select username, password, enabled from users where username = ?")
                .authoritiesByUsernameQuery("select username, authority from authorities where username = ?")
                .passwordEncoder(encoder);
    }

    public void configure(HttpSecurity httpSecurity) throws Exception {
        // httpBasic是由http协议定义的最基础的认证方式
        // 每次请求时，在请求头Authorization参数中附带用户/密码的base64编码
        // 不安全
        // use HTTP Basic username/password authentication for this application.
        // This is done by calling the httpBasic() method
        httpSecurity.httpBasic();

        // Access rules are applied at the endpoint level.
        httpSecurity.authorizeRequests()
                // First we match /loggedin and specify that you must be logged in (authenticated) to access the endpoint.
                .mvcMatchers("/loggedin").authenticated()
                // Next we specify that GET requests to /needsRole are only accessible to users with the ROLE_MANAGER authority.
                // This implies that the user is also logged in because you can't have an authority if you're not logged in.
                .mvcMatchers(HttpMethod.GET, "/needsRole").hasAuthority("ROLE_MANAGER")
                // Next we specify that POST requests to /needsRole are only accessible to users with the ROLE_ADMIN authority.
                .mvcMatchers(HttpMethod.POST, "/needsRole").hasAuthority("ROLE_ADMIN")
                // Finally, we specify that we'll permit anyone (logged in or not) to access all other endpoints.
                .anyRequest().permitAll();

        httpSecurity
                .logout()
                .clearAuthentication(true)
                // The URL that is to be called for logging out.
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                // The URL that the application will redirect to when logout is complete.
                .logoutSuccessUrl("/allDone")
                // The cookies to delete
                .deleteCookies("JSESSIONID")
                .deleteCookies("XSRF-TOKEN")
                .invalidateHttpSession(true);
        // Configure CSRF Protection
        httpSecurity

                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }
}
