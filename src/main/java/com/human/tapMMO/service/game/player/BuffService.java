package com.human.tapMMO.service.game.player;

import com.human.tapMMO.runtime.game.actors.Actor;
import com.human.tapMMO.runtime.game.buffs.Buff;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class BuffService {
    // Активные баффы для каждого актера
    private final Map<Actor, List<Buff>> activeBuffs = new ConcurrentHashMap<>();

    /**
     * Применяет бафф к цели
     * @param buff бафф для применения
     * @param target цель
     */
    public void applyBuff(Buff buff, Actor target) {
        // Получение списка активных баффов цели
        List<Buff> buffs = activeBuffs.computeIfAbsent(target, k -> new ArrayList<>());

        // Проверка, есть ли уже такой бафф
        for (Buff existingBuff : new ArrayList<>(buffs)) {
            if (existingBuff.getClass().equals(buff.getClass())) {
                // Если такой бафф уже есть, удаляем его
                removeBuff(existingBuff, target);
                break;
            }
        }

        // Применение баффа
        buff.applyTo(target);
        buffs.add(buff);
    }

    /**
     * Удаляет бафф с цели
     * @param buff бафф для удаления
     * @param target цель
     */
    public void removeBuff(Buff buff, Actor target) {
        List<Buff> buffs = activeBuffs.get(target);
        if (buffs != null) {
            buff.remove();
            buffs.remove(buff);
        }
    }

    /**
     * Обновляет все активные баффы
     * @param deltaTime прошедшее время в секундах
     */
    public void updateBuffs(float deltaTime) {
        // Обновление всех баффов для всех акторов
        for (Map.Entry<Actor, List<Buff>> entry : activeBuffs.entrySet()) {
            Actor actor = entry.getKey();
            List<Buff> buffs = entry.getValue();

            // Обновление каждого баффа
            for (Buff buff : new ArrayList<>(buffs)) {
                boolean isActive = buff.update(deltaTime);
                if (!isActive) {
                    buffs.remove(buff);
                }
            }

            // Если у актора больше нет баффов, удаляем его из мапы
            if (buffs.isEmpty()) {
                activeBuffs.remove(actor);
            }
        }
    }

    /**
     * Получает все активные баффы актора
     * @param actor актор
     * @return список активных баффов
     */
    public List<Buff> getActiveBuffs(Actor actor) {
        return activeBuffs.getOrDefault(actor, new ArrayList<>());
    }

    /**
     * Получает все позитивные баффы актора
     * @param actor актор
     * @return список позитивных баффов
     */
    public List<Buff> getPositiveBuffs(Actor actor) {
        List<Buff> result = new ArrayList<>();
        List<Buff> buffs = activeBuffs.get(actor);

        if (buffs != null) {
            for (Buff buff : buffs) {
                if (buff.isPositive()) {
                    result.add(buff);
                }
            }
        }

        return result;
    }

    /**
     * Получает все негативные баффы (дебаффы) актора
     * @param actor актор
     * @return список негативных баффов
     */
    public List<Buff> getNegativeBuffs(Actor actor) {
        List<Buff> result = new ArrayList<>();
        List<Buff> buffs = activeBuffs.get(actor);

        if (buffs != null) {
            for (Buff buff : buffs) {
                if (!buff.isPositive()) {
                    result.add(buff);
                }
            }
        }

        return result;
    }

    /**
     * Удаляет все баффы актора
     * @param actor актор
     */
    public void removeAllBuffs(Actor actor) {
        List<Buff> buffs = activeBuffs.get(actor);
        if (buffs != null) {
            for (Buff buff : new ArrayList<>(buffs)) {
                buff.remove();
            }
            activeBuffs.remove(actor);
        }
    }

    /**
     * Удаляет все негативные баффы актора (очищение)
     * @param actor актор
     */
    public void dispelNegativeBuffs(Actor actor) {
        List<Buff> buffs = activeBuffs.get(actor);
        if (buffs != null) {
            for (Buff buff : new ArrayList<>(buffs)) {
                if (!buff.isPositive()) {
                    buff.remove();
                    buffs.remove(buff);
                }
            }
        }
    }

    /**
     * Проверяет, есть ли у актора определенный тип баффа
     * @param actor актор
     * @param buffClass класс баффа
     * @return true если у актора есть указанный бафф
     */
    public boolean hasBuff(Actor actor, Class<? extends Buff> buffClass) {
        List<Buff> buffs = activeBuffs.get(actor);
        if (buffs != null) {
            for (Buff buff : buffs) {
                if (buffClass.isInstance(buff)) {
                    return true;
                }
            }
        }
        return false;
    }
}
