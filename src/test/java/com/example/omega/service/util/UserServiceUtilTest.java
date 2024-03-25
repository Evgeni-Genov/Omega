package com.example.omega.service.util;

import com.example.omega.OmegaApplication;
import com.example.omega.domain.User;
import com.example.omega.repository.UserRepository;
import com.example.omega.service.exception.BadRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = OmegaApplication.class)
class UserServiceUtilTest {

    @Autowired
    private UserServiceUtil userServiceUtil;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userServiceUtil = new UserServiceUtil(userRepository);
    }

    @Test
    void testIsEmailInUse_WhenEmailIsNotInUse() {
        // Arrange
        var email = "newemail@example.com";
        Mockito.when(userRepository.existsByEmail(email)).thenReturn(false);

        // Act
        var result = userServiceUtil.existsByEmail(email);
        // Assert
        Assertions.assertFalse(result);
    }

    @Test
    void testIsEmailInUse_WhenEmailIsInUse() {
        // Arrange
        var email = "existingemail@example.com";
        Mockito.when(userRepository.existsByEmail(email)).thenReturn(true);

        // Act
        var result = userServiceUtil.existsByEmail(email);

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    void testIsUserNameInUse_WhenUserNameIsNotUse() {
        // Arrange
        var userName = "newuser";
        Mockito.when(userRepository.existsByUsername(userName)).thenReturn(false);

        // Act
        var result = userServiceUtil.existsByUsername(userName);

        // Assert
        Assertions.assertFalse(result);
    }

    @Test
    void testIsUserNameInUse_WhenUserNameIsInUse() {
        // Arrange
        var userName = "existinguser";

        Mockito.when(userRepository.existsByUsername(userName)).thenReturn(true);

        // Act
        var result = userServiceUtil.existsByUsername(userName);

        // Assert
        Assertions.assertTrue(result);
    }

    @Test
    void testValidateUserNotNull_WhenUserIsNotNull() {
        // Arrange
        var user = new User();

        // Act & Assert (no exception should be thrown)
        Assertions.assertDoesNotThrow(() -> userServiceUtil.validateUserNotNull(user));
    }

    @Test
    void testValidateUserNotNull_WhenUserIsNull() {
        var exception = Assertions.assertThrows(BadRequestException.class, () -> userServiceUtil.validateUserNotNull(null));
        Assertions.assertNotNull(exception);
    }

    @Test
    void testValidateUserNameAndPasswordNotEmpty_WhenUsernameAndPasswordAreNotEmpty() {
        // Arrange
        var user = new User();
        user.setUsername("username");
        user.setPassword("password");

        // Act & Assert (no exception should be thrown)
        Assertions.assertDoesNotThrow(() -> userServiceUtil.validateUsernameAndPasswordNotEmpty(user));
    }

    @Test
    void testValidateUserNameAndPasswordNotEmpty_WhenUsernameIsEmpty() {
        // Arrange
        var user = new User();
        user.setUsername("");
        user.setPassword("password");

        // Act & Assert (HttpBadRequestException should be thrown)
        var exception = Assertions.assertThrows(BadRequestException.class, () -> userServiceUtil.validateUsernameAndPasswordNotEmpty(user));
        Assertions.assertNotNull(exception);
    }

    @Test
    void testValidateUserNameAndPasswordNotEmpty_WhenPasswordIsEmpty() {
        var user = new User();
        user.setUsername("username");
        user.setPassword("");

        var exception = Assertions.assertThrows(BadRequestException.class, () -> userServiceUtil.validateUsernameAndPasswordNotEmpty(user));
        Assertions.assertNotNull(exception);
    }

}
