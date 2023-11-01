package com.example.omega.config;

import com.example.omega.service.util.annotations.NormalizeString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class NormalizeStringResolver implements HandlerMethodArgumentResolver {

    /**
     * @param parameter the method parameter to check
     * @return true or false
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(NormalizeString.class) != null;
    }

    /**
     * @param parameter     the method parameter to resolve. This parameter must
     *                      have previously been passed to {@link #supportsParameter} which must
     *                      have returned {@code true}.
     * @param mavContainer  the ModelAndViewContainer for the current request
     * @param webRequest    the current request
     * @param binderFactory a factory for creating {@link WebDataBinder} instances
     * @return the parsed value or null if value is Blank
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        var parameterName = parameter.getParameterAnnotation(NormalizeString.class);

        if (parameterName != null) {
            var parameterValue = webRequest.getParameter(parameterName.value());

            var parsedValue = StringUtils.isNotBlank(parameterValue) ?
                parameterValue.trim() : parameterValue;

            if (StringUtils.isBlank(parsedValue)) {
                return null;
            }

            return parsedValue;
        }

        return null;
    }
}
