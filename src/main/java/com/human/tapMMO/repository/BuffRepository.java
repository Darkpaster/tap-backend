package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Buff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BuffRepository extends JpaRepository<Buff, Long> {
    List<Buff> findAllByCharacterId(long characterId);
}
