package com.human.tapMMO.runtime.game.quests.requirement;

import com.human.tapMMO.runtime.game.quests.PlayerContext;

class ItemRequirement implements QuestRequirement {
    private String itemId;
    private int quantity;

    public ItemRequirement(String itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    @Override
    public boolean isMet(PlayerContext playerContext) {
        return playerContext.hasItem(itemId, quantity);
    }

    @Override
    public String getDescription() {
        return "Требуется: " + itemId + " x" + quantity;
    }
}
