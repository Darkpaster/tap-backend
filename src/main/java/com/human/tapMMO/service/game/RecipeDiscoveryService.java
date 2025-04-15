package com.human.tapMMO.service.game;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class RecipeDiscoveryService {

    private final ProfessionRecipeRepository recipeRepository;
    private final PlayerProfessionRepository playerProfessionRepository;
    private final Random random = new Random();

    @Transactional
    public boolean tryDiscoverRecipe(Long playerProfessionId, ProfessionType activityType) {
        PlayerProfession playerProfession = playerProfessionRepository.findById(playerProfessionId)
                .orElseThrow(() -> new RuntimeException("Player profession not found"));

        // Получаем все рецепты, которые можно обнаружить
        List<ProfessionRecipe> discoverableRecipes = getDiscoverableRecipes(playerProfession, activityType);

        if (discoverableRecipes.isEmpty()) {
            return false;
        }

        // Базовый шанс обнаружения
        int baseChance = calculateDiscoveryChance(playerProfession);

        // Если повезло, то открываем один из рецептов
        if (random.nextInt(100) < baseChance) {
            // Выбираем случайный рецепт из списка
            ProfessionRecipe discoveredRecipe = discoverableRecipes.get(random.nextInt(discoverableRecipes.size()));

            // Добавляем его в изученные
            playerProfession.getUnlockedRecipeIds().add(discoveredRecipe.getId());
            playerProfessionRepository.save(playerProfession);

            return true;
        }

        return false;
    }

    private List<ProfessionRecipe> getDiscoverableRecipes(PlayerProfession playerProfession, ProfessionType activityType) {
        // Получаем рецепты соответствующего уровня и типа, которые можно обнаружить
        List<ProfessionRecipe> recipes = recipeRepository.findByProfessionAndRequiredLevelLessThanEqual(
                playerProfession.getProfession(), playerProfession.getCurrentLevel());

        // Фильтруем по тем, которые еще не изучены и могут быть обнаружены
        Set<Long> learnedRecipes = playerProfession.getUnlockedRecipeIds();
        return recipes.stream()
                .filter(recipe -> !learnedRecipes.contains(recipe.getId()) &&
                        recipe.isDiscoverable() &&
                        recipe.getProfession().getType() == activityType)
                .collect(Collectors.toList());
    }

    private int calculateDiscoveryChance(PlayerProfession playerProfession) {
        // Базовый шанс
        int baseChance = 5; // 5% базовый шанс

        // Бонус от уровня профессии
        int levelBonus = playerProfession.getCurrentLevel() / 10; // +1% за каждые 10 уровней

        // Здесь могут быть дополнительные модификаторы

        return baseChance + levelBonus;
    }
}

