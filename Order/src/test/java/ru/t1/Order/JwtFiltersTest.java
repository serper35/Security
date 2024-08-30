package ru.t1.Order;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.Authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import ru.t1.Order.filter.JwtAuthenticationFilter;
import ru.t1.Order.filter.JwtAuthorizationFilter;
import ru.t1.Order.util.JwtTokenUtil;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class JwtFiltersTest {

    @Mock
    private JwtTokenUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private AuthenticationManager authenticationManager;

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtUtil, userDetailsService);
        jwtAuthorizationFilter = new JwtAuthorizationFilter(userDetailsService, jwtUtil);
    }

    @Test
    public void testJwtAuthenticationFilterSuccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getParameter("username")).thenReturn("user");
        when(request.getParameter("password")).thenReturn("password");
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "password");
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(auth);

        jwtAuthenticationFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void testJwtAuthorizationFilterSuccess() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getHeader("Authorization")).thenReturn("Bearer token");
        when(jwtUtil.getUserName("token")).thenReturn("user");
        when(userDetailsService.loadUserByUsername("user")).thenReturn(mock(UserDetails.class));
        when(jwtUtil.validateToken("token", mock(UserDetails.class))).thenReturn(true);

        jwtAuthorizationFilter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }
}

