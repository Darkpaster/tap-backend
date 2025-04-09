package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Mob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface MobRepository extends JpaRepository<Mob, Long> {

    List<Mob> findAllByStateAndRespawnTimeBefore(String state, Instant time);

    List<Mob> findAllByState(String state);
}
