package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Account, Long> {

    Optional<Account> findUserByEmail(String email);

    Optional<Account> findByUsername(String username);
}

