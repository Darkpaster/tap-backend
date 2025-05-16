package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.MobModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface MobRepository extends JpaRepository<MobModel, Long> {

    List<MobModel> findAllByStateAndRespawnTimeBefore(String state, Instant time);

    List<MobModel> findAllByState(String state);
}
