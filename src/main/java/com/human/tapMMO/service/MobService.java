package com.human.tapMMO.service;

import com.human.tapMMO.model.tables.Mob;
import com.human.tapMMO.repository.MobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class MobService {
    private final MobRepository mobRepository;


    public Mob die(long mobId) {
        var mob = mobRepository.findById(mobId).orElseThrow(() -> new NoSuchElementException("mob dies"));
        mobRepository.delete(mob);
        return mob;

//        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//
//        ScheduledFuture<?> future = scheduler.schedule(() -> {
//
//        }, 10, TimeUnit.SECONDS);

        // future.cancel(true); // можно отменить таймер

    }

    public void spawn(Mob mob) {
        mobRepository.save(mob);
    }


    public List<Mob> initAllMobs() { //при инициализации мира
        return mobRepository.findAll();
    }
}
