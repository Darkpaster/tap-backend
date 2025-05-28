package com.human.tapMMO.controller.players;

import com.human.tapMMO.model.tables.Quest;
import com.human.tapMMO.runtime.game.quests.QuestDecision;
import com.human.tapMMO.runtime.game.quests.QuestNode;
import com.human.tapMMO.service.game.player.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    @GetMapping("/active/{characterId}")
    public ResponseEntity<List<Quest>> getActiveQuests(@PathVariable Long characterId) {
        return ResponseEntity.ok(questService.getActiveQuests(characterId));
    }

    @GetMapping("/completed/{characterId}")
    public ResponseEntity<List<Quest>> getCompletedQuests(@PathVariable Long characterId) {
        return ResponseEntity.ok(questService.getCompletedQuests(characterId));
    }

    @GetMapping("/available/{characterId}")
    public ResponseEntity<List<Quest>> getAvailableQuests(@PathVariable Long characterId) {
        return ResponseEntity.ok(questService.getAvailableQuests(characterId));
    }

    @PostMapping("/start/{characterId}/{questId}")
    public ResponseEntity<Quest> startQuest(@PathVariable Long characterId, @PathVariable String questId) {
        try {
            Quest quest = questService.startQuest(characterId, questId);
            return ResponseEntity.ok(quest);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/current-node/{characterId}/{questId}")
    public ResponseEntity<QuestNode> getCurrentQuestNode(@PathVariable Long characterId, @PathVariable String questId) {
        try {
            QuestNode node = questService.getCurrentQuestNode(characterId, questId);
            return ResponseEntity.ok(node);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/decisions/{characterId}/{questId}")
    public ResponseEntity<List<QuestDecision>> getAvailableDecisions(@PathVariable Long characterId, @PathVariable String questId) {
        try {
            List<QuestDecision> decisions = questService.getAvailableDecisions(characterId, questId);
            return ResponseEntity.ok(decisions);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/decision/{characterId}/{questId}/{decisionId}")
    public ResponseEntity<QuestNode> makeDecision(@PathVariable Long characterId, @PathVariable String questId, @PathVariable String decisionId) {
        try {
            QuestNode nextNode = questService.makeDecision(characterId, questId, decisionId);
            return ResponseEntity.ok(nextNode);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/complete/{characterId}/{questId}")
    public ResponseEntity<Void> completeQuest(@PathVariable Long characterId, @PathVariable String questId) {
        try {
            questService.completeQuest(characterId, questId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/cancel/{characterId}/{questId}")
    public ResponseEntity<Void> cancelQuest(@PathVariable Long characterId, @PathVariable String questId) {
        try {
            questService.cancelQuest(characterId, questId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/description/{characterId}/{questId}")
    public ResponseEntity<String> getCurrentQuestDescription(@PathVariable Long characterId, @PathVariable String questId) {
        try {
            String description = questService.getCurrentQuestDescription(characterId, questId);
            return ResponseEntity.ok(description);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stage/{characterId}/{questId}")
    public ResponseEntity<Integer> getQuestStage(@PathVariable Long characterId, @PathVariable String questId) {
        int stage = questService.getQuestStage(characterId, questId);
        return ResponseEntity.ok(stage);
    }

    @GetMapping("/is-completed/{characterId}/{questId}")
    public ResponseEntity<Boolean> isQuestCompleted(@PathVariable Long characterId, @PathVariable String questId) {
        boolean completed = questService.isQuestCompleted(characterId, questId);
        return ResponseEntity.ok(completed);
    }
}
