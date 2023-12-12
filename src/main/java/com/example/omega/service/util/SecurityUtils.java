package com.example.omega.service.util;

import com.example.omega.domain.enumeration.Roles;
import com.example.omega.service.UserService;
import com.example.omega.service.exception.HttpBadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class SecurityUtils {

    private final UserService userService;

    private static final String EMAIL_CLAIM = "unique_name";
    private static final String ROLES_CLAIM = "roles";
    private static final String GROUPS_CLAIM = "groups";
    private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";

    /**
     * Get the login of the current user.
     *
     * @return the login of the current user.
     */
    public static Optional<String> getCurrentUserLogin() {
        var securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    /**
     * Extract the principal (user login) from the provided authentication.
     *
     * @param authentication The authentication object.
     * @return The user login if available, otherwise null.
     */
    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof UserDetails) {
            var springSecurityUser = (UserDetails) authentication.getPrincipal();
            return springSecurityUser.getUsername();
        } else if (authentication instanceof JwtAuthenticationToken) {
            return (String) ((JwtAuthenticationToken) authentication).getToken().getClaims().get(PREFERRED_USERNAME_CLAIM);
        } else if (authentication.getPrincipal() instanceof DefaultOidcUser) {
            var attributes = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes();
            if (attributes.containsKey(PREFERRED_USERNAME_CLAIM)) {
                return (String) attributes.get(PREFERRED_USERNAME_CLAIM);
            }
        } else if (authentication.getPrincipal() instanceof String) {
            return (String) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Check if the current user is authenticated.
     *
     * @return True if authenticated, false otherwise.
     */
    public static boolean isAuthenticated() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                getAuthorities(authentication).noneMatch(Roles.ROLE_ANONYMOUS::equals);
    }

    /**
     * Get the authorities (roles) associated with the provided authentication.
     *
     * @param authentication The authentication object.
     * @return Stream of authority names.
     */
    private static Stream<String> getAuthorities(Authentication authentication) {
        var authorities = authentication instanceof JwtAuthenticationToken ?
                extractAuthorityFromClaims(((JwtAuthenticationToken) authentication).getToken().getClaims())
                : authentication.getAuthorities();
        return authorities.stream()
                .map(GrantedAuthority::getAuthority);
    }

    /**
     * Extract authorities (roles) from the provided claims.
     *
     * @param claims The claims containing roles information.
     * @return List of GrantedAuthority.
     */
    public static List<GrantedAuthority> extractAuthorityFromClaims(Map<String, Object> claims) {
        return mapRolesToGrantedAuthorities(getRolesFromClaims(claims));
    }

    /**
     * Extracts a collection of roles from the provided claims.
     *
     * @param claims The claims containing role information.
     * @return A collection of role names.
     */
    private static Collection<String> getRolesFromClaims(Map<String, Object> claims) {
        return (Collection<String>) claims.getOrDefault(ROLES_CLAIM,
                claims.getOrDefault(GROUPS_CLAIM, new ArrayList<>()));
    }

    /**
     * Maps a collection of role names to a list of {@link GrantedAuthority} instances.
     * Only roles starting with the "ROLE_" prefix are included.
     *
     * @param roles A collection of role names.
     * @return A list of {@link GrantedAuthority} instances representing the roles.
     */
    private static List<GrantedAuthority> mapRolesToGrantedAuthorities(Collection<String> roles) {
        return roles.stream()
                .filter(role -> role.startsWith("ROLE_"))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    /**
     * If the current user has a specific authority (security role).
     * The name of this method comes from the {@code isUserInRole()} method in the Servlet API.
     *
     * @param authority the authority to check.
     * @return true if the current user has the authority, false otherwise.
     */
    public static boolean isCurrentUserInRole(String authority) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                getAuthorities(authentication)
                        .anyMatch(authority::equals);
    }

    public void canCurrentUserEditThisData(Principal principal, Long userId) {
        var currentUserId = extractCurrentUserIdFromPrincipal(principal);
        boolean isUserAuthenticated = userId.equals(currentUserId) || isCurrentUserInRole(Roles.ROLE_ADMIN.name());
        if (!isUserAuthenticated) {
            throw new HttpBadRequestException("You do not have permission to update this Entity!");
        }
    }

    public Long extractCurrentUserIdFromPrincipal(Principal principal) {
        var currentUserLogin = principal.getName();
        var currentUser = userService.getUserWithAuthoritiesByLogin(currentUserLogin);
        if (currentUser.isEmpty()) {
            throw new HttpBadRequestException("No user is present!");
        }
        return currentUser.get().getId();
    }
}