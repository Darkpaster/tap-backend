package com.human.tapMMO.service.game;

import com.human.tapMMO.model.tables.Mob;
import com.human.tapMMO.repository.MobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MobService {
    private final MobRepository mobRepository;

    public void die(long mobId) {
        var mob = mobRepository.findById(mobId).orElseThrow(() -> new NoSuchElementException("mob dies"));
        mob.setState("dead");
        mob.setRespawnTime(Instant.now().plusSeconds(10));
        mobRepository.saveAndFlush(mob);
    }

    public void updateDB() {
        mobRepository.deleteAll();
        mobRepository.flush();
        for (int i = 0; i < 500; i++) {
            final Point coords = generateCoordinatesInZone();
            final var newMob = new Mob();
            newMob.setX((int) coords.getX());
            newMob.setY((int) coords.getY());
            mobRepository.saveAndFlush(newMob);
        }
    }

    public Point generateCoordinatesInZone() {
        double x = -5000 + Math.random() * (5000 + 5000);
        double y = -5000 + Math.random() * (5000 + 5000);
        return new Point((int) x, (int) y);
    }


    public List<Mob> initAllMobs() { //при инициализации мира
        return mobRepository.findAllByState("alive");
    }

}
