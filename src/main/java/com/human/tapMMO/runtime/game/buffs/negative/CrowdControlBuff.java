package com.human.tapMMO.runtime.game.buffs.negative;

import com.human.tapMMO.runtime.game.actors.Actor;
import com.human.tapMMO.runtime.game.buffs.Buff;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrowdControlBuff extends Buff {
    public enum ControlType {
        SLOW,      // Замедление
        STUN,      // Оглушение (невозможность действий)
        ROOT,      // Обездвиживание (невозможность движения)
        SILENCE    // Молчание (невозможность использования способностей)
    }

    private ControlType type;
    private float intensity; // для замедления: процент снижения скорости (0.0-1.0)

    public CrowdControlBuff(String name, String description, int duration,
                            ControlType type, float intensity) {
        super(name, description, duration, false);
        this.type = type;
        this.intensity = intensity;
    }

    @Override
    protected void onApply() {
        Actor target = getTarget();
        if (target != null) {
            // В реальной реализации здесь будет код для применения
            // соответствующего эффекта контроля к цели
            switch (type) {
                case SLOW:
                    // Применить замедление скорости
                    break;
                case STUN:
                    // Применить оглушение
                    break;
                case ROOT:
                    // Применить обездвиживание
                    break;
                case SILENCE:
                    // Применить молчание
                    break;
            }
        }
    }

    @Override
    protected void onRemove() {
        Actor target = getTarget();
        if (target != null) {
            // Отменить эффект контроля
        }
    }

    @Override
    protected void onUpdate(float deltaTime) {
        // Большинство эффектов контроля не требуют обновления,
        // так как они просто блокируют определенные действия
    }
}
