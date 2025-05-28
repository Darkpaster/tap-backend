package com.human.tapMMO.runtime.websocket.service;

import com.human.tapMMO.dto.websocket.ActorDTO;
import com.human.tapMMO.runtime.game.actors.mob.MobServiceList;
import com.human.tapMMO.runtime.game.actors.player.Player;
import com.human.tapMMO.runtime.game.world.MapManager;
import com.human.tapMMO.service.game.GameLoopService;
import com.human.tapMMO.service.game.player.ItemService;
import com.human.tapMMO.service.game.world.MobService;
import com.human.tapMMO.service.game.world.RespawnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventService {

    private final GameLoopService gameLoopService;
    private final MobService mobService;
    private final ItemService itemService;
    private final MapManager mapManager;
    private final RespawnService respawnService;
    private final WebSocketBroadcastService broadcastService;

    public void initializeGameServices() {
        mapManager.init().thenRun(() -> {
            if (mapManager.actorList.isEmpty()) {
                log.warn("Actor list is empty after map initialization");
                return;
            }

            mobService.updateDB(mapManager.actorList);

            MobServiceList mobServiceList = createMobServiceList();

            gameLoopService.init(
                    mobService.initAllMobs(),
                    itemService.initAllItems(),
                    sendUpdatedMobs(),
                    mobServiceList
            );

            initializeRespawnService(mobServiceList);
        });
    }

    private void initializeRespawnService(MobServiceList mobServiceList) {
        respawnService.init(mob -> {
            gameLoopService.addNewMob(mob, mobServiceList);
            return mob;
        });
    }

    private MobServiceList createMobServiceList() {
        return new MobServiceList(
                sendDamageFromMob(),
                sendHealingFromMob(),
                sendBuffFromMob(),
                sendNewQuestFromMob(),
                sendUpdatedQuestFromMob(),
                sendCompletedQuestFromMob()
        );
    }

    @Bean
    public Function<List<ActorDTO>, List<ActorDTO>> sendUpdatedMobs() {
        return broadcastService::broadcastMobUpdates;
    }

    @Bean
    public Function<Player, Player> sendDamageFromMob() {
        return broadcastService::sendPlayerHealthUpdate;
    }

    @Bean
    public Function<Player, Player> sendHealingFromMob() {
        return broadcastService::broadcastPlayerUpdate;
    }

    @Bean
    public Function<Player, Player> sendBuffFromMob() {
        return broadcastService::broadcastPlayerUpdate;
    }

    @Bean
    public Function<Player, Player> sendNewQuestFromMob() {
        return broadcastService::broadcastPlayerUpdate;
    }

    @Bean
    public Function<Player, Player> sendUpdatedQuestFromMob() {
        return broadcastService::broadcastPlayerUpdate;
    }

    @Bean
    public Function<Player, Player> sendCompletedQuestFromMob() {
        return broadcastService::broadcastPlayerUpdate;
    }
}