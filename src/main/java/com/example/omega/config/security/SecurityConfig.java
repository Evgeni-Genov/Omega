package com.example.omega.config.security;

import com.example.omega.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    private final UserDetailsServiceImpl userDetailsService;


//    private final AuthTokenFilter authTokenFilter;


    /**
     * Configures the security filter chain.
     *
     * @param http The HTTP security configuration object.
     * @return The configured security filter chain.
     * @throws Exception If an error occurs while configuring security.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .cors(withDefaults()) // Enable CORS with default settings.
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection.

                .authorizeHttpRequests(authorizeHttpRequests -> {
                    // Configure URL patterns and access control.
                    authorizeHttpRequests
                            .requestMatchers("/management/health").permitAll() // Permit unauthenticated access to /management/health.
                            .requestMatchers("/management/info").permitAll() // Permit unauthenticated access to /management/info.
                            .requestMatchers("/**").permitAll() // Permit unauthenticated access to other URLs.
                            .requestMatchers("/**").hasRole("USER"); // Requires "USER" role for other URLs.
                })

                .formLogin(withDefaults()) // Configure form-based login.
                .logout(logout -> logout.deleteCookies("remove")
                        .invalidateHttpSession(false)
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/logout-success"));

        http.authenticationProvider(authenticationProvider()); // Set authentication provider.

        return http.build();
    }

    /**
     * Configures custom security settings for the web.
     *
     * @return A custom security customizer.
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/js/**", "/images/**"); // Ignores certain URL patterns.
    }

    /**
     * Defines an in-memory user details service.
     *
     * @return An in-memory user details service.
     */
    //TODO: this will be removed after the project is set and the database is initialized
    @Bean
    public UserDetailsService userDetailsService() {
        var user = User.withUsername("user")
                .password(bCryptPasswordEncoder.encode("user"))
                .roles("USER")
                .build();

        var admin = User.withUsername("admin")
                .password(bCryptPasswordEncoder.encode("admin"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
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

    /**
     * Defines a JWT token filter for authentication.
     *
     * @return An AuthTokenFilter instance.
     */
//    @Bean
//    public AuthTokenFilter authenticationJwtTokenFilter() {
//        return new AuthTokenFilter();
//    }
}
