package com.example.omega.config.security.method;

import com.example.omega.service.UserService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

/**
 * CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler
 * to create a custom security expression root that provides additional functionality
 * for evaluating security expressions.
 */
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    //TODO: delete?
    private ApplicationContext applicationContext;
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();

    /**
     * Creates and returns a custom security expression root for evaluating security expressions.
     *
     * @param authentication The authentication object associated with the current user.
     * @param invocation     The method invocation object.
     * @return A custom security expression root.
     */
    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        var root = new CustomMethodSecurityExpressionRoot(authentication);
        root.setTrustResolver(this.trustResolver);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setUserService(this.applicationContext.getBean(UserService.class));
        return root;
    }

    /**
     * Sets the Spring Application Context, which allows access to application beans.
     *
     * @param applicationContext The Spring Application Context.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        this.applicationContext = applicationContext;
    }
}
