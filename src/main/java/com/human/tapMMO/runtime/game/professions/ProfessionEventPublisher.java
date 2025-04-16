package com.human.tapMMO.runtime.game.professions;

import com.human.tapMMO.service.game.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfessionEventPublisher {

    private final AchievementService achievementService;

    public void publishProfessionLevelUp(PlayerProfession playerProfession) {
        // Обновление прогресса достижений при повышении уровня профессии
        achievementService.updateProgress(
                playerProfession.getPlayer().getId(),
                "PROFESSION_LEVEL",
                playerProfession.getProfession().getId().toString(),
                1
        );
    }

    public void publishRecipeLearned(PlayerProfession playerProfession, ProfessionRecipe recipe) {
        // Обновление прогресса достижений при изучении рецепта
        achievementService.updateProgress(
                playerProfession.getPlayer().getId(),
                "LEARN_RECIPE",
                recipe.getId().toString(),
                1
        );

        // Достижение за общее количество изученных рецептов
        achievementService.updateProgress(
                playerProfession.getPlayer().getId(),
                "LEARN_RECIPE_COUNT",
                playerProfession.getProfession().getId().toString(),
                1
        );
    }

    public void publishItemCrafted(PlayerProfession playerProfession, ProfessionRecipe recipe) {
        // Обновление прогресса достижений при создании предмета
        achievementService.updateProgress(
                playerProfession.getPlayer().getId(),
                "CRAFT_ITEM",
                recipe.getId().toString(),
                1
        );

        // Достижение за общее количество созданных предметов
        achievementService.updateProgress(
                playerProfession.getPlayer().getId(),
                "CRAFT_ITEM_COUNT",
                playerProfession.getProfession().getId().toString(),
                1
        );
    }
}

