package com.human.tapMMO.service.game.world;

import com.human.tapMMO.dto.websocket.ActorDTO;
import com.human.tapMMO.mapper.ActorMapper;
import com.human.tapMMO.model.tables.MobModel;
import com.human.tapMMO.repository.MobRepository;
import com.human.tapMMO.runtime.game.actors.mob.Mob;
import com.human.tapMMO.runtime.game.world.MapManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MobService {
    private final MobRepository mobRepository;
    private final ActorMapper actorMapper;

    public void die(long mobId) {
        var mob = mobRepository.findById(mobId).orElseThrow(() -> new NoSuchElementException("mob dies"));
        mob.setState("dead");
        mob.setRespawnTime(Instant.now().plusSeconds(10));
        mobRepository.saveAndFlush(mob);
    }

    public void updateDB(List<MapManager.ActorData> mobList) {
        mobRepository.deleteAll();
        mobRepository.flush();
        for (MapManager.ActorData mob: mobList) {
            final var newMob = new MobModel();
            newMob.setX(mob.x);
            newMob.setY(mob.y);
            newMob.setName(mob.name);
            newMob.setHealth(Mob.createMob(mob.name).getHealth());
            mobRepository.save(newMob);
        }
    }

//    public Point generateCoordinatesInZone() {
//        double x = -5000 + Math.random() * (5000 + 5000);
//        double y = -5000 + Math.random() * (5000 + 5000);
//        return new Point((int) x, (int) y);
//    }


    public List<ActorDTO> initAllMobs() { //при инициализации мира
        return actorMapper.toActorDTOFromModel(mobRepository.findAllByState("alive"));
    }

}
