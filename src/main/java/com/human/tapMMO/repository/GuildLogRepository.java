package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.GuildLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuildLogRepository extends JpaRepository<GuildLog, Long> {
//    Page<GuildLog> findByGuildIdOrderByCreatedAtDesc(Long guildId, Pageable pageable);

}
