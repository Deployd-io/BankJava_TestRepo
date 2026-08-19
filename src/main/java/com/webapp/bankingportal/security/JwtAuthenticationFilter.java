package com.webapp.bankingportal.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.webapp.bankingportal.exception.InvalidTokenException;
import com.webapp.bankingportal.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT Authentication Filter
 * 
 * This filter intercepts incoming requests to authenticate users based on JWT
 * tokens. It extends OncePerRequestFilter to ensure it's executed once per
 * request.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    /**
     * Performs the filtering for each request
     *
     * @param request     The HTTP request
     * @param response    The HTTP response
     * @param filterChain The filter chain
     *
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
			log.debug(">>> Entering doFilterInternal(request={},response={},filterChain={})", request, response, filterChain);

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.info("User is already authenticated");

            filterChain.doFilter(request, response);
            return;
        }

        val requestTokenHeader = request.getHeader("Authorization");
		log.debug("doFilterInternal(request={},response={},filterChain={}): requestTokenHeader → {}", request, response, filterChain, requestTokenHeader);

        if (requestTokenHeader == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!requestTokenHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    "Token must start with 'Bearer '");

            return;
        }

        val token = requestTokenHeader.substring(7);
		log.warn("doFilterInternal(request={},response={},filterChain={}): token → {}", request, response, filterChain, token);
        String username = null;
		log.debug("doFilterInternal(request={},response={},filterChain={}): username → {}", request, response, filterChain, username);

        try {
            tokenService.validateToken(token);
            username = tokenService.getUsernameFromToken(token);

        } catch (InvalidTokenException e) {
			log.error("Exception in doFilterInternal(request={},response={},filterChain={}): {}", request, response, filterChain, e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                    e.getMessage());
            return;
        }

        val userDetails = userDetailsService.loadUserByUsername(username);
		log.debug("doFilterInternal(request={},response={},filterChain={}): userDetails → {}", request, response, filterChain, userDetails);
        val authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		log.debug("doFilterInternal(request={},response={},filterChain={}): authToken → {}", request, response, filterChain, authToken);
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
		log.debug("<<< Exiting doFilterInternal(request={},response={},filterChain={})", request, response, filterChain);
    }

}
