package com.human.tapMMO.service.game;

import com.human.tapMMO.model.game.TalentRequirementType;
import com.human.tapMMO.model.tables.Talent;
import com.human.tapMMO.repository.TalentRepository;
import com.human.tapMMO.runtime.game.buff.Buff;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.human.tapMMO.model.game.TalentRequirementType.*;

@Service
public class TalentService {

    private final TalentRepository talentRepository;
    private final CharacterService characterService;

    @Autowired
    public TalentService(TalentRepository talentRepository, CharacterService characterService) {
        this.talentRepository = talentRepository;
        this.characterService = characterService;
    }

    public List<Talent> getAllTalents() {
        return talentRepository.findAll();
    }

    public List<Talent> getTalentsByCategory(TalentCategory category) {
        return talentRepository.findByCategory(category);
    }

    public List<Talent> getTalentsBySubCategory(TalentSubCategory subCategory) {
        return talentRepository.findBySubCategory(subCategory);
    }

    public List<Talent> getTalentsBySpecialization(TalentSpecialization specialization) {
        return talentRepository.findBySpecialization(specialization);
    }

    public Optional<Talent> getTalentById(Long id) {
        return talentRepository.findById(id);
    }

    public List<Talent> getAvailableTalents(Long characterId) {
        // Логика получения доступных для изучения талантов
        // на основе текущих атрибутов персонажа
        return null; // Заглушка
    }

    @Transactional
    public boolean learnTalent(Long characterId, Long talentId) {
        Optional<Talent> talentOpt = talentRepository.findById(talentId);
        if (talentOpt.isEmpty()) {
            return false;
        }

        Talent talent = talentOpt.get();

        // Проверяем, выполняются ли все требования для изучения таланта
        if (!meetsAllRequirements(characterId, talent)) {
            return false;
        }

        // Обновляем скиллы и таланты персонажа
        characterService.addTalentToCharacter(characterId, talent);
        applyTalentEffects(characterId, talent);

        return true;
    }

    private boolean meetsAllRequirements(Long characterId, Talent talent) {
        // Проверяем каждое требование таланта
        for (TalentRequirementType req : talent.getRequirements()) {
            switch (req.getType()) {
                case CHARACTER_LEVEL:
                    if (!characterService.hasRequiredLevel(characterId, req.getRequiredValue())) {
                        return false;
                    }
                    break;
                case ATTRIBUTE_LEVEL:
                    if (!characterService.hasRequiredAttributeLevel(characterId, req.getAttributeName(), req.getRequiredValue())) {
                        return false;
                    }
                    break;
                case SKILL_EXPERIENCE:
                    if (!characterService.hasRequiredSkillExperience(characterId, req.getAttributeName(), req.getRequiredValue())) {
                        return false;
                    }
                    break;
                case TALENT_PREREQUISITE:
                    if (!characterService.hasTalent(characterId, Long.valueOf(req.getAttributeName()))) {
                        return false;
                    }
                    break;
                case QUEST_COMPLETION:
                    if (!characterService.hasCompletedQuest(characterId, req.getAttributeName())) {
                        return false;
                    }
                    break;
                case ITEM_POSSESSION:
                    if (!characterService.hasItem(characterId, req.getAttributeName(), req.getRequiredValue())) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    private void applyTalentEffects(Long characterId, Talent talent) {
        for (Buff effect : talent.getEffects()) {
            characterService.applyAttributeModifier(
                    characterId,
                    effect.getAttributeAffected(),
                    effect.getValue()
            );
        }
    }
}
