package com.human.tapMMO.service;

import com.human.tapMMO.dto.UserDTO;
import com.human.tapMMO.mapper.UserMapper;
import com.human.tapMMO.model.User;
import com.human.tapMMO.repository.UserRepository;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Base64;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

//    private final String secretKey = Base64.getEncoder().encodeToString("my-secret-key".getBytes());

    public Optional<UserDTO> getUserByID(long id) {
        return userRepository.findById(id).map(userMapper::toDTO);
    }

    public UserDTO checkUser(String email, String password) throws AuthenticationException {
        User user = getUserByEmail(email).orElseThrow(() -> new AuthenticationException("Could not find user by email: "+email));
        String userHash = user.getPassword();
        if (!password.equals(userHash)) {
//            return ResponseEntity.notFound().build();
            throw new AuthenticationException("Wrong password!");
        }
        System.out.printf("User %s signed in just now!", user.getEmail());
        return userMapper.toDTO(user); // это надо возвращать в виде JWT
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public long saveUser(UserDTO userDTO) {
        var user = userMapper.toEntity(userDTO);
        String hashedPassword = new BCryptPasswordEncoder().encode(userDTO.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return user.getId();
    }

    public void deleteUserByID(long id) {
        userRepository.delete(userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Could not find deleting user by id: "+id)));
    }

//    private String generateToken(long id) {
//        return Jwts.builder()
//                .setSubject(String.valueOf(id))
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 / 20)) // 3 m
//                .signWith(SignatureAlgorithm.HS256, secretKey)
//                .compact();
//    }
//
//    public Claims validateToken(String token) {
//        return Jwts.parser()
//                .setSigningKey(secretKey)
//                .parseClaimsJws(token)
//                .getBody();
//    }
}
