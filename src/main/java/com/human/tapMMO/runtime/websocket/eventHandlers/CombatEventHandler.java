package com.human.tapMMO.runtime.websocket.eventHandlers;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.human.tapMMO.dto.websocket.DamageDTO;
import com.human.tapMMO.service.game.GameLoopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class CombatEventHandler {

    private final GameLoopService gameLoopService;

    public void handleDealDamage(SocketIOClient client, DamageDTO data, AckRequest ackRequest) {
        try {
            log.info("Damage dealt to target: {} (type: {})",
                    data.getTarget().getTargetId(), data.getTarget().getTargetType());

            if (Objects.equals(data.getTarget().getTargetType(), "mob")) {
                handleDamageToMob(data);
            } else if (Objects.equals(data.getTarget().getTargetType(), "player")) {
                handleDamageToPlayer(data);
            } else {
                log.warn("Unknown target type: {}", data.getTarget().getTargetType());
            }

        } catch (Exception e) {
            log.error("Error handling deal damage event", e);
        }
    }

    private void handleDamageToMob(DamageDTO data) {
        final long mobId = data.getTarget().getTargetId();
        gameLoopService.dealDamageToMob(mobId, data.getValue());
        log.debug("Dealt {} damage to mob {}", data.getValue(), mobId);
    }

    private void handleDamageToPlayer(DamageDTO data) {
        // TODO: Implement player damage logic
        log.info("Player damage not implemented yet. Target: {}", data.getTarget().getTargetId());
    }
}