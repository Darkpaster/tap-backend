package com.human.tapMMO.runtime.game.trading;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Trade {
    // Геттеры и сеттеры
    private String id;
    private String initiatorId;
    private String targetId;
    private Map<String, List<TradeItem>> initiatorItems;
    private Map<String, List<TradeItem>> targetItems;
    private BigDecimal initiatorGold;
    private BigDecimal targetGold;
    private boolean initiatorAccepted;
    private boolean targetAccepted;
    private TradeStatus status;
    private LocalDateTime creationTime;
    private LocalDateTime lastUpdateTime;

    public Trade(String id, String initiatorId, String targetId) {
        this.id = id;
        this.initiatorId = initiatorId;
        this.targetId = targetId;
        this.initiatorItems = new HashMap<>();
        this.targetItems = new HashMap<>();
        this.initiatorGold = BigDecimal.ZERO;
        this.targetGold = BigDecimal.ZERO;
        this.initiatorAccepted = false;
        this.targetAccepted = false;
        this.status = TradeStatus.PENDING;
        this.creationTime = LocalDateTime.now();
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void setInitiatorGold(BigDecimal gold) {
        this.initiatorGold = gold;
        this.initiatorAccepted = false;
        this.targetAccepted = false;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void setTargetGold(BigDecimal gold) {
        this.targetGold = gold;
        this.initiatorAccepted = false;
        this.targetAccepted = false;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void setInitiatorAccepted(boolean accepted) {
        this.initiatorAccepted = accepted;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void setTargetAccepted(boolean accepted) {
        this.targetAccepted = accepted;
        this.lastUpdateTime = LocalDateTime.now();
    }

    public void setStatus(TradeStatus status) {
        this.status = status;
        this.lastUpdateTime = LocalDateTime.now();
    }

    // Добавление предмета инициатором обмена
    public void addInitiatorItem(TradeItem item) {
        if (!initiatorItems.containsKey(item.getItemId())) {
            initiatorItems.put(item.getItemId(), new ArrayList<>());
        }
        initiatorItems.get(item.getItemId()).add(item);

        // Сбрасываем статусы подтверждения
        this.initiatorAccepted = false;
        this.targetAccepted = false;
        this.lastUpdateTime = LocalDateTime.now();
    }

    // Добавление предмета целевым игроком
    public void addTargetItem(TradeItem item) {
        if (!targetItems.containsKey(item.getItemId())) {
            targetItems.put(item.getItemId(), new ArrayList<>());
        }
        targetItems.get(item.getItemId()).add(item);

        // Сбрасываем статусы подтверждения
        this.initiatorAccepted = false;
        this.targetAccepted = false;
        this.lastUpdateTime = LocalDateTime.now();
    }

    // Удаление предмета инициатором обмена
    public boolean removeInitiatorItem(String itemId, String instanceId) {
        if (initiatorItems.containsKey(itemId)) {
            List<TradeItem> items = initiatorItems.get(itemId);
            for (Iterator<TradeItem> iterator = items.iterator(); iterator.hasNext();) {
                TradeItem item = iterator.next();
                if (item.getInstanceId().equals(instanceId)) {
                    iterator.remove();

                    // Сбрасываем статусы подтверждения
                    this.initiatorAccepted = false;
                    this.targetAccepted = false;
                    this.lastUpdateTime = LocalDateTime.now();

                    return true;
                }
            }
        }
        return false;
    }

    // Удаление предмета целевым игроком
    public boolean removeTargetItem(String itemId, String instanceId) {
        if (targetItems.containsKey(itemId)) {
            List<TradeItem> items = targetItems.get(itemId);
            for (Iterator<TradeItem> iterator = items.iterator(); iterator.hasNext();) {
                TradeItem item = iterator.next();
                if (item.getInstanceId().equals(instanceId)) {
                    iterator.remove();

                    // Сбрасываем статусы подтверждения
                    this.initiatorAccepted = false;
                    this.targetAccepted = false;
                    this.lastUpdateTime = LocalDateTime.now();

                    return true;
                }
            }
        }
        return false;
    }

    // Проверка, готов ли обмен к завершению
    public boolean isReadyToComplete() {
        return initiatorAccepted && targetAccepted;
    }
}

// Статус обмена
public enum TradeStatus {
    PENDING, COMPLETED, CANCELLED, EXPIRED
}

