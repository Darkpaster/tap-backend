package com.human.tapMMO.runtime.game.skills.passive;


public class BerserkerRage extends PassiveAbility {
    private int damageBonus;
    private double healthThreshold;

    public BerserkerRage() {
        super(1, "Berserker Rage", "Увеличивает урон при низком здоровье");
        this.damageBonus = 25;
        this.healthThreshold = 0.3; // 30% здоровья

        setRequiredLevel(10);
        setRequiredSkill("Combat");
        setRequiredSkillLevel(25);
    }

    @Override
    public boolean checkActivationConditions() {
        if (!super.checkActivationConditions()) return false;

        // Активируется только при низком здоровье
        double healthPercent = (double) getOwner().getHealth() / getOwner().getMaxHealth();
        return healthPercent <= healthThreshold;
    }

    @Override
    protected void onActivate() {
        // Добавляем бонус к урону
        addStatModifier("strength", damageBonus);
        System.out.println(getOwner().getName() + " вошел в берсерскую ярость!");
    }

    @Override
    protected void onDeactivate() {
        // Убираем бонус к урону
        getStatModifiers().remove("strength");
        System.out.println(getOwner().getName() + " вышел из берсерской ярости.");
    }
}