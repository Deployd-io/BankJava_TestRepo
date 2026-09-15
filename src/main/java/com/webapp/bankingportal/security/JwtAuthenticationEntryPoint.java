package com.webapp.bankingportal.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * This class implements the AuthenticationEntryPoint interface to handle
 * unauthorized access attempts. It is responsible for commencing the
 * authentication scheme and sending the appropriate response when an
 * unauthenticated user tries to access a secured resource.
 */
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * This method is called when an unauthenticated user tries to access a secured
     * resource. It sets the HTTP response status to 401 (Unauthorized) and sends an
     * error message.
     *
     * @param request       The HTTP request that resulted in an
     *                      AuthenticationException
     * @param response      The HTTP response
     * @param authException The AuthenticationException that was thrown when trying
     *                      to authenticate the user
     *
     * @throws IOException      If an input or output exception occurs
     * @throws ServletException If a servlet exception occurs
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
			log.info(">>> Entering commence(request={},response={},authException={})", request, response, authException);
			log.info("<<< Exiting commence(request={},response={},authException={})", request, response, authException);
			log.error("commence(request={},response={},authException={}): response → {}", request, response, authException, response);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println("Access Denied !! " + authException.getMessage());
    }

}
