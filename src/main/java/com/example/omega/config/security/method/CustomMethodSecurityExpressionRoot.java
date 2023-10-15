package com.example.omega.config.security.method;

import com.example.omega.service.UserService;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot and implements MethodSecurityExpressionOperations
 * to provide custom security expression operations.
 */
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private UserService userService;
    private Object filterObject;
    private Object returnObject;
    private Object target;

    /**
     * Creates a new instance of CustomMethodSecurityExpressionRoot.
     *
     * @param authentication The Authentication object to use. Cannot be null.
     */
    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    /**
     * Checks if the authenticated user has access to the user details with the specified ID.
     *
     * @param id The ID of the user details to check.
     * @return True if the authenticated user can access the user details, otherwise false.
     */
    public boolean hasUser(Long id) {
        var user = this.userService.getUserById(id);
        return getAuthentication().getName().equals(user.getUsername());
    }

    /**
     * Sets the UserService to provide access to user-related operations.
     *
     * @param userService The UserService to set.
     */
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * Sets the filter object for security expression evaluation.
     *
     * @param filterObject The filter object to set.
     */
    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    /**
     * Gets the filter object for security expression evaluation.
     *
     * @return The filter object.
     */
    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    /**
     * Sets the return object for security expression evaluation.
     *
     * @param returnObject The return object to set.
     */
    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    /**
     * Gets the return object for security expression evaluation.
     *
     * @return The return object.
     */
    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    /**
     * Gets the target object for security expression evaluation.
     *
     * @return The target object.
     */
    @Override
    public Object getThis() {
        return target;
    }
}
