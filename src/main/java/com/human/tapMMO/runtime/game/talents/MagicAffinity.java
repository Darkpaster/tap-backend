package com.human.tapMMO.runtime.game.talents;

public class MagicAffinity extends RuntimeTalent {
    public MagicAffinity() {
        super(2, "Magic Affinity", "Увеличивает магическую силу и ману");
        setMaxTier(3);
        setRequiredLevel(8);
        setRequiredSkill("Magic");
        setRequiredSkillLevel(15);

        // +3 интеллекта и +5% к максимальной мане за уровень
        addStatModifier("intelligence", 3);
        addPercentageModifier("maxMana", 5.0);
    }
}