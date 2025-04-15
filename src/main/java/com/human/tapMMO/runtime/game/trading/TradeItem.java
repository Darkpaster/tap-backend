package com.human.tapMMO.runtime.game.trading;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TradeItem {
    private String itemId; // ID типа предмета
    private String instanceId; // Уникальный ID экземпляра предмета
    private int quantity; // Количество
    private Map<String, String> properties; // Свойства предмета (чары, качество и т.д.)

    public TradeItem(String itemId, String instanceId, int quantity) {
        this.itemId = itemId;
        this.instanceId = instanceId;
        this.quantity = quantity;
        this.properties = new HashMap<>();
    }

    // Геттеры и сеттеры
    public String getItemId() { return itemId; }
    public String getInstanceId() { return instanceId; }
    public int getQuantity() { return quantity; }
    public Map<String, String> getProperties() { return properties; }
    public void addProperty(String key, String value) { properties.put(key, value); }
}
