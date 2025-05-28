package com.human.tapMMO.service.game.player;

import com.human.tapMMO.runtime.game.actors.Actor;
import com.human.tapMMO.runtime.game.actors.player.Player;
import com.human.tapMMO.runtime.game.buffs.Buff;
import com.human.tapMMO.runtime.game.skills.passive.PassiveAbility;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EnhancedBuffService {
    // Активные баффы для каждого актера (для обратной совместимости)
    private final Map<Actor, List<Buff>> activeBuffs = new ConcurrentHashMap<>();

    /**
     * Применяет бафф к цели
     * @param buff бафф для применения
     * @param target цель
     */
    public void applyBuff(Buff buff, Actor target) {
        if (buff == null || target == null || !target.isAlive()) {
            log.warn("Попытка применить бафф к недопустимой цели");
            return;
        }

        // Проверяем, можно ли применить бафф
        if (!canApplyBuff(buff, target)) {
            log.debug("Не удалось применить бафф {} к {}", buff.getName(), target.getName());
            return;
        }

        // Получаем список баффов для цели
        List<Buff> targetBuffs = activeBuffs.computeIfAbsent(target, k -> new ArrayList<>());

        // Проверяем на стекование или замещение
        handleBuffStacking(buff, targetBuffs);

        // Применяем бафф
        buff.applyTo(target);
        targetBuffs.add(buff);

        // Для игроков добавляем бафф в их личный список и пересчитываем статы
        if (target instanceof Player player) {
            player.addBuff(buff);
        }

        log.info("Бафф {} применен к {}", buff.getName(), target.getName());
    }

    /**
     * Удаляет бафф с цели
     * @param buff бафф для удаления
     * @param target цель
     */
    public void removeBuff(Buff buff, Actor target) {
        if (buff == null || target == null) return;

        List<Buff> targetBuffs = activeBuffs.get(target);
        if (targetBuffs != null && targetBuffs.remove(buff)) {
            buff.remove();

            // Для игроков удаляем бафф из их личного списка и пересчитываем статы
            if (target instanceof Player player) {
                player.removeBuff(buff);
            }

            log.info("Бафф {} удален с {}", buff.getName(), target.getName());
        }
    }

    /**
     * Удаляет все баффы указанного типа с цели
     * @param buffClass класс баффа
     * @param target цель
     */
    public void removeBuffsByType(Class<? extends Buff> buffClass, Actor target) {
        List<Buff> targetBuffs = activeBuffs.get(target);
        if (targetBuffs == null) return;

        List<Buff> buffsToRemove = targetBuffs.stream()
                .filter(buffClass::isInstance)
                .collect(Collectors.toList());

        for (Buff buff : buffsToRemove) {
            removeBuff(buff, target);
        }
    }

    /**
     * Удаляет все баффы с цели
     * @param target цель
     */
    public void removeAllBuffs(Actor target) {
        List<Buff> targetBuffs = activeBuffs.get(target);
        if (targetBuffs == null) return;

        // Создаем копию списка для безопасного удаления
        List<Buff> buffsToRemove = new ArrayList<>(targetBuffs);
        for (Buff buff : buffsToRemove) {
            removeBuff(buff, target);
        }
    }

    /**
     * Удаляет все негативные баффы с цели
     * @param target цель
     */
    public void removeNegativeBuffs(Actor target) {
        List<Buff> targetBuffs = activeBuffs.get(target);
        if (targetBuffs == null) return;

        List<Buff> negativeBuffs = targetBuffs.stream()
                .filter(buff -> !buff.isPositive())
                .collect(Collectors.toList());

        for (Buff buff : negativeBuffs) {
            removeBuff(buff, target);
        }
    }

    /**
     * Проверяет, есть ли у цели бафф указанного типа
     * @param buffClass класс баффа
     * @param target цель
     * @return true если бафф есть
     */
    public boolean hasBuff(Class<? extends Buff> buffClass, Actor target) {
        List<Buff> targetBuffs = activeBuffs.get(target);
        if (targetBuffs == null) return false;

        return targetBuffs.stream().anyMatch(buffClass::isInstance);
    }

    /**
     * Получает все баффы цели
     * @param target цель
     * @return список баффов
     */
    public List<Buff> getBuffs(Actor target) {
        List<Buff> targetBuffs = activeBuffs.get(target);
        return targetBuffs != null ? new ArrayList<>(targetBuffs) : new ArrayList<>();
    }

    /**
     * Получает баффы определенного типа
     * @param buffClass класс баффа
     * @param target цель
     * @return список баффов указанного типа
     */
    @SuppressWarnings("unchecked")
    public <T extends Buff> List<T> getBuffsByType(Class<T> buffClass, Actor target) {
        List<Buff> targetBuffs = activeBuffs.get(target);
        if (targetBuffs == null) return new ArrayList<>();

        return targetBuffs.stream()
                .filter(buffClass::isInstance)
                .map(buff -> (T) buff)
                .collect(Collectors.toList());
    }

    /**
     * Обновляет все баффы (должен вызываться каждый игровой тик)
     * @param deltaTime прошедшее время в секундах
     */
    public void updateBuffs(float deltaTime) {
        for (Map.Entry<Actor, List<Buff>> entry : activeBuffs.entrySet()) {
            Actor target = entry.getKey();
            List<Buff> buffs = entry.getValue();

            // Создаем копию списка для безопасного удаления
            List<Buff> buffsToUpdate = new ArrayList<>(buffs);

            for (Buff buff : buffsToUpdate) {
                if (!buff.update(deltaTime)) {
                    // Бафф истек, удаляем его
                    buffs.remove(buff);

                    // Для игроков пересчитываем статы
                    if (target instanceof Player player) {
                        player.removeBuff(buff);
                    }

                    log.debug("Бафф {} истек у {}", buff.getName(), target.getName());
                }
            }

            // Если у актера не осталось баффов, удаляем его из карты
            if (buffs.isEmpty()) {
                activeBuffs.remove(target);
            }
        }
    }

    /**
     * Очищает все баффы при смерти актера
     * @param target цель
     */
    public void onActorDeath(Actor target) {
        List<Buff> targetBuffs = activeBuffs.remove(target);
        if (targetBuffs != null) {
            for (Buff buff : targetBuffs) {
                buff.remove();
            }
            log.info("Все баффы удалены с {} после смерти", target.getName());
        }
    }

    /**
     * Проверяет, можно ли применить бафф к цели
     */
    private boolean canApplyBuff(Buff buff, Actor target) {
        // Проверяем иммунитет к баффам (можно расширить)
        if (hasBuffImmunity(target, buff)) {
            return false;
        }

        // Проверяем максимальное количество баффов
        List<Buff> targetBuffs = activeBuffs.get(target);
        if (targetBuffs != null && targetBuffs.size() >= getMaxBuffCount(target)) {
            log.warn("Достигнуто максимальное количество баффов для {}", target.getName());
            return false;
        }

        return true;
    }

    /**
     * Обрабатывает стекование и замещение баффов
     */
    private void handleBuffStacking(Buff newBuff, List<Buff> existingBuffs) {
        // Ищем существующие баффы того же типа
        List<Buff> sameTypeBuffs = existingBuffs.stream()
                .filter(buff -> buff.getClass().equals(newBuff.getClass()))
                .collect(Collectors.toList());

        if (!sameTypeBuffs.isEmpty()) {
            // Определяем стратегию стекования
            BuffStackingStrategy strategy = getStackingStrategy(newBuff);

            switch (strategy) {
                case REPLACE -> {
                    // Заменяем старые баффы новым
                    for (Buff oldBuff : sameTypeBuffs) {
                        existingBuffs.remove(oldBuff);
                        oldBuff.remove();
                    }
                }
                case REFRESH -> {
                    // Обновляем время действия существующего баффа
                    if (!sameTypeBuffs.isEmpty()) {
                        Buff existingBuff = sameTypeBuffs.get(0);
                        existingBuff.setDuration(newBuff.getDuration());
                        return; // Не добавляем новый бафф
                    }
                }
                case STACK -> {
                    // Разрешаем стекование (ничего не делаем)
                }
                case REJECT -> {
                    // Отклоняем новый бафф если уже есть такой
                    return;
                }
            }
        }
    }

    /**
     * Получает стратегию стекования для баффа
     */
    private BuffStackingStrategy getStackingStrategy(Buff buff) {
        // По умолчанию заменяем баффы того же типа
        // Можно настроить для конкретных типов баффов
        if (buff.getName().contains("Poison") || buff.getName().contains("Яд")) {
            return BuffStackingStrategy.STACK;
        }

        return BuffStackingStrategy.REPLACE;
    }

    /**
     * Проверяет иммунитет к баффам
     */
    private boolean hasBuffImmunity(Actor target, Buff buff) {
        // Проверяем пассивные способности игрока на иммунитет
        if (target instanceof Player player) {
            for (PassiveAbility ability : player.getPassiveAbilities()) {
                if (ability.isActive() && ability instanceof BuffImmunityAbility immunityAbility) {
                    if (immunityAbility.isImmuneToBuffType(buff.getClass())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Получает максимальное количество баффов для актера
     */
    private int getMaxBuffCount(Actor target) {
        int baseMaxBuffs = 10; // Базовое количество

        if (target instanceof Player player) {
            // Можно увеличить лимит через таланты или способности
            for (PassiveAbility ability : player.getPassiveAbilities()) {
                if (ability.isActive() && ability instanceof BuffCapacityAbility capacityAbility) {
                    baseMaxBuffs += capacityAbility.getAdditionalBuffSlots();
                }
            }
        }

        return baseMaxBuffs;
    }

    /**
     * Стратегии стекования баффов
     */
    private enum BuffStackingStrategy {
        REPLACE,  // Заменить существующий бафф
        REFRESH,  // Обновить время действия существующего
        STACK,    // Разрешить стекование
        REJECT    // Отклонить новый бафф
    }

    /**
     * Интерфейс для способностей, дающих иммунитет к баффам
     */
    public interface BuffImmunityAbility {
        boolean isImmuneToBuffType(Class<? extends Buff> buffType);
    }

    /**
     * Интерфейс для способностей, увеличивающих лимит баффов
     */
    public interface BuffCapacityAbility {
        int getAdditionalBuffSlots();
    }

    /**
     * Получает статистику баффов для цели
     */
    public BuffStatistics getBuffStatistics(Actor target) {
        List<Buff> targetBuffs = activeBuffs.get(target);
        if (targetBuffs == null) {
            return new BuffStatistics(0, 0, 0);
        }

        long positiveCount = targetBuffs.stream().filter(Buff::isPositive).count();
        long negativeCount = targetBuffs.stream().filter(buff -> !buff.isPositive()).count();
        long permanentCount = targetBuffs.stream().filter(Buff::isPermanent).count();

        return new BuffStatistics((int) positiveCount, (int) negativeCount, (int) permanentCount);
    }

    /**
     * Класс для статистики баффов
     */
    @Getter
    public static class BuffStatistics {
        private final int positiveBuffs;
        private final int negativeBuffs;
        private final int permanentBuffs;

        public BuffStatistics(int positiveBuffs, int negativeBuffs, int permanentBuffs) {
            this.positiveBuffs = positiveBuffs;
            this.negativeBuffs = negativeBuffs;
            this.permanentBuffs = permanentBuffs;
        }

        public int getTotalBuffs() { return positiveBuffs + negativeBuffs; }
    }
}