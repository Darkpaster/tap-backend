package com.human.tapMMO.service.game.player;

import com.human.tapMMO.runtime.game.talents.TalentRequirementType;
import com.human.tapMMO.model.tables.Talent;
import com.human.tapMMO.repository.TalentRepository;
import com.human.tapMMO.runtime.game.buffs.Buff;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.human.tapMMO.runtime.game.talents.TalentRequirementType.*;

@Service
@RequiredArgsConstructor
public class TalentService {

    private final TalentRepository talentRepository;
    private final PlayerService playerService;

    public List<Talent> getAllTalents() {
        return talentRepository.findAll();
    }

    @Transactional
    public Talent createTalent(String name) {
        Talent talent = new Talent();
        talent.setName(name);
        return talentRepository.save(talent);
    }

    public Optional<Talent> getTalentById(Long id) {
        return talentRepository.findById(id);
    }

    public Optional<Talent> getTalentByName(String name) {
        return talentRepository.findByName(name);
    }

    @Transactional
    public void deleteTalent(Long talentId) {
        talentRepository.deleteById(talentId);
    }

    // Методы для работы с талантами персонажей можно добавить,
    // если будет создана связующая таблица character_talents
    public boolean canLearnTalent(Long characterId, Long talentId) {
        // Здесь может быть логика проверки требований
        // Пока возвращаем true как заглушку
        return playerService.getCharacterById(characterId).isPresent();
    }
}