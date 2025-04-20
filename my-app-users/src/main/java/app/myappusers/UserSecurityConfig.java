package app.myappusers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class UserSecurityConfig {

    private final Environment environment;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserSecurityConfig(Environment environment, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.environment = environment;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        var authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
        var authenticationManager = authenticationManagerBuilder.build();

        var userFilter = new UserAuthFilter(authenticationManager, environment);
        userFilter.setFilterProcessesUrl("/users/login");

        httpSecurity
                .csrf(AbstractHttpConfigurer::disable);

        httpSecurity
                .authorizeHttpRequests((auth) -> auth
                        //                       .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/users")
                        .access(
                                new WebExpressionAuthorizationManager(
                                        "hasIpAddress('" + this.environment.getProperty("my.app.gateway.ip.address") + "')"))
                        .requestMatchers("/users/status/check")
                        .access(
                                new WebExpressionAuthorizationManager(
                                        "hasIpAddress('" + this.environment.getProperty("my.app.gateway.ip.address") + "')"))

//                        .anyRequest().authenticated()
                )
//                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll())

                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity
                .addFilter(userFilter)
                .authenticationManager(authenticationManager);

        httpSecurity
                .headers((headers) -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return httpSecurity.build();

    }

}
