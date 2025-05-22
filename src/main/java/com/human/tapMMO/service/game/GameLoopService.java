package com.human.tapMMO.service.game;

import com.corundumstudio.socketio.SocketIOClient;
import com.human.tapMMO.dto.rest.ItemDTO;
import com.human.tapMMO.dto.websocket.ActorDTO;
import com.human.tapMMO.model.connection.InitCharacterConnection;
import com.human.tapMMO.dto.websocket.Position;
import com.human.tapMMO.model.tables.ItemPosition;
import com.human.tapMMO.model.tables.MobModel;
import com.human.tapMMO.runtime.game.actors.mob.Mob;
import com.human.tapMMO.runtime.game.actors.mob.MobServiceList;
import com.human.tapMMO.runtime.game.actors.player.Player;
import com.human.tapMMO.service.game.player.ItemService;
import com.human.tapMMO.service.game.world.MobService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Getter
@Service
@RequiredArgsConstructor
public class GameLoopService implements InitializingBean, DisposableBean {

    private final HashMap<Long, Mob> mobList = new HashMap<>();
    private final HashMap<Long, Player> playerList = new HashMap<>();
    private final HashMap<Long, ItemPosition> itemList = new HashMap<>();

    private final HashMap<UUID, Long> playerIdList = new HashMap<>();

    private Function<List<ActorDTO>, List<ActorDTO>> sendUpdatedMobs;

    private final MobService mobService;


    public void init(List<ActorDTO> initMobs, List<ItemPosition> initItems, Function<List<ActorDTO>, List<ActorDTO>> sendUpdatedMob, MobServiceList mobServiceList) {
        this.sendUpdatedMobs = sendUpdatedMob;
        for (ActorDTO mob : initMobs) {
//            System.out.println("MobName from db: "+mob.getName());
            addNewMob(mob, mobServiceList);
        }
        for (ItemPosition item : initItems) {
            this.itemList.put(item.getId(), item);
        }
        System.out.println("Amount of mobs in RAM: "+this.mobList.size());
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
        try {
            final Mob[] mobList = this.mobList.values().toArray(new Mob[0]);
            final ActorDTO[] dtoList = new ActorDTO[mobList.length];
            for (int i = 0; i < mobList.length; i++) {
                final var mob = mobList[i];
                mob.update(playerList);
                final ActorDTO mobDTO = new ActorDTO();
                mobDTO.setActorId(mob.getId());
                mobDTO.setX(mob.getX());
                mobDTO.setY(mob.getY());
                mobDTO.setHealth(mob.getHealth());
                mobDTO.setRenderState(mob.getRenderState());
                dtoList[i] = mobDTO;
            }
            if (sendUpdatedMobs != null) {
                sendUpdatedMobs.apply(List.of(dtoList));
            }
        } catch (Exception e) {
            System.err.println("Ошибка в async "+ e);
        }
    }

//    public void addNewPlayer(InitCharacterConnection player, SocketIOClient client) {
//        // запрос в бд на получение всей инфы
//        final var newPlayer = new Player(player.getNickname(), client.getSessionId());
//        newPlayer.setId(player.getCharacterId());
//        playerList.put(player.getCharacterId(), newPlayer);
//    }

    public void addNewPlayer(Player player) {
        if (player == null || playerList.containsKey(player.getId())) {
            System.out.println("fuck!");
            return;
        }
        playerList.put(player.getId(), player);
    }

    public void deletePlayer(long id) {
        playerList.remove(id);
    }

    public void addNewItem(ItemDTO itemDTO, long posId) {
        final var newItemPos = new ItemPosition();
        newItemPos.setItemId(itemDTO.getId());
        newItemPos.setId(posId);
        newItemPos.setX(itemDTO.getX());
        newItemPos.setY(itemDTO.getY());
        itemList.put(newItemPos.getItemId(), newItemPos);
    }

    public void deleteItem(long itemId) {
        itemList.remove(itemId);
    }

    public void deleteMob(long mobId) {
        mobList.remove(mobId);
    }

    public void addNewMob(MobModel mob, MobServiceList mobServiceList) {
        final var newMob = Mob.createMob(mob.getName());
        assert newMob != null;
        newMob.setX(mob.getX());
        newMob.setY(mob.getY());
        newMob.setId(mob.getId());
        newMob.setServiceList(mobServiceList);
        this.mobList.put(mob.getId(), newMob);
    }

    public void addNewMob(ActorDTO mob, MobServiceList mobServiceList) {
        final var newMob = Mob.createMob(mob.getName());
        assert newMob != null;
        newMob.setX(mob.getX());
        newMob.setY(mob.getY());
        newMob.setId(mob.getActorId());
        newMob.setServiceList(mobServiceList);
        this.mobList.put(mob.getActorId(), newMob);
    }

    public void dealDamageToMob(long entityId, int value) {
        final var target = mobList.get(entityId);
        assert target != null;
        target.takeDamage(value);
        if (!target.isAlive()) {
            deleteMob(entityId);
            mobService.die(entityId);
            System.out.println("Mob "+entityId+" has died.");
        }
    }

    public void updatePlayer(ActorDTO actorDTO) {
        var link = playerList.get(actorDTO.getActorId()); //на клиенте отправляется не DTO, надо фиксить там websocket
        link.setX(actorDTO.getX());
        link.setY(actorDTO.getY());
        link.setHealth(actorDTO.getHealth());
        link.setRenderState(actorDTO.getRenderState());
    }
}
