package com.human.tapMMO.service;

import com.human.tapMMO.dto.MobDTO;
import com.human.tapMMO.model.InitCharacterConnection;
import com.human.tapMMO.model.Position;
import com.human.tapMMO.model.tables.ItemPosition;
import com.human.tapMMO.model.tables.Mob;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Getter
@Service
public class GameLoopService implements InitializingBean, DisposableBean {

    private final HashMap<Long, MobDTO> mobList = new HashMap<>();
    private final HashMap<Long, Position> playerList = new HashMap<>();
    private final HashMap<Long, ItemPosition> itemList = new HashMap<>();

    private Function<List<MobDTO>, List<MobDTO>> sendUpdatedMob;


    public void init(List<Mob> initMobs, List<ItemPosition> initItems, Function<List<MobDTO>, List<MobDTO>> sendUpdatedMob) {
        this.sendUpdatedMob = sendUpdatedMob;
        for (Mob mob: initMobs) {
            var newMob = new MobDTO();
            newMob.setHealth(mob.getHealth());
            newMob.setX(mob.getX());
            newMob.setY(mob.getY());
            newMob.setId(mob.getId());
            mobList.put(mob.getId(), newMob);
        }
        for (ItemPosition item: initItems) {
            itemList.put(item.getId(), item);
        }
    }

    private ScheduledExecutorService scheduler;


    @Override
    public void afterPropertiesSet() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::update, 1000, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void destroy() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }

    private void update() {
        for (MobDTO mob: mobList.values()) {
            mob.update(playerList);
        }
        sendUpdatedMob.apply(new ArrayList<>(mobList.values()));
    }

    public void addNewPlayer(InitCharacterConnection player) {
        var newPosition = new Position();
        newPosition.setEntityId(player.getCharacterId());
        playerList.put(player.getCharacterId(), newPosition);
    }

    public void deletePlayer(long id) {
        playerList.remove(id);
    }

    public void addNewItem(ItemPosition item) {
        itemList.put(item.getItemId(), item);
    }

    public void deleteItem(long id) {
        itemList.remove(id);
    }

    public void updatePlayer(Position newData) {
        var link = playerList.get(newData.getEntityId());
        link.setX(newData.getX());
        link.setY(newData.getY());
    }
}
