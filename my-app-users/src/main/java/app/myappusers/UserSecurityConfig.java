package app.myappusers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class UserSecurityConfig {

    private final Environment environment;
    private UserService userService;

    public UserSecurityConfig(Environment environment, UserService userService) {
        this.environment = environment;
        this.userService=userService;
    }

    @Bean
    BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        var authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
        var authenticationManager= authenticationManagerBuilder.build();

        // user filter
        var userFilter = new UserAuthFilter(authenticationManager, environment);
        userFilter.setFilterProcessesUrl("/users/login");

        httpSecurity
                .csrf((csrf) -> csrf.disable());

        httpSecurity
                .authorizeHttpRequests((auth) -> auth
                        //.requestMatchers(new AntPathRequestMatcher("/users/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/users")).access(
                                new WebExpressionAuthorizationManager(
                                        "hasIpAddress('" + environment.getProperty("my.app.gateway.ip.address") + "')"))
                        .requestMatchers(new AntPathRequestMatcher("/users/find/**")).access(
                                new WebExpressionAuthorizationManager(
                                        "hasIpAddress('" + environment.getProperty("my.app.gateway.ip.address") + "')"))
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity
                .addFilter(userFilter)
                .authenticationManager(authenticationManager);

        httpSecurity
                .headers((headers) -> headers
                        .frameOptions((frameOptions) -> frameOptions.sameOrigin()));

        return httpSecurity.build();

    }

}
