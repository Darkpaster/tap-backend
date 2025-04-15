package com.human.tapMMO.repository;

import com.human.tapMMO.model.tables.Guild;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuildRepository extends JpaRepository<Guild, Long> {
    Optional<Guild> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT g FROM Guild g ORDER BY g.level DESC, g.experience DESC")
    Page<Guild> findTopGuildsByLevel(Pageable pageable);

    @Query("SELECT g FROM Guild g WHERE lower(g.name) LIKE lower(concat('%', :searchTerm, '%'))")
    List<Guild> searchGuildsByName(String searchTerm);
}
