package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Character;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CharacterRepository extends JpaRepository<Character, Long> {

    Optional<Character> findCharacterByName(String name);

    List<Character> findCharactersByAccountId(Long accountId);
}