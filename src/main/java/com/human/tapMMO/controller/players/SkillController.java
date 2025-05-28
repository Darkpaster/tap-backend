package com.human.tapMMO.controller.players;

import com.human.tapMMO.model.tables.Skill;
import com.human.tapMMO.service.game.player.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/character/{characterId}")
    public ResponseEntity<List<Skill>> getCharacterSkills(@PathVariable Long characterId) {
        List<Skill> skills = skillService.getCharacterSkills(characterId);
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/character/{characterId}/skill/{skillTitle}")
    public ResponseEntity<Skill> getCharacterSkill(@PathVariable Long characterId, @PathVariable String skillTitle) {
        Optional<Skill> skill = skillService.getCharacterSkill(characterId, skillTitle);
        return skill.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/character/{characterId}/skill/{skillTitle}/level")
    public ResponseEntity<Integer> getSkillLevel(@PathVariable Long characterId, @PathVariable String skillTitle) {
        int level = skillService.getSkillLevel(characterId, skillTitle);
        return ResponseEntity.ok(level);
    }

    @GetMapping("/character/{characterId}/skill/{skillTitle}/experience")
    public ResponseEntity<Integer> getSkillExperience(@PathVariable Long characterId, @PathVariable String skillTitle) {
        int experience = skillService.getSkillExperience(characterId, skillTitle);
        return ResponseEntity.ok(experience);
    }

    @PostMapping("/character/{characterId}/skill/{skillTitle}/create")
    public ResponseEntity<Skill> createSkill(@PathVariable Long characterId, @PathVariable String skillTitle) {
        try {
            Skill skill = skillService.createSkill(characterId, skillTitle);
            return ResponseEntity.ok(skill);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/character/{characterId}/skill/{skillTitle}/experience")
    public ResponseEntity<SkillService.SkillProgressResult> addExperience(@PathVariable Long characterId,
                                                                          @PathVariable String skillTitle,
                                                                          @RequestParam int experiencePoints) {
        try {
            SkillService.SkillProgressResult result = skillService.addExperience(characterId, skillTitle, experiencePoints);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/character/{characterId}/skill/{skillTitle}/level")
    public ResponseEntity<Skill> setSkillLevel(@PathVariable Long characterId,
                                               @PathVariable String skillTitle,
                                               @RequestParam int level) {
        try {
            Skill skill = skillService.setSkillLevel(characterId, skillTitle, level);
            return ResponseEntity.ok(skill);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/character/{characterId}/skill/{skillTitle}/requirements/level")
    public ResponseEntity<Boolean> checkSkillLevelRequirement(@PathVariable Long characterId,
                                                              @PathVariable String skillTitle,
                                                              @RequestParam int requiredLevel) {
        boolean hasLevel = skillService.hasRequiredSkillLevel(characterId, skillTitle, requiredLevel);
        return ResponseEntity.ok(hasLevel);
    }

    @GetMapping("/character/{characterId}/skill/{skillTitle}/requirements/experience")
    public ResponseEntity<Boolean> checkSkillExperienceRequirement(@PathVariable Long characterId,
                                                                   @PathVariable String skillTitle,
                                                                   @RequestParam int requiredExperience) {
        boolean hasExperience = skillService.hasRequiredSkillExperience(characterId, skillTitle, requiredExperience);
        return ResponseEntity.ok(hasExperience);
    }

    @GetMapping("/character/{characterId}/statistics")
    public ResponseEntity<SkillService.SkillStatistics> getSkillStatistics(@PathVariable Long characterId) {
        SkillService.SkillStatistics statistics = skillService.getSkillStatistics(characterId);
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/character/{characterId}/top")
    public ResponseEntity<List<Skill>> getTopSkills(@PathVariable Long characterId, @RequestParam(defaultValue = "5") int limit) {
        List<Skill> topSkills = skillService.getTopSkills(characterId, limit);
        return ResponseEntity.ok(topSkills);
    }

    @GetMapping("/character/{characterId}/skill/{skillTitle}/next-level")
    public ResponseEntity<Integer> getExperienceToNextLevel(@PathVariable Long characterId, @PathVariable String skillTitle) {
        int expToNext = skillService.getExperienceToNextLevel(characterId, skillTitle);
        return ResponseEntity.ok(expToNext);
    }

    @GetMapping("/character/{characterId}/skill/{skillTitle}/progress")
    public ResponseEntity<Double> getProgressToNextLevel(@PathVariable Long characterId, @PathVariable String skillTitle) {
        double progress = skillService.getProgressToNextLevel(characterId, skillTitle);
        return ResponseEntity.ok(progress);
    }

    @GetMapping("/character/{characterId}/skill/{skillTitle}/bonus")
    public ResponseEntity<Double> getSkillBonus(@PathVariable Long characterId, @PathVariable String skillTitle) {
        double bonus = skillService.getSkillBonus(characterId, skillTitle);
        return ResponseEntity.ok(bonus);
    }

    @GetMapping("/character/{characterId}/ability")
    public ResponseEntity<Boolean> canUseAbility(@PathVariable Long characterId,
                                                 @RequestParam String requiredSkill,
                                                 @RequestParam int requiredLevel) {
        boolean canUse = skillService.canUseAbility(characterId, requiredSkill, requiredLevel);
        return ResponseEntity.ok(canUse);
    }

    @DeleteMapping("/skill/{skillId}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long skillId) {
        skillService.deleteSkill(skillId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/character/{characterId}/skills")
    public ResponseEntity<Integer> deleteAllCharacterSkills(@PathVariable Long characterId) {
        int deletedCount = skillService.deleteAllCharacterSkills(characterId);
        return ResponseEntity.ok(deletedCount);
    }
}