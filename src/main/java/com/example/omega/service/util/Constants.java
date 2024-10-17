package com.example.omega.service.util;

import org.springframework.http.HttpMethod;

public class Constants {

    /**
     * GlobalExceptionHandler
     */
    public static final String DATE_FORMATTER = "dd-MM-yyyy HH:mm:ss";

    /**
     * Scheduled Tasks
     */
    public static final String EVERY_SUNDAY_1_AM = "0 0 1 ? * SUN";

    public static final String EVERY_TWO_MINUTES = "0 */2 * ? * *";

    public static final String EVERY_25_SECONDS = "*/30 * * * * *";

    public static final String EVERY_DAY_3_AM = "0 0 3 * * ?";

    /**
     * Path for storing avatars
     */
    public static final String USER_PROFILE_DIR = "src/main/resources/userProfiles/";

    /**
     * WebMvcConfig constants
     */
    public static final String AUTH = "/auth/**";
    public static final String API = "/api/**";

    public static final String[] ALLOWED_METHODS = {
            HttpMethod.OPTIONS.name(),
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.PATCH.name()
    };
}
