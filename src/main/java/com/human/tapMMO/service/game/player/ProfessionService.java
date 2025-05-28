package com.human.tapMMO.service.game.player;

import com.human.tapMMO.model.tables.Profession;
import com.human.tapMMO.repository.ProfessionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionService {

    private final ProfessionRepository professionRepository;
    private final PlayerService playerService;

    public List<Profession> getCharacterProfessions(Long characterId) {
        return professionRepository.findByCharacterId(characterId);
    }

    @Transactional
    public Profession learnProfession(Long characterId, String professionName) {
        // Проверяем, что персонаж существует
        playerService.getCharacterById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found"));

        // Проверяем, не изучена ли уже профессия
        Optional<Profession> existingProfession = professionRepository
                .findByCharacterIdAndName(characterId, professionName);

        if (existingProfession.isPresent()) {
            throw new RuntimeException("Profession already learned");
        }

        Profession profession = new Profession();
        profession.setCharacterId(characterId);
        profession.setName(professionName);
        profession.setLevel(1);
        profession.setExperience(0);

        return professionRepository.save(profession);
    }

    @Transactional
    public void addExperience(Long professionId, int experience) {
        Profession profession = professionRepository.findById(professionId)
                .orElseThrow(() -> new RuntimeException("Profession not found"));

        int newExperience = profession.getExperience() + experience;
        profession.setExperience(newExperience);

        // Простая система повышения уровня (каждые 1000 опыта = новый уровень)
        int newLevel = 1 + (newExperience / 1000);
        if (newLevel > profession.getLevel()) {
            profession.setLevel(newLevel);
        }

        professionRepository.save(profession);
    }

    public Optional<Profession> getProfessionById(Long id) {
        return professionRepository.findById(id);
    }

    @Transactional
    public void deleteProfession(Long professionId) {
        professionRepository.deleteById(professionId);
    }

    public int getProfessionLevel(Long characterId, String professionName) {
        return professionRepository.findByCharacterIdAndName(characterId, professionName)
                .map(Profession::getLevel)
                .orElse(0);
    }
}