package com.human.tapMMO.service.auth;

import com.human.tapMMO.model.tables.Account;
import com.human.tapMMO.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository; // Ваш JPA репозиторий


    public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
        private final Long id;
        private final String email;

         public CustomUserDetails(String username, String password,
                                 Collection<? extends GrantedAuthority> authorities,
                                 Long id, String email) {
            super(username, password, authorities);
            this.id = id;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Здесь вы явно говоритеspring security где и как искать пользователя
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                account.getUsername(),
                account.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(account.getRole())),
                account.getId(),
                account.getEmail()
        );
    }
}