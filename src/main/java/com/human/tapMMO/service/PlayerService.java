package com.human.tapMMO.service;


import com.human.tapMMO.model.InitCharacterConnection;
import com.human.tapMMO.model.tables.Character;
import com.human.tapMMO.model.tables.CharacterStats;
import com.human.tapMMO.repository.CharacterRepository;
import com.human.tapMMO.repository.CharacterStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final CharacterRepository characterRepository;
    private final CharacterStatsRepository characterStatsRepository;

    private Character createNewCharacter(InitCharacterConnection init) {
        Character newChar = new Character();
        newChar.setNickname(init.getNickname());
        newChar.setCharacterType(init.getCharacterType());
        characterRepository.save(newChar);
        return newChar;
    }

    private void createCharacterStats(Long charId) {
        var charStats = new CharacterStats();
        charStats.setCharacterId(charId);
        characterStatsRepository.save(charStats);
    }

    public InitCharacterConnection initNewCharacter(InitCharacterConnection init) {
        Character newChar = createNewCharacter(init);
        long id = newChar.getId();
        init.setId(id);
        createCharacterStats(id);
        return init;
    }

    public Optional<Character> getCharacterByID(long id) {
        return characterRepository.findById(id);
    }

}