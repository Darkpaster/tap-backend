package com.human.tapMMO.controller.system;

import com.human.tapMMO.dto.rest.UserDTO;
import com.human.tapMMO.service.auth.AuthService;
import com.human.tapMMO.service.auth.CustomUserDetailsService;
import com.human.tapMMO.util.JWTTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.userdetails.UserDetailsService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController { // forbidden axios
    private final AuthService authService;
    @Autowired
    private JWTTokenUtil jwtTokenUtil;
    @Autowired
    private AuthenticationManager authenticationManager; //убрал autowired
    @Autowired
    private UserDetailsService userDetailService;


    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody UserDTO user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.saveUser(user));
    }


    @PostMapping("/login")
    public ResponseEntity<Long> createAuthenticationToken(@RequestBody UserDTO authenticationRequest, HttpServletResponse response) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final CustomUserDetailsService.CustomUserDetails userDetails = (CustomUserDetailsService.CustomUserDetails)
                userDetailService.loadUserByUsername(authenticationRequest.getUsername());


        final String token = jwtTokenUtil.generateToken(userDetails);

        // Создание HttpOnly cookie
        Cookie cookie = new Cookie("access_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // только HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60); // 60 m
        response.addCookie(cookie);

        return ResponseEntity.ok(userDetails.getId());
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("Ты в банане", e);
        } catch (BadCredentialsException e) {
            throw new Exception("Ты кто...", e);
        }
    }

}
