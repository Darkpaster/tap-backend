package com.human.tapMMO.service.auth;

import com.human.tapMMO.dto.UserDTO;
import com.human.tapMMO.mapper.UserMapper;
import com.human.tapMMO.model.tables.Account;
import com.human.tapMMO.repository.AccountRepository;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.NoSuchElementException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccountRepository accountRepository;
    private final UserMapper userMapper;

//    private final String secretKey = Base64.getEncoder().encodeToString("my-secret-key".getBytes());

    public Optional<UserDTO> getUserById(long id) {
        return accountRepository.findById(id).map(userMapper::toDTO);
    }

    public UserDTO checkUser(String email, String password) throws AuthenticationException {
        Account account = getUserByEmail(email).orElseThrow(() -> new AuthenticationException("Could not find user by email: "+email));
        String userHash = account.getPassword();
        if (!password.equals(userHash)) {
//            return ResponseEntity.notFound().build();
            throw new AuthenticationException("Wrong password!");
        }
        System.out.printf("User %s signed in just now!", account.getEmail());
        return userMapper.toDTO(account); // это надо возвращать в виде JWT
    }

    public Optional<Account> getUserByEmail(String email) {
        return accountRepository.findUserByEmail(email);
    }

    public Optional<Account> getUserByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public long saveUser(UserDTO userDTO) {
        var user = userMapper.toEntity(userDTO);
        String hashedPassword = new BCryptPasswordEncoder().encode(userDTO.getPassword());
        user.setPassword(hashedPassword);
        accountRepository.save(user);
        return user.getId();
    }

    public void deleteUserById(long id) {
        accountRepository.delete(accountRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Could not find deleting user by entityId: "+id)));
    }

//    private String generateToken(long entityId) {
//        return Jwts.builder()
//                .setSubject(String.valueOf(entityId))
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
