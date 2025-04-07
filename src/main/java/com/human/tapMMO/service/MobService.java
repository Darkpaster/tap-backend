package com.human.tapMMO.service;

import com.human.tapMMO.model.tables.Mob;
import com.human.tapMMO.repository.MobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class MobService {
    private final MobRepository mobRepository;


    public void die(long mobId, Runnable sendAliveState) {
        var mob = mobRepository.findById(mobId).orElseThrow(() -> new NoSuchElementException("mob dies"));
        mob.setAlive(false);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        ScheduledFuture<?> future = scheduler.schedule(() -> {
            spawn(mob); // хранить в озу всех игроков на серве, в гейм лупе на сервере проверять расстояние между мобов и игроками
            // в гейм лупе рассылать раз в 20 мс всем игрокам новые координаты мобов (и хп)
            // на клиенте срабатывает атака по мобу или моба и отправляется событие на рассылку новых данных
            sendAliveState.run();
        }, mob.getRespawnTime(), TimeUnit.SECONDS);

        // future.cancel(true); // можно отменить таймер

    }

    private void spawn(Mob mob) {
        mob.setAlive(true);
    }

    public void updateDB() {
        mobRepository.deleteAll();
        int[][] coords = generateCoordinates(100, -5000, 5000, 100, 500);
        for (int[] coor: coords) {
            final var newMob = new Mob();
            newMob.setX(coor[0]);
            newMob.setY(coor[1]);
            newMob.setHealth(50);
            mobRepository.save(newMob);
        }
        mobRepository.flush();
    }


    public List<Mob> initAllMobs() { //при инициализации мира
        return mobRepository.findAll();
    }


    public static int[][] generateCoordinates(int count, int min, int max, double minDist, double maxDist) {
        Random rnd = new Random();
        int[][] coords = new int[count][2];

        // Первая точка — случайно в заданном диапазоне
        coords[0][0] = rnd.nextInt(max - min + 1) + min;
        coords[0][1] = rnd.nextInt(max - min + 1) + min;

        for (int i = 1; i < count; i++) {
            int x, y;
            // Генерируем до тех пор, пока не получим точку, удовлетворяющую условиям
            do {
                double angle = rnd.nextDouble() * 2 * Math.PI;
                double dist = minDist + rnd.nextDouble() * (maxDist - minDist);
                x = coords[i - 1][0] + (int) Math.round(dist * Math.cos(angle));
                y = coords[i - 1][1] + (int) Math.round(dist * Math.sin(angle));
                // проверяем, что попали в допустимый квадрат
            } while (x < min || x > max || y < min || y > max);

            coords[i][0] = x;
            coords[i][1] = y;
        }

        return coords;
    }
}
