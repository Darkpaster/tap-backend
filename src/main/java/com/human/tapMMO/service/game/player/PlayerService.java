package com.human.tapMMO.service.game.player;


import com.human.tapMMO.mapper.ActorMapper;
import com.human.tapMMO.model.connection.InitCharacterConnection;
import com.human.tapMMO.model.tables.*;
import com.human.tapMMO.model.tables.Character;
import com.human.tapMMO.repository.*;
import com.human.tapMMO.runtime.game.actors.player.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final CharacterRepository characterRepository;
    private final CharacterStatsRepository characterStatsRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final EquippedItemRepository equippedItemRepository;
    private final AchievementRepository achievementRepository;
    private final ProfessionRepository professionRepository;
    private final QuestRepository questRepository;
    private final SkillRepository skillRepository;
    private final TalentRepository talentRepository;

    private final ActorMapper actorMapper;

    private Character createNewCharacter(InitCharacterConnection init, long accountId) {
        Character newChar = new Character();
        newChar.setName(init.getName());
        newChar.setCharacterType(init.getCharacterType());
        newChar.setAccountId(accountId);
        characterRepository.save(newChar);
        return newChar;
    }

    private void createCharacterStats(Long charId) {
        var charStats = new CharacterStats();
        charStats.setCharacterId(charId);
        characterStatsRepository.save(charStats);
    }

    public InitCharacterConnection initNewCharacter(InitCharacterConnection init, long accountId) throws Exception {
        if (characterRepository.findCharacterByName(init.getName()).isPresent()) {
            throw new Exception("Ник занят");
        }
        Character newChar = createNewCharacter(init, accountId);
        long id = newChar.getId();
        init.setCharacterId(id);
        createCharacterStats(id);
        return init;
    }

    public Optional<Character> getCharacterById(long id) {
        return characterRepository.findById(id);
    }

    public List<Character> getCharactersByAccountId(long accountId) {
        return characterRepository.findCharactersByAccountId(accountId);
    }

    public Player getAllCharacterData(long charId) {
        final Character character = characterRepository.findById(charId).orElseThrow(
                () -> new NoSuchElementException("could not found char during attempt to receive data "+charId));
        final var characterStats = characterStatsRepository.findByCharacterId(charId).orElseThrow(
                () -> new NoSuchElementException("could not found charStats during attempt to receive data "+charId));
        return actorMapper.toPlayer(character, characterStats);
    }

    public void updateAllCharacterData(Player newData) {
        characterRepository.findById(newData.getId()).orElseThrow(() -> new NoSuchElementException("could not found char during updating data "+newData.getName()));
        final var character = actorMapper.toCharacter(newData);
        final var characterStats = actorMapper.toCharacterStats(newData);
        characterRepository.save(character);
        characterStatsRepository.save(characterStats);
    }

    public void updateCharacter(Character newData) {
        final Character character = characterRepository.findById(newData.getId()).orElseThrow(() -> new NoSuchElementException("update char"));
        character.setCharacterType(newData.getCharacterType());
        character.setGold(newData.getGold());
        character.setExperience(newData.getExperience());
        character.setLevel(newData.getLevel());
        character.setX(newData.getX());
        character.setY(newData.getY());
        character.setSanity(newData.getSanity());
        character.setReputation(newData.getReputation());
        characterRepository.save(character);
    }

    public void updateCharacterStats(CharacterStats newData) {
        final CharacterStats character = characterStatsRepository.findByCharacterId(newData.getCharacterId()).orElseThrow(() -> new NoSuchElementException("update char stats"));
        character.setHealth(newData.getHealth());
        character.setMana(newData.getMana());
        character.setIntellect(newData.getIntellect());
        character.setStamina(newData.getStamina());
        character.setStrength(newData.getStrength());
        character.setAgility(newData.getAgility());
        characterStatsRepository.save(character);
    }

    public void deleteCharacter(long characterId) {
        final var characterStats = characterStatsRepository.findByCharacterId(characterId).orElseThrow(() -> new NoSuchElementException("delete char"));
        characterRepository.deleteById(characterId);
        characterStatsRepository.deleteById(characterStats.getId());
        long deletedInventory = inventoryItemRepository.deleteAllByCharacterId(characterId);
        long deletedEquipped = equippedItemRepository.deleteAllByCharacterId(characterId);
        System.out.println("Deleted character items: "+deletedInventory+", "+deletedEquipped);
    }

    // Реализация методов проверки требований
    public boolean hasRequiredLevel(Long characterId, int requiredLevel) {
        return characterRepository.findById(characterId)
                .map(character -> character.getLevel() >= requiredLevel)
                .orElse(false);
    }

    public boolean hasRequiredAttributeLevel(Long characterId, String attributeName, int requiredValue) {
        Optional<CharacterStats> stats = characterStatsRepository.findByCharacterId(characterId);
        if (stats.isEmpty()) {
            return false;
        }

        CharacterStats characterStats = stats.get();
        switch (attributeName.toLowerCase()) {
            case "strength":
                return characterStats.getStrength() >= requiredValue;
            case "agility":
                return characterStats.getAgility() >= requiredValue;
            case "intellect":
                return characterStats.getIntellect() >= requiredValue;
            case "stamina":
                return characterStats.getStamina() >= requiredValue;
            default:
                return false;
        }
    }

    public boolean hasRequiredSkillExperience(Long characterId, String skillName, int requiredValue) {
        // Если есть таблица skills, можно реализовать проверку
        return skillRepository.findByCharacterIdAndSkillName(characterId, skillName)
                .map(skill -> skill.getExperience() >= requiredValue)
                .orElse(false);
    }

    public boolean hasTalent(Long characterId, Long talentId) {
        // Нужна связующая таблица character_talents для полной реализации
        // Пока возвращаем false
        return false;
    }

    public boolean hasCompletedQuest(Long characterId, String questName) {
        return questRepository.findByCharacterIdAndQuest(characterId, questName)
                .map(quest -> quest.getQuestStage() == -1) // -1 = завершен
                .orElse(false);
    }

    public boolean hasItem(Long characterId, String itemId, int quantity) {
        // Нужно проверить в инвентаре
        long itemIdLong;
        try {
            itemIdLong = Long.parseLong(itemId);
        } catch (NumberFormatException e) {
            return false;
        }

        return inventoryItemRepository.findByCharacterIdAndItemId(characterId, itemIdLong)
                .map(inventoryItem -> inventoryItem.getQuantity() >= quantity)
                .orElse(false);
    }

    public void addTalentToCharacter(Long characterId, Talent talent) {
        // Здесь должна быть логика добавления таланта персонажу
        // Нужна связующая таблица character_talents
        // Пока оставляем пустой метод
    }

    public void applyAttributeModifier(Long characterId, String attributeName, double value) {
        Optional<CharacterStats> statsOpt = characterStatsRepository.findByCharacterId(characterId);
        if (statsOpt.isEmpty()) {
            return;
        }

        CharacterStats stats = statsOpt.get();
        int intValue = (int) value;

        switch (attributeName.toLowerCase()) {
            case "strength":
                stats.setStrength(stats.getStrength() + intValue);
                break;
            case "agility":
                stats.setAgility(stats.getAgility() + intValue);
                break;
            case "intellect":
                stats.setIntellect(stats.getIntellect() + intValue);
                break;
            case "stamina":
                stats.setStamina(stats.getStamina() + intValue);
                break;
            case "health":
                stats.setHealth(stats.getHealth() + intValue);
                break;
            case "mana":
                stats.setMana(stats.getMana() + intValue);
                break;
        }

        characterStatsRepository.save(stats);
    }
}