package com.human.tapMMO.util.time;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Класс для реализации задержки на основе счетчика кадров.
 * Используется для отслеживания времени на основе итераций цикла игрового движка.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delay {
    private int frameCounter;
    private int iterations;

    /**
     * Создает объект задержки с указанным количеством итераций.
     *
     * @param iterations количество итераций перед завершением задержки
     */
    public Delay(int iterations) {
        this.frameCounter = 0;
        this.iterations = iterations;
    }

    /**
     * Проверяет, истекло ли время задержки.
     * Увеличивает счетчик и сравнивает его с заданным количеством итераций.
     * Если счетчик превышает заданное количество итераций, он сбрасывается.
     *
     * @return true, если счетчик достиг или превысил заданное количество итераций
     */
    public boolean timeIsUp() {
        if (frameCounter > iterations) {
            frameCounter = 0;
        }
        return ++frameCounter >= iterations;
    }
}