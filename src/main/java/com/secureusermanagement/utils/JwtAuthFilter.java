package com.secureusermanagement.utils;


import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter 
{
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");
        
        // Check if token is present and starts with 'Bearer '
        if (authHeader != null && authHeader.startsWith("Bearer "))
        {
            String token = authHeader.substring(7);
            try 
            {
            	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                // Validate the token
                if (jwtUtils.isTokenValid(token)&& authentication == null)
                {
//                    String email = jwtUtils.extractSubject(token);
//                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                	
                	Long userId = jwtUtils.extractUserId(token);
                	UserDetails userDetails = userDetailsService.loadUserById(userId);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } 
            //If the token expires or token is black listed(Log out)
            catch (io.jsonwebtoken.ExpiredJwtException ex)
            {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Token is expired");
                return;
            } 
            catch (ResponseStatusException ex) 
            {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), ex.getReason());
                return;
            } 
            catch (Exception ex) 
            {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED.value(), "Invalid token");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    
    /*
     *  Helper method to send consistent JSON error responses
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException 
    {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String jsonResponse = String.format("{\"success\": false, \"statusCode\": %d, \"message\": \"%s\"}",statusCode, message);

        response.getWriter().write(jsonResponse);
    }
    
       
}



