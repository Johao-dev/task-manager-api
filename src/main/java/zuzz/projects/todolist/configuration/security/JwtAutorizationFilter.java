package zuzz.projects.todolist.configuration.security;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import zuzz.projects.todolist.service.interfaces.JwtService;

public class JwtAutorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    private Logger logger = LoggerFactory.getLogger(JwtAutorizationFilter.class);

    public JwtAutorizationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response, FilterChain filterchain)
            throws ServletException, IOException {

        logger.info("JwtAuthorizationFilter doing filter");
        String header = request.getHeader("authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            logger.info("no header or not a bearer token");
            filterchain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            JWTClaimsSet claims = jwtService.parseJwt(token);
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(claims.getSubject(),
                    null,
                    Collections.emptyList()
                );

            SecurityContextHolder
                .getContext()
                .setAuthentication(authenticationToken);

        } catch (NoSuchAlgorithmException |
                InvalidKeySpecException |
                ParseException |
                JOSEException ex) {
            logger.error("error parsing token");
            ex.printStackTrace(System.out);
        }
        
        logger.info("filter done");
        filterchain.doFilter(request, response);
    }
}
