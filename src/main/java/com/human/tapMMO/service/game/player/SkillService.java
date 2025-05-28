package com.human.tapMMO.service.game.player;

import com.human.tapMMO.model.tables.Skill;
import com.human.tapMMO.repository.SkillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final PlayerService playerService;

    // Константы для расчета опыта
    private static final int BASE_EXP_PER_LEVEL = 100;
    private static final double EXP_MULTIPLIER = 1.5;
    private static final int MAX_SKILL_LEVEL = 100;

    /**
     * Получает все навыки персонажа
     */
    public List<Skill> getCharacterSkills(Long characterId) {
        return skillRepository.findByCharacterId(characterId);
    }

    /**
     * Получает конкретный навык персонажа по названию
     */
    public Optional<Skill> getCharacterSkill(Long characterId, String skillTitle) {
        return skillRepository.findByCharacterIdAndTitle(characterId, skillTitle);
    }

    /**
     * Получает уровень конкретного навыка
     */
    public int getSkillLevel(Long characterId, String skillTitle) {
        return getCharacterSkill(characterId, skillTitle)
                .map(Skill::getLevel)
                .orElse(0);
    }

    /**
     * Получает опыт конкретного навыка
     */
    public int getSkillExperience(Long characterId, String skillTitle) {
        return getCharacterSkill(characterId, skillTitle)
                .map(Skill::getExperience)
                .orElse(0);
    }

    /**
     * Создает новый навык для персонажа
     */
    @Transactional
    public Skill createSkill(Long characterId, String skillTitle) {
        // Проверяем, что персонаж существует
        playerService.getCharacterById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found"));

        // Проверяем, что навык еще не существует
        if (getCharacterSkill(characterId, skillTitle).isPresent()) {
            throw new RuntimeException("Skill already exists for this character");
        }

        Skill skill = new Skill();
        skill.setCharacterId(characterId);
        skill.setTitle(skillTitle);
        skill.setLevel(1);
        skill.setExperience(0);

        return skillRepository.save(skill);
    }

    /**
     * Добавляет опыт к навыку
     */
    @Transactional
    public SkillProgressResult addExperience(Long characterId, String skillTitle, int experiencePoints) {
        if (experiencePoints <= 0) {
            throw new IllegalArgumentException("Experience points must be positive");
        }

        Skill skill = getCharacterSkill(characterId, skillTitle)
                .orElseGet(() -> createSkill(characterId, skillTitle));

        int oldLevel = skill.getLevel();
        int newExperience = skill.getExperience() + experiencePoints;
        skill.setExperience(newExperience);

        // Проверяем повышение уровня
        int newLevel = calculateLevelFromExperience(newExperience);
        if (newLevel > skill.getLevel() && newLevel <= MAX_SKILL_LEVEL) {
            skill.setLevel(newLevel);
        }

        skillRepository.save(skill);

        return new SkillProgressResult(
                skill,
                oldLevel,
                newLevel,
                experiencePoints,
                newLevel > oldLevel
        );
    }

    /**
     * Устанавливает конкретный уровень навыка (для админских команд)
     */
    @Transactional
    public Skill setSkillLevel(Long characterId, String skillTitle, int level) {
        if (level < 1 || level > MAX_SKILL_LEVEL) {
            throw new IllegalArgumentException("Level must be between 1 and " + MAX_SKILL_LEVEL);
        }

        Skill skill = getCharacterSkill(characterId, skillTitle)
                .orElseGet(() -> createSkill(characterId, skillTitle));

        skill.setLevel(level);
        skill.setExperience(calculateExperienceForLevel(level));

        return skillRepository.save(skill);
    }

    /**
     * Проверяет, имеет ли персонаж требуемый уровень навыка
     */
    public boolean hasRequiredSkillLevel(Long characterId, String skillTitle, int requiredLevel) {
        return getSkillLevel(characterId, skillTitle) >= requiredLevel;
    }

    /**
     * Проверяет, имеет ли персонаж требуемый опыт в навыке
     */
    public boolean hasRequiredSkillExperience(Long characterId, String skillTitle, int requiredExperience) {
        return getSkillExperience(characterId, skillTitle) >= requiredExperience;
    }

    /**
     * Получает статистику всех навыков персонажа
     */
    public SkillStatistics getSkillStatistics(Long characterId) {
        List<Skill> skills = getCharacterSkills(characterId);

        int totalSkills = skills.size();
        int totalLevels = skills.stream().mapToInt(Skill::getLevel).sum();
        int totalExperience = skills.stream().mapToInt(Skill::getExperience).sum();
        int maxLevel = skills.stream().mapToInt(Skill::getLevel).max().orElse(0);

        Map<String, Integer> skillLevels = skills.stream()
                .collect(Collectors.toMap(Skill::getTitle, Skill::getLevel));

        return new SkillStatistics(totalSkills, totalLevels, totalExperience, maxLevel, skillLevels);
    }

    /**
     * Получает топ навыков персонажа по уровню
     */
    public List<Skill> getTopSkills(Long characterId, int limit) {
        return getCharacterSkills(characterId).stream()
                .sorted((s1, s2) -> Integer.compare(s2.getLevel(), s1.getLevel()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Рассчитывает опыт, необходимый для достижения следующего уровня
     */
    public int getExperienceToNextLevel(Long characterId, String skillTitle) {
        Skill skill = getCharacterSkill(characterId, skillTitle)
                .orElse(null);

        if (skill == null || skill.getLevel() >= MAX_SKILL_LEVEL) {
            return 0;
        }

        int expForNextLevel = calculateExperienceForLevel(skill.getLevel() + 1);
        return expForNextLevel - skill.getExperience();
    }

    /**
     * Рассчитывает прогресс к следующему уровню в процентах
     */
    public double getProgressToNextLevel(Long characterId, String skillTitle) {
        Skill skill = getCharacterSkill(characterId, skillTitle)
                .orElse(null);

        if (skill == null || skill.getLevel() >= MAX_SKILL_LEVEL) {
            return 100.0;
        }

        int currentLevelExp = calculateExperienceForLevel(skill.getLevel());
        int nextLevelExp = calculateExperienceForLevel(skill.getLevel() + 1);
        int progressExp = skill.getExperience() - currentLevelExp;
        int levelExpRange = nextLevelExp - currentLevelExp;

        return (double) progressExp / levelExpRange * 100.0;
    }

    /**
     * Удаляет навык персонажа
     */
    @Transactional
    public void deleteSkill(Long skillId) {
        skillRepository.deleteById(skillId);
    }

    /**
     * Удаляет все навыки персонажа
     */
    @Transactional
    public int deleteAllCharacterSkills(Long characterId) {
        List<Skill> skills = getCharacterSkills(characterId);
        skillRepository.deleteAll(skills);
        return skills.size();
    }

    /**
     * Получает бонус от навыка (например, для расчетов урона, вероятности успеха и т.д.)
     */
    public double getSkillBonus(Long characterId, String skillTitle) {
        int level = getSkillLevel(characterId, skillTitle);
        // Простая формула: каждый уровень дает 1% бонуса
        return level * 0.01;
    }

    /**
     * Проверяет, может ли персонаж использовать способность с требованием по навыку
     */
    public boolean canUseAbility(Long characterId, String requiredSkill, int requiredLevel) {
        return hasRequiredSkillLevel(characterId, requiredSkill, requiredLevel);
    }

    /**
     * Рассчитывает уровень на основе опыта
     */
    private int calculateLevelFromExperience(int experience) {
        if (experience <= 0) return 1;

        int level = 1;
        int expForCurrentLevel = 0;

        while (level < MAX_SKILL_LEVEL) {
            int expForNextLevel = calculateExperienceForLevel(level + 1);
            if (experience < expForNextLevel) {
                break;
            }
            level++;
        }

        return level;
    }

    /**
     * Рассчитывает необходимый опыт для достижения уровня
     */
    private int calculateExperienceForLevel(int level) {
        if (level <= 1) return 0;

        int totalExp = 0;
        for (int i = 2; i <= level; i++) {
            totalExp += (int) (BASE_EXP_PER_LEVEL * Math.pow(EXP_MULTIPLIER, i - 2));
        }
        return totalExp;
    }

    /**
     * Класс для результата прогресса навыка
     */
    public static class SkillProgressResult {
        private final Skill skill;
        private final int oldLevel;
        private final int newLevel;
        private final int experienceGained;
        private final boolean leveledUp;

        public SkillProgressResult(Skill skill, int oldLevel, int newLevel, int experienceGained, boolean leveledUp) {
            this.skill = skill;
            this.oldLevel = oldLevel;
            this.newLevel = newLevel;
            this.experienceGained = experienceGained;
            this.leveledUp = leveledUp;
        }

        public Skill getSkill() { return skill; }
        public int getOldLevel() { return oldLevel; }
        public int getNewLevel() { return newLevel; }
        public int getExperienceGained() { return experienceGained; }
        public boolean isLeveledUp() { return leveledUp; }
        public int getLevelsGained() { return newLevel - oldLevel; }
    }

    /**
     * Класс для статистики навыков
     */
    public static class SkillStatistics {
        private final int totalSkills;
        private final int totalLevels;
        private final int totalExperience;
        private final int maxLevel;
        private final Map<String, Integer> skillLevels;

        public SkillStatistics(int totalSkills, int totalLevels, int totalExperience, int maxLevel, Map<String, Integer> skillLevels) {
            this.totalSkills = totalSkills;
            this.totalLevels = totalLevels;
            this.totalExperience = totalExperience;
            this.maxLevel = maxLevel;
            this.skillLevels = skillLevels;
        }

        public int getTotalSkills() { return totalSkills; }
        public int getTotalLevels() { return totalLevels; }
        public int getTotalExperience() { return totalExperience; }
        public int getMaxLevel() { return maxLevel; }
        public Map<String, Integer> getSkillLevels() { return skillLevels; }
        public double getAverageLevel() { return totalSkills > 0 ? (double) totalLevels / totalSkills : 0; }
    }

    /**
     * Предопределенные названия навыков (можно расширить)
     */
    public static class SkillNames {
        public static final String COMBAT = "Combat";
        public static final String MAGIC = "Magic";
        public static final String CRAFTING = "Crafting";
        public static final String MINING = "Mining";
        public static final String FISHING = "Fishing";
        public static final String COOKING = "Cooking";
        public static final String ALCHEMY = "Alchemy";
        public static final String LOCKPICKING = "Lockpicking";
        public static final String STEALTH = "Stealth";
        public static final String ARCHERY = "Archery";
        public static final String SMITHING = "Smithing";
        public static final String ENCHANTING = "Enchanting";
        public static final String HERBALISM = "Herbalism";
        public static final String TRADING = "Trading";
        public static final String LEADERSHIP = "Leadership";
    }
}