package com.human.tapMMO.runtime.game.talents;

import com.human.tapMMO.runtime.game.actors.player.Player;

public class WarriorStrength extends RuntimeTalent {
    public WarriorStrength() {
        super(1, "Warrior's Strength", "Увеличивает силу воина");
        setMaxTier(5);
        setRequiredLevel(5);
        setRequiredSkill("Combat");
        setRequiredSkillLevel(10);

        // +2 силы за каждый уровень таланта
        addStatModifier("strength", 2);

        setSpecialEffect(new TalentEffect() {
            @Override
            public void onActivate(Player player) {
                System.out.println(player.getName() + " изучил талант 'Сила Воина'!");
            }

            @Override
            public void onUpgrade(Player player, int newTier) {
                System.out.println(player.getName() + " улучшил 'Силу Воина' до уровня " + newTier);
            }
        });
    }
}
