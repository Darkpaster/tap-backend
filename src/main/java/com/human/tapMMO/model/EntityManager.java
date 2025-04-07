package com.human.tapMMO.model;

import com.human.tapMMO.dto.MobDTO;
import com.human.tapMMO.model.tables.ItemPosition;
import com.human.tapMMO.model.tables.Mob;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@Getter
public class EntityManager implements Runnable {
    private final HashMap<Long, MobDTO> mobList = new HashMap<>();
    private final HashMap<Long, Position> playerList = new HashMap<>();
    private final HashMap<Long, ItemPosition> itemList = new HashMap<>();

    private final Function<List<MobDTO>, List<MobDTO>> sendUpdatedMob;

    public EntityManager(List<Mob> initMobs, List<ItemPosition> initItems, Function<List<MobDTO>, List<MobDTO>> sendUpdatedMob) {
        this.sendUpdatedMob = sendUpdatedMob;
        for (Mob mob: initMobs) {
            var newMob = new MobDTO();
            newMob.setHealth(mob.getHealth());
            newMob.setX(mob.getX());
            newMob.setY(mob.getY());
            mobList.put(mob.getId(), newMob);
        }
        for (ItemPosition item: initItems) {
            itemList.put(item.getId(), item);
        }
    }

    public void addNewPlayer(InitCharacterConnection player) {
        var newPosition = new Position();
        newPosition.setEntityId(player.characterId);
        playerList.put(player.characterId, newPosition);
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

    private void update() throws InterruptedException {
        for (MobDTO mob: mobList.values()) {
            mob.update(playerList);
        }
        sendUpdatedMob.apply((List<MobDTO>) mobList.values());
    }

    private void startServerGameLoop() throws InterruptedException {
        while (true) {
            update();
            Thread.sleep(500);
        }
    }

    @SneakyThrows
    @Override
    public void run() {
        new Thread(this::startServerGameLoop).start();
    }
}
