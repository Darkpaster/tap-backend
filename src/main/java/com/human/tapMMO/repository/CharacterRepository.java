package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Character;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CharacterRepository extends JpaRepository<Character, Long> {
}