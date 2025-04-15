package com.human.tapMMO.service.game;

import com.human.tapMMO.repository.ProfessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionService {

    private final ProfessionRepository professionRepository;
    private final PlayerProfessionRepository playerProfessionRepository;
    private final ProfessionRecipeRepository recipeRepository;
    private final CraftingRepository craftingRepository;
    private final PlayerService playerService;
    private final InventoryService inventoryService;
    private final Random random = new Random();

    public List<Profession> getAllProfessions() {
        return professionRepository.findByIsEnabled(true);
    }

    public List<PlayerProfession> getPlayerProfessions(Long playerId) {
        return playerProfessionRepository.findByPlayerId(playerId);
    }

    @Transactional
    public PlayerProfession learnProfession(Long playerId, Long professionId) {
        Player player = playerService.getPlayerById(playerId);
        Profession profession = professionRepository.findById(professionId)
                .orElseThrow(() -> new RuntimeException("Profession not found"));

        // Проверяем, не достигнут ли лимит на профессии
        long primaryCount = playerProfessionRepository.countByPlayerAndProfessionTypeNot(
                player, ProfessionType.SECONDARY);

        if ((profession.getType() != ProfessionType.SECONDARY) && primaryCount >= 2) {
            throw new RuntimeException("Player can only learn 2 primary professions");
        }

        // Проверяем, не изучена ли уже профессия
        if (playerProfessionRepository.existsByPlayerAndProfession(player, profession)) {
            throw new RuntimeException("Player already learned this profession");
        }

        PlayerProfession playerProfession = new PlayerProfession();
        playerProfession.setPlayer(player);
        playerProfession.setProfession(profession);
        playerProfession.setCurrentLevel(1);
        playerProfession.setCurrentExperience(0);
        playerProfession.setLearningDate(LocalDateTime.now());

        // Добавляем базовые рецепты
        Set<Long> basicRecipes = recipeRepository.findByProfessionAndRequiredLevelAndIsAutoLearned(
                        profession, 1, true)
                .stream()
                .map(ProfessionRecipe::getId)
                .collect(Collectors.toSet());

        playerProfession.setUnlockedRecipeIds(basicRecipes);

        return playerProfessionRepository.save(playerProfession);
    }

    @Transactional
    public void addProfessionExperience(Long playerProfessionId, int experience) {
        PlayerProfession playerProfession = playerProfessionRepository.findById(playerProfessionId)
                .orElseThrow(() -> new RuntimeException("Player profession not found"));

        Profession profession = playerProfession.getProfession();
        int currentLevel = playerProfession.getCurrentLevel();
        int currentExp = playerProfession.getCurrentExperience();

        // Находим текущий уровень в настройках профессии
        Optional<ProfessionLevel> currentLevelInfo = profession.getLevels().stream()
                .filter(level -> level.getLevel() == currentLevel)
                .findFirst();

        if (currentLevelInfo.isEmpty()) {
            throw new RuntimeException("Level configuration not found");
        }

        // Добавляем опыт
        int newExp = currentExp + experience;
        playerProfession.setCurrentExperience(newExp);

        // Проверяем, достаточно ли опыта для повышения уровня
        if (currentLevel < profession.getMaxLevel() &&
                newExp >= currentLevelInfo.get().getRequiredExperience()) {

            // Повышаем уровень
            levelUp(playerProfession);
        }

        playerProfessionRepository.save(playerProfession);
    }

    private void levelUp(PlayerProfession playerProfession) {
        int newLevel = playerProfession.getCurrentLevel() + 1;
        playerProfession.setCurrentLevel(newLevel);

        // Сбрасываем лишний опыт
        Profession profession = playerProfession.getProfession();
        Optional<ProfessionLevel> previousLevel = profession.getLevels().stream()
                .filter(level -> level.getLevel() == playerProfession.getCurrentLevel() - 1)
                .findFirst();

        if (previousLevel.isPresent()) {
            int excessExp = playerProfession.getCurrentExperience() - previousLevel.get().getRequiredExperience();
            playerProfession.setCurrentExperience(excessExp);
        } else {
            playerProfession.setCurrentExperience(0);
        }

        // Находим информацию о новом уровне
        Optional<ProfessionLevel> newLevelInfo = profession.getLevels().stream()
                .filter(level -> level.getLevel() == newLevel)
                .findFirst();

        if (newLevelInfo.isPresent()) {
            // Применяем бонусы к атрибутам
            Player player = playerProfession.getPlayer();
            // Код для добавления бонусов к атрибутам
            playerService.updatePlayer(player);

            // Открываем новые рецепты для автоизучения
            List<ProfessionRecipe> newRecipes = recipeRepository.findByProfessionAndRequiredLevelAndIsAutoLearned(
                    profession, newLevel, true);

            Set<Long> unlockedRecipes = playerProfession.getUnlockedRecipeIds();
            newRecipes.forEach(recipe -> unlockedRecipes.add(recipe.getId()));
        }
    }

    @Transactional
    public void learnRecipe(Long playerProfessionId, Long recipeId) {
        PlayerProfession playerProfession = playerProfessionRepository.findById(playerProfessionId)
                .orElseThrow(() -> new RuntimeException("Player profession not found"));

        ProfessionRecipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        // Проверяем, подходит ли рецепт для данной профессии
        if (!recipe.getProfession().getId().equals(playerProfession.getProfession().getId())) {
            throw new RuntimeException("Recipe not available for this profession");
        }

        // Проверяем уровень
        if (playerProfession.getCurrentLevel() < recipe.getRequiredLevel()) {
            throw new RuntimeException("Required profession level not met");
        }

        // Проверяем, не изучен ли уже рецепт
        if (playerProfession.getUnlockedRecipeIds().contains(recipe.getId())) {
            throw new RuntimeException("Recipe already learned");
        }

        // Добавляем рецепт
        playerProfession.getUnlockedRecipeIds().add(recipe.getId());
        playerProfessionRepository.save(playerProfession);
    }

    @Transactional
    public Crafting startCrafting(Long playerProfessionId, Long recipeId, int quantity) {
        PlayerProfession playerProfession = playerProfessionRepository.findById(playerProfessionId)
                .orElseThrow(() -> new RuntimeException("Player profession not found"));

        ProfessionRecipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        // Проверяем, изучен ли рецепт
        if (!playerProfession.getUnlockedRecipeIds().contains(recipe.getId())) {
            throw new RuntimeException("Recipe not learned");
        }

        // Проверяем наличие ингредиентов
        Player player = playerProfession.getPlayer();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            int requiredQuantity = ingredient.getQuantity() * quantity;
            if (!inventoryService.hasItem(player.getId(), ingredient.getItemId(), requiredQuantity)) {
                throw new RuntimeException("Not enough ingredients");
            }
        }

        // Проверяем, нужна ли рабочая станция
        if (recipe.getProfession().getType().requiresWorkbench()) {
            // Здесь должна быть проверка наличия рабочей станции поблизости
            // Этот код зависит от реализации игрового мира и локаций
        }

        // Забираем ингредиенты
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            if (ingredient.isConsumed()) {
                inventoryService.removeItem(player.getId(), ingredient.getItemId(), ingredient.getQuantity() * quantity);
            }
        }

        // Рассчитываем время крафта
        int totalCraftingTime = recipe.getCraftingTime() * quantity;
        LocalDateTime now = LocalDateTime.now();

        // Создаем процесс крафта
        Crafting crafting = new Crafting();
        crafting.setPlayerProfession(playerProfession);
        crafting.setRecipe(recipe);
        crafting.setStartTime(now);
        crafting.setFinishTime(now.plusSeconds(totalCraftingTime));
        crafting.setStatus(CraftingStatus.IN_PROGRESS);

        // Бонус качества зависит от уровня профессии
        int qualityBonus = calculateQualityBonus(playerProfession, recipe);
        crafting.setQualityBonus(qualityBonus);

        return craftingRepository.save(crafting);
    }

    private int calculateQualityBonus(PlayerProfession playerProfession, ProfessionRecipe recipe) {
        int levelDifference = playerProfession.getCurrentLevel() - recipe.getRequiredLevel();
        // Базовый бонус плюс бонус за превышение требуемого уровня
        return Math.min(10, 1 + (levelDifference / 2));
    }

    @Transactional
    public void completeCrafting(Long craftingId) {
        Crafting crafting = craftingRepository.findById(craftingId)
                .orElseThrow(() -> new RuntimeException("Crafting process not found"));

        // Проверяем, завершился ли процесс крафта
        if (LocalDateTime.now().isBefore(crafting.getFinishTime())) {
            throw new RuntimeException("Crafting process not finished yet");
        }

        if (crafting.getStatus() != CraftingStatus.IN_PROGRESS) {
            throw new RuntimeException("Crafting is not in progress");
        }

        PlayerProfession playerProfession = crafting.getPlayerProfession();
        ProfessionRecipe recipe = crafting.getRecipe();
        Player player = playerProfession.getPlayer();

        // Проверка на успешность крафта
        boolean success = isSuccessfulCraft(crafting);

        if (success) {
            // Выдаем результаты крафта
            for (RecipeResult result : recipe.getResults()) {
                int chance = result.getChance();
                if (chance == 100 || random.nextInt(100) < chance) {
                    int quantity = result.getMinQuantity();
                    if (result.getMaxQuantity() > result.getMinQuantity()) {
                        quantity += random.nextInt(result.getMaxQuantity() - result.getMinQuantity() + 1);
                    }

                    // Применяем бонус качества (если это имеет смысл для данного предмета)
                    // Реализация зависит от системы качества предметов в игре

                    inventoryService.addItem(player.getId(), result.getItemId(), quantity, crafting.getQualityBonus());
                }
            }

            // Выдаем опыт за крафт
            int experience = (int)(recipe.getExperienceReward() * recipe.getRarity().getExperienceMultiplier());
            addProfessionExperience(playerProfession.getId(), experience);

            crafting.setStatus(CraftingStatus.COMPLETED);
        } else {
            // Крафт неудачный, но можно вернуть часть ингредиентов
            // в зависимости от дизайна игры

            crafting.setStatus(CraftingStatus.FAILED);
        }

        craftingRepository.save(crafting);
    }

    private boolean isSuccessfulCraft(Crafting crafting) {
        ProfessionRecipe recipe = crafting.getRecipe();
        PlayerProfession playerProfession = crafting.getPlayerProfession();

        int baseChance = recipe.getChanceOfSuccess();

        // Бонус от уровня профессии
        int levelDifference = playerProfession.getCurrentLevel() - recipe.getRequiredLevel();
        int levelBonus = Math.min(15, levelDifference * 3);

        // Бонус от других факторов (экипировка, бафы и т.д.)
        // Для простоты пока не реализуем

        int totalChance = baseChance + levelBonus + crafting.getQualityBonus();
        totalChance = Math.min(totalChance, 100); // Максимальный шанс - 100%

        return random.nextInt(100) < totalChance;
    }

    @Transactional
    public List<ProfessionRecipe> getAvailableRecipes(Long playerProfessionId) {
        PlayerProfession playerProfession = playerProfessionRepository.findById(playerProfessionId)
                .orElseThrow(() -> new RuntimeException("Player profession not found"));

        // Получаем все рецепты соответствующего уровня
        List<ProfessionRecipe> recipes = recipeRepository.findByProfessionAndRequiredLevelLessThanEqual(
                playerProfession.getProfession(), playerProfession.getCurrentLevel());

        // Фильтруем по тем, которые уже изучены
        Set<Long> learnedRecipes = playerProfession.getUnlockedRecipeIds();
        return recipes.stream()
                .filter(recipe -> learnedRecipes.contains(recipe.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ProfessionRecipe> getLearnableRecipes(Long playerProfessionId) {
        PlayerProfession playerProfession = playerProfessionRepository.findById(playerProfessionId)
                .orElseThrow(() -> new RuntimeException("Player profession not found"));

        // Получаем все рецепты соответствующего уровня
        List<ProfessionRecipe> recipes = recipeRepository.findByProfessionAndRequiredLevelLessThanEqual(
                playerProfession.getProfession(), playerProfession.getCurrentLevel());

        // Фильтруем по тем, которые еще не изучены и не являются скрытыми
        Set<Long> learnedRecipes = playerProfession.getUnlockedRecipeIds();
        return recipes.stream()
                .filter(recipe -> !learnedRecipes.contains(recipe.getId()) && !recipe.isDiscoverable())
                .collect(Collectors.toList());
    }
}