package com.human.tapMMO.service.game.player;

import com.human.tapMMO.repository.QuestRepository;
import com.human.tapMMO.runtime.game.quests.Quest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
package com.human.tapMMO.service.game.player;

import com.human.tapMMO.repository.QuestRepository;
import com.human.tapMMO.runtime.game.quests.*;
import com.human.tapMMO.runtime.game.quests.requirement.QuestRequirement;
import com.human.tapMMO.model.tables.Character;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final PlayerService playerService;

    // Хранилище шаблонов квестов (в реальном приложении может быть загружено из файлов/БД)
    private final Map<String, Quest> questTemplates = new HashMap<>();

    private final ItemService itemService;

    /**
     * Создает контекст игрока на основе данных из БД
     */
    private PlayerContext createPlayerContext(Long characterId) {
        Character character = playerService.getCharacterById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found"));

        PlayerContext context = new PlayerContext(String.valueOf(characterId));

        // Заполняем статистики
        context.setStat("level", character.getLevel());
        context.setStat("experience", character.getExperience());
        context.setStat("gold", character.getGold());
        context.setStat("reputation", character.getReputation());
        context.setStat("sanity", character.getSanity());

        // Добавляем статистики персонажа
        var playerData = playerService.getAllCharacterData(characterId);
        context.setStat("health", playerData.getHealth());
        context.setStat("mana", playerData.getMana());
        context.setStat("strength", playerData.getStrength());
        context.setStat("agility", playerData.getAgility());
        context.setStat("intellect", playerData.getIntellect());
        context.setStat("stamina", playerData.getStamina());

        // Заполняем завершенные квесты
        List<com.human.tapMMO.model.tables.Quest> completedQuests = questRepository.findByCharacterId(characterId)
                .stream()
                .filter(q -> q.getQuestStage() == -1)
                .collect(Collectors.toList());

        for (var quest : completedQuests) {
            context.completeQuest(quest.getQuest());
        }

        // TODO: Заполнение инвентаря из БД
        // Здесь нужно добавить логику загрузки предметов из inventoryItemRepository

        return context;
    }

    /**
     * Получает активные квесты персонажа
     */
    public List<Quest> getActiveQuests(Long characterId) {
        List<com.human.tapMMO.model.tables.Quest> dbQuests = questRepository.findByCharacterId(characterId)
                .stream()
                .filter(q -> q.getQuestStage() > 0 && q.getQuestStage() != -1)
                .collect(Collectors.toList());

        return dbQuests.stream()
                .map(dbQuest -> {
                    Quest questTemplate = questTemplates.get(dbQuest.getQuest());
                    if (questTemplate != null) {
                        Quest activeQuest = new Quest(questTemplate.getId(), questTemplate.getTitle(), questTemplate.getDescription());
                        activeQuest.setStatus(QuestStatus.IN_PROGRESS);
                        activeQuest.setRootNode(questTemplate.getRootNode());
                        activeQuest.setRewards(questTemplate.getRewards());
                        activeQuest.setRequirements(questTemplate.getRequirements());
                        return activeQuest;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Получает завершенные квесты персонажа
     */
    public List<Quest> getCompletedQuests(Long characterId) {
        List<com.human.tapMMO.model.tables.Quest> completedDbQuests = questRepository.findByCharacterId(characterId)
                .stream()
                .filter(q -> q.getQuestStage() == -1)
                .collect(Collectors.toList());

        return completedDbQuests.stream()
                .map(dbQuest -> {
                    Quest questTemplate = questTemplates.get(dbQuest.getQuest());
                    if (questTemplate != null) {
                        Quest completedQuest = new Quest(questTemplate.getId(), questTemplate.getTitle(), questTemplate.getDescription());
                        completedQuest.setStatus(QuestStatus.COMPLETED);
                        return completedQuest;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Проверяет, может ли персонаж начать квест
     */
    public boolean canStartQuest(Long characterId, String questId) {
        Quest questTemplate = questTemplates.get(questId);
        if (questTemplate == null) {
            return false;
        }

        // Проверяем, не начат ли уже квест
        Optional<com.human.tapMMO.model.tables.Quest> existingQuest =
                questRepository.findByCharacterIdAndQuest(characterId, questId);
        if (existingQuest.isPresent()) {
            return false;
        }

        // Проверяем требования квеста
        PlayerContext context = createPlayerContext(characterId);
        return questTemplate.getRequirements().stream()
                .allMatch(req -> req.isMet(context));
    }

    /**
     * Начинает квест для персонажа
     */
    @Transactional
    public Quest startQuest(Long characterId, String questId) {
        if (!canStartQuest(characterId, questId)) {
            throw new RuntimeException("Cannot start quest: requirements not met or quest already active");
        }

        Quest questTemplate = questTemplates.get(questId);

        // Создаем запись в БД
        com.human.tapMMO.model.tables.Quest dbQuest = new com.human.tapMMO.model.tables.Quest();
        dbQuest.setCharacterId(characterId);
        dbQuest.setQuest(questId);
        dbQuest.setQuestStage(1);
        questRepository.save(dbQuest);

        // Возвращаем runtime объект квеста
        Quest runtimeQuest = new Quest(questTemplate.getId(), questTemplate.getTitle(), questTemplate.getDescription());
        runtimeQuest.setStatus(QuestStatus.IN_PROGRESS);
        runtimeQuest.setRootNode(questTemplate.getRootNode());
        runtimeQuest.setRewards(questTemplate.getRewards());
        runtimeQuest.setRequirements(questTemplate.getRequirements());

        return runtimeQuest;
    }

    /**
     * Получает текущий узел квеста для персонажа
     */
    public QuestNode getCurrentQuestNode(Long characterId, String questId) {
        com.human.tapMMO.model.tables.Quest dbQuest = questRepository
                .findByCharacterIdAndQuest(characterId, questId)
                .orElseThrow(() -> new RuntimeException("Quest not found"));

        Quest questTemplate = questTemplates.get(questId);
        if (questTemplate == null) {
            throw new RuntimeException("Quest template not found");
        }

        // Находим текущий узел по стадии квеста
        return findNodeByStage(questTemplate.getRootNode(), dbQuest.getQuestStage());
    }

    /**
     * Рекурсивно ищет узел по стадии
     */
    private QuestNode findNodeByStage(QuestNode node, int stage) {
        // Простая реализация - можно расширить для более сложной логики
        if (stage == 1) {
            return node;
        }

        // Для демонстрации - возвращаем первый дочерний узел
        if (!node.getDecisions().isEmpty()) {
            QuestDecision firstDecision = node.getDecisions().get(0);
            if (firstDecision.getNextNode() != null) {
                return firstDecision.getNextNode();
            }
        }

        return node;
    }

    /**
     * Выбирает решение в квесте и переходит к следующему узлу
     */
    @Transactional
    public QuestNode makeDecision(Long characterId, String questId, String decisionId) {
        QuestNode currentNode = getCurrentQuestNode(characterId, questId);

        // Находим выбранное решение
        QuestDecision selectedDecision = currentNode.getDecisions().stream()
                .filter(decision -> decision.getId().equals(decisionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Decision not found"));

        // Проверяем требования узла
        PlayerContext context = createPlayerContext(characterId);
        QuestNode nextNode = selectedDecision.getNextNode();

        if (nextNode != null) {
            boolean canProceed = nextNode.getRequirements().stream()
                    .allMatch(req -> req.isMet(context));

            if (!canProceed) {
                throw new RuntimeException("Requirements not met for next quest node");
            }

            // Обновляем стадию квеста в БД
            com.human.tapMMO.model.tables.Quest dbQuest = questRepository
                    .findByCharacterIdAndQuest(characterId, questId)
                    .orElseThrow(() -> new RuntimeException("Quest not found"));

            dbQuest.setQuestStage(dbQuest.getQuestStage() + 1);
            questRepository.save(dbQuest);

            // Применяем награды узла
            applyNodeRewards(characterId, nextNode);
        }

        return nextNode;
    }

    /**
     * Применяет награды узла к персонажу
     */
    private void applyNodeRewards(Long characterId, QuestNode node) {
        for (Reward reward : node.getRewards().values()) {
            applyReward(characterId, reward);
        }
    }

    /**
     * Применяет конкретную награду к персонажу
     */
    private void applyReward(Long characterId, Reward reward) {
        Character character = playerService.getCharacterById(characterId)
                .orElseThrow(() -> new RuntimeException("Character not found"));

        switch (reward.getType()) {
            case EXPERIENCE:
                character.setExperience(character.getExperience() + reward.getAmount());
                playerService.updateCharacter(character);
                break;
            case GOLD:
                character.setGold(character.getGold() + reward.getAmount());
                playerService.updateCharacter(character);
                break;
            case REPUTATION:
                character.setReputation(character.getReputation() + reward.getAmount());
                playerService.updateCharacter(character);
                break;
            case ITEM:
                // TODO: Добавить предмет в инвентарь через InventoryService
//                itemService.pickUpItem();
                break;
        }
    }

    /**
     * Завершает квест
     */
    @Transactional
    public void completeQuest(Long characterId, String questId) {
        com.human.tapMMO.model.tables.Quest dbQuest = questRepository
                .findByCharacterIdAndQuest(characterId, questId)
                .orElseThrow(() -> new RuntimeException("Quest not found"));

        Quest questTemplate = questTemplates.get(questId);
        if (questTemplate != null) {
            // Применяем финальные награды квеста
            PlayerContext context = createPlayerContext(characterId);
            for (Reward reward : questTemplate.getRewards().values()) {
                applyReward(characterId, reward);
            }
        }

        // Отмечаем квест как завершенный
        dbQuest.setQuestStage(-1);
        questRepository.save(dbQuest);
    }

    /**
     * Проверяет, завершен ли квест
     */
    public boolean isQuestCompleted(Long characterId, String questId) {
        return questRepository.findByCharacterIdAndQuest(characterId, questId)
                .map(quest -> quest.getQuestStage() == -1)
                .orElse(false);
    }

    /**
     * Получает текущую стадию квеста
     */
    public int getQuestStage(Long characterId, String questId) {
        return questRepository.findByCharacterIdAndQuest(characterId, questId)
                .map(com.human.tapMMO.model.tables.Quest::getQuestStage)
                .orElse(0);
    }

    /**
     * Добавляет шаблон квеста в систему
     */
    public void registerQuestTemplate(Quest questTemplate) {
        questTemplates.put(questTemplate.getId(), questTemplate);
    }

    /**
     * Получает все доступные шаблоны квестов
     */
    public Collection<Quest> getAllQuestTemplates() {
        return questTemplates.values();
    }

    /**
     * Получает доступные для начала квесты для персонажа
     */
    public List<Quest> getAvailableQuests(Long characterId) {
        PlayerContext context = createPlayerContext(characterId);

        return questTemplates.values().stream()
                .filter(quest -> {
                    // Проверяем, что квест не активен и не завершен
                    boolean notActive = questRepository.findByCharacterIdAndQuest(characterId, quest.getId()).isEmpty();
                    // Проверяем требования
                    boolean requirementsMet = quest.getRequirements().stream()
                            .allMatch(req -> req.isMet(context));

                    return notActive && requirementsMet;
                })
                .collect(Collectors.toList());
    }

    /**
     * Отменяет активный квест
     */
    @Transactional
    public void cancelQuest(Long characterId, String questId) {
        com.human.tapMMO.model.tables.Quest dbQuest = questRepository
                .findByCharacterIdAndQuest(characterId, questId)
                .orElseThrow(() -> new RuntimeException("Quest not found"));

        if (dbQuest.getQuestStage() == -1) {
            throw new RuntimeException("Cannot cancel completed quest");
        }

        questRepository.delete(dbQuest);
    }

    /**
     * Получает описание текущего узла квеста
     */
    public String getCurrentQuestDescription(Long characterId, String questId) {
        QuestNode currentNode = getCurrentQuestNode(characterId, questId);
        return currentNode.getDescription();
    }

    /**
     * Получает доступные решения для текущего узла квеста
     */
    public List<QuestDecision> getAvailableDecisions(Long characterId, String questId) {
        QuestNode currentNode = getCurrentQuestNode(characterId, questId);
        PlayerContext context = createPlayerContext(characterId);

        // Фильтруем решения по требованиям следующих узлов
        return currentNode.getDecisions().stream()
                .filter(decision -> {
                    QuestNode nextNode = decision.getNextNode();
                    if (nextNode == null) return true;

                    return nextNode.getRequirements().stream()
                            .allMatch(req -> req.isMet(context));
                })
                .collect(Collectors.toList());
    }
}