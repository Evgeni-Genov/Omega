package com.example.omega.config.security;

import com.example.omega.config.security.jwt.AuthTokenFilter;
import com.example.omega.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserDetailsServiceImpl userDetailsService;

    private final AuthTokenFilter authTokenFilter;

    /**
     * Configures the security filter chain.
     *
     * @param http The HTTP security configuration object.
     * @return The configured security filter chain.
     * @throws Exception If an error occurs while configuring security.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
//        http
//                .cors(withDefaults()) // Enable CORS with default settings.
//                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection.
//                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests // Configure URL patterns and access control.
//                        .requestMatchers("/management/health").permitAll() // Permit unauthenticated access to /management/health.
//                        .requestMatchers("/auth/**").permitAll()
//                        .requestMatchers("/management/info").permitAll() // Permit unauthenticated access to /management/info.
//                        .requestMatchers("/api/**").authenticated() // Permit unauthenticated access to /management/info.
//                        .requestMatchers("/api/v1/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/v3/**").permitAll())
//                .sessionManagement((session) -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))//we don't store info about the user in the session, comes only from token
//                .authenticationProvider(authenticationProvider()).addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
//                .formLogin(withDefaults()) // Configure form-based login.
//                .logout(logout -> logout.deleteCookies("remove")
//                        .invalidateHttpSession(false)
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/logout-success"));
//
//        return http.build();
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests()
                .anyRequest().authenticated().and()
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authTokenFilter,UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * Configures custom security settings for the web.
     *
     * @return A custom security customizer.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web
                .ignoring()
                .requestMatchers(
                        "/js/**",
                        "/images/**",
                        "/v3/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/v2/api-docs/**",
                        "/swagger.json", "/swagger-ui.html", "/swagger-resources/**", "/webjars/**",
                        "/v3/api-docs/**");
    }

    /**
     * Configures the authentication provider with user details service and password encoder.
     *
     * @return A DaoAuthenticationProvider.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authProvider;
    }

    /**
     * Configures the authentication manager.
     *
     * @param authConfiguration Authentication configuration.
     * @return The configured authentication manager.
     * @throws Exception If an error occurs while configuring authentication manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

//    /**
//     * Defines a JWT token filter for authentication.
//     *
//     * @return An AuthTokenFilter instance.
//     */
//    @Bean
//    public AuthTokenFilter authenticationJwtTokenFilter() {
//        return new AuthTokenFilter();
//    }


}
