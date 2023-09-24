package com.example.omega.domain.enumeration;

/**
 * ROLE_ADMIN -  has all the authorities(only one)
 * ROLE_USER -   the basic user
 * ROLE_SYSTEM - can only see the transaction and the
 *               reason for eventual failure of the transaction
 */
public enum Roles {
    ROLE_ADMIN, ROLE_USER, ROLE_SYSTEM
}
