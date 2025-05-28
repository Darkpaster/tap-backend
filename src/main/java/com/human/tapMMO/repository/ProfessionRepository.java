package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Profession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfessionRepository extends JpaRepository<Profession, Long> {
    List<Profession> findByCharacterId(Long characterId);
    Optional<Profession> findByCharacterIdAndName(Long characterId, String name);

}
