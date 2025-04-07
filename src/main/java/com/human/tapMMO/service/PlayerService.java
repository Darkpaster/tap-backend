package com.human.tapMMO.service;


import com.human.tapMMO.model.InitCharacterConnection;
import com.human.tapMMO.model.tables.*;
import com.human.tapMMO.model.tables.Character;
import com.human.tapMMO.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.Arrays;
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

    private Character createNewCharacter(InitCharacterConnection init, long accoundId) {
        Character newChar = new Character();
        newChar.setNickname(init.getNickname());
        newChar.setCharacterType(init.getCharacterType());
        newChar.setAccountId(accoundId);
        characterRepository.save(newChar);
        return newChar;
    }

    private void createCharacterStats(Long charId) {
        var charStats = new CharacterStats();
        charStats.setCharacterId(charId);
        characterStatsRepository.save(charStats);
    }

    public InitCharacterConnection initNewCharacter(InitCharacterConnection init, long accountId) throws Exception {
        if (characterRepository.findCharacterByNickname(init.getNickname()).isPresent()) {
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
    }

    public void updateCharacterStats(CharacterStats newData) {
        final CharacterStats character = characterStatsRepository.findByCharacterId(newData.getCharacterId()).orElseThrow(() -> new NoSuchElementException("update char stats"));
        character.setHealth(newData.getHealth());
        character.setMana(newData.getMana());
        character.setIntellect(newData.getIntellect());
        character.setStamina(newData.getStamina());
        character.setStrength(newData.getStrength());
        character.setAgility(newData.getAgility());
    }

    public void deleteCharacter(long characterId) {
        final var characterStats = characterStatsRepository.findByCharacterId(characterId).orElseThrow(() -> new NoSuchElementException("delete char"));
        characterRepository.deleteById(characterId);
        characterStatsRepository.deleteById(characterStats.getId());
        long deletedInventory = inventoryItemRepository.deleteAllByCharacterId(characterId);
        long deletedEquipped = equippedItemRepository.deleteAllByCharacterId(characterId);
        System.out.println("Deleted character items: "+deletedInventory+", "+deletedEquipped);
    }


}