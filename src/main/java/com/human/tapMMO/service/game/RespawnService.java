package com.human.tapMMO.service.game;

import com.human.tapMMO.model.tables.Mob;
import com.human.tapMMO.repository.MobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class RespawnService {
    private final MobRepository mobRepository;

    private Function<Mob, Mob> respawnDTO;

    public void init(Function<Mob, Mob> func) {
        this.respawnDTO = func;
    }

    public Point generateCoordinatesInZone() {
        double x = -5000 + Math.random() * (5000 + 5000);
        double y = -5000 + Math.random() * (5000 + 5000);
        return new Point((int) x, (int) y);
    }

    @Scheduled(fixedRate = 500)
    public void checkAndRespawnMobs() {
        final List<Mob> mobsToRespawn = mobRepository.findAllByStateAndRespawnTimeBefore("dead", Instant.now());

        for(Mob mob: mobsToRespawn) {
            Point newCoords = generateCoordinatesInZone();
            mob.setX((int) newCoords.getX());
            mob.setY((int) newCoords.getY());
            mob.setState("alive");
            mobRepository.saveAndFlush(mob);
            respawnDTO.apply(mob);
        }
    }
}
