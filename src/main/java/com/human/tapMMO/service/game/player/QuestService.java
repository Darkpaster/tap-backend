package com.human.tapMMO.service.game.player;

import com.human.tapMMO.runtime.game.quests.*;
import com.human.tapMMO.runtime.game.quests.requirement.ItemRequirement;
import com.human.tapMMO.runtime.game.quests.requirement.LevelRequirement;
import com.human.tapMMO.runtime.game.quests.requirement.QuestRequirement;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestService {
    private Map<String, Quest> quests = new HashMap<>();
    private Map<String, PlayerContext> playerContexts = new HashMap<>();

    // Регистрация нового квеста
    public void registerQuest(Quest quest) {
        quests.put(quest.getId(), quest);
    }

    // Получение квеста по ID
    public Quest getQuest(String questId) {
        return quests.get(questId);
    }

    // Начало квеста игроком
    public boolean startQuest(String playerId, String questId) {
        PlayerContext playerContext = getOrCreatePlayerContext(playerId);
        Quest quest = quests.get(questId);

        if (quest == null) {
            return false;
        }

        // Проверка требований квеста
        for (QuestRequirement req : quest.getRequirements()) {
            if (!req.isMet(playerContext)) {
                return false;
            }
        }

        quest.setStatus(QuestStatus.IN_PROGRESS);
        return true;
    }

    // Выбор решения игроком
    public QuestNode makeDecision(String playerId, String questId, String nodeId, String decisionId) {
        Quest quest = quests.get(questId);
        if (quest == null || quest.getStatus() != QuestStatus.IN_PROGRESS) {
            return null;
        }

        // Находим текущий узел
        QuestNode currentNode = findNode(quest.getRootNode(), nodeId);
        if (currentNode == null) {
            return null;
        }

        // Находим выбранное решение
        QuestDecision selectedDecision = null;
        for (QuestDecision decision : currentNode.getDecisions()) {
            if (decision.getId().equals(decisionId)) {
                selectedDecision = decision;
                break;
            }
        }

        if (selectedDecision == null) {
            return null;
        }

        // Проверка требований для узла, к которому ведет решение
        PlayerContext playerContext = getOrCreatePlayerContext(playerId);
        QuestNode nextNode = selectedDecision.getNextNode();

        for (QuestRequirement req : nextNode.getRequirements()) {
            if (!req.isMet(playerContext)) {
                return null;
            }
        }

        // Если узел является конечным, завершаем квест
        if (nextNode.getDecisions().isEmpty()) {
            completeQuest(playerId, questId);
        }

        // Выдаем награды за прохождение текущего узла
        giveNodeRewards(playerId, currentNode);

        return nextNode;
    }

    // Завершение квеста
    public void completeQuest(String playerId, String questId) {
        PlayerContext playerContext = getOrCreatePlayerContext(playerId);
        Quest quest = quests.get(questId);

        if (quest != null) {
            quest.setStatus(QuestStatus.COMPLETED);
            playerContext.completeQuest(questId);

            // Выдаем награды за квест
            for (Reward reward : quest.getRewards().values()) {
                applyReward(playerContext, reward);
            }
        }
    }

    // Проваленный квест
    public void failQuest(String questId) {
        Quest quest = quests.get(questId);
        if (quest != null) {
            quest.setStatus(QuestStatus.FAILED);
        }
    }

    // Вспомогательные методы
    private PlayerContext getOrCreatePlayerContext(String playerId) {
        if (!playerContexts.containsKey(playerId)) {
            playerContexts.put(playerId, new PlayerContext(playerId));
        }
        return playerContexts.get(playerId);
    }

    private QuestNode findNode(QuestNode node, String nodeId) {
        if (node.getId().equals(nodeId)) {
            return node;
        }

        for (QuestDecision decision : node.getDecisions()) {
            QuestNode found = findNode(decision.getNextNode(), nodeId);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    private void giveNodeRewards(String playerId, QuestNode node) {
        PlayerContext playerContext = getOrCreatePlayerContext(playerId);

        for (Reward reward : node.getRewards().values()) {
            applyReward(playerContext, reward);
        }
    }

    private void applyReward(PlayerContext playerContext, Reward reward) {
        switch (reward.getType()) {
            case EXPERIENCE:
                playerContext.setStat("experience", playerContext.getStat("experience") + reward.getAmount());
                break;
            case GOLD:
                playerContext.setStat("gold", playerContext.getStat("gold") + reward.getAmount());
                break;
            case ITEM:
                playerContext.addItem(reward.getId(), reward.getAmount());
                break;
            case REPUTATION:
                playerContext.setStat("reputation_" + reward.getId(),
                        playerContext.getStat("reputation_" + reward.getId()) + reward.getAmount());
                break;
        }
    }

    // Пример создания простого квеста с разветвлениями
    public Quest createSampleQuest() {
        // Создаем квест
        Quest quest = new Quest("quest001", "Пропавший торговец",
                "Найдите пропавшего торговца и выясните, что с ним случилось.");

        // Добавляем требования к квесту
        quest.setRequirements(List.of(new LevelRequirement(5)));

        // Создаем узлы квеста
        QuestNode startNode = new QuestNode("node001", "Вы встречаете напуганного помощника торговца на дороге.", QuestNodeType.DIALOGUE);

        QuestNode askBanditsNode = new QuestNode("node002", "Помощник упоминает, что видел бандитов неподалеку.", QuestNodeType.DIALOGUE);
        QuestNode askForestNode = new QuestNode("node003", "Помощник рассказывает о странных звуках из леса.", QuestNodeType.DIALOGUE);
        QuestNode ignoreNode = new QuestNode("node004", "Вы решили не помогать и уйти.", QuestNodeType.DIALOGUE);

        QuestNode banditsEncounterNode = new QuestNode("node005", "Вы нашли лагерь бандитов.", QuestNodeType.BATTLE);
        banditsEncounterNode.addRequirement(new LevelRequirement(7));

        QuestNode forestExplorationNode = new QuestNode("node006", "Вы исследуете лес в поисках следов.", QuestNodeType.COLLECTION);
        forestExplorationNode.addRequirement(new ItemRequirement("torch", 1));

        QuestNode fightBanditsNode = new QuestNode("node007", "Вы решаете атаковать бандитов.", QuestNodeType.BATTLE);
        QuestNode negotiateNode = new QuestNode("node008", "Вы пытаетесь договориться с бандитами.", QuestNodeType.DIALOGUE);

        QuestNode foundTraderCaptive = new QuestNode("node009", "Вы нашли торговца связанным в палатке бандитов.", QuestNodeType.DIALOGUE);
        QuestNode banditsRefused = new QuestNode("node010", "Бандиты отказываются сотрудничать и нападают на вас.", QuestNodeType.BATTLE);

        QuestNode foundCaveNode = new QuestNode("node011", "Вы нашли пещеру с следами борьбы.", QuestNodeType.PUZZLE);
        QuestNode foundTraderDead = new QuestNode("node012", "Вы нашли тело торговца, убитого диким зверем.", QuestNodeType.DIALOGUE);

        QuestNode rescueTrader = new QuestNode("node013", "Вы освобождаете торговца и возвращаетесь в город.", QuestNodeType.DIALOGUE);
        QuestNode reportTragedy = new QuestNode("node014", "Вы возвращаетесь, чтобы сообщить печальные новости.", QuestNodeType.DIALOGUE);

        // Добавляем награды
        rescueTrader.addReward(new Reward("gold", RewardType.GOLD, 500));
        rescueTrader.addReward(new Reward("exp", RewardType.EXPERIENCE, 1000));
        rescueTrader.addReward(new Reward("trader_favor", RewardType.REPUTATION, 20));

        reportTragedy.addReward(new Reward("gold", RewardType.GOLD, 200));
        reportTragedy.addReward(new Reward("exp", RewardType.EXPERIENCE, 800));

        // Связываем узлы решениями
        startNode.addDecision(new QuestDecision("decision001", "Спросить о бандитах", askBanditsNode));
        startNode.addDecision(new QuestDecision("decision002", "Спросить о странных звуках из леса", askForestNode));
        startNode.addDecision(new QuestDecision("decision003", "Игнорировать помощника и уйти", ignoreNode));

        askBanditsNode.addDecision(new QuestDecision("decision004", "Пойти искать бандитов", banditsEncounterNode));
        askBanditsNode.addDecision(new QuestDecision("decision005", "Вернуться к началу разговора", startNode));

        askForestNode.addDecision(new QuestDecision("decision006", "Отправиться в лес на поиски", forestExplorationNode));
        askForestNode.addDecision(new QuestDecision("decision007", "Вернуться к началу разговора", startNode));

        banditsEncounterNode.addDecision(new QuestDecision("decision008", "Атаковать бандитов", fightBanditsNode));
        banditsEncounterNode.addDecision(new QuestDecision("decision009", "Попытаться договориться", negotiateNode));

        fightBanditsNode.addDecision(new QuestDecision("decision010", "Обыскать лагерь после боя", foundTraderCaptive));

        negotiateNode.addDecision(new QuestDecision("decision011", "Предложить выкуп", foundTraderCaptive));
        negotiateNode.addDecision(new QuestDecision("decision012", "Угрожать бандитам", banditsRefused));

        banditsRefused.addDecision(new QuestDecision("decision013", "Сражаться с бандитами", foundTraderCaptive));

        forestExplorationNode.addDecision(new QuestDecision("decision014", "Следовать по следам", foundCaveNode));

        foundCaveNode.addDecision(new QuestDecision("decision015", "Исследовать пещеру", foundTraderDead));

        foundTraderCaptive.addDecision(new QuestDecision("decision016", "Освободить торговца", rescueTrader));

        foundTraderDead.addDecision(new QuestDecision("decision017", "Вернуться с плохими новостями", reportTragedy));

        // Устанавливаем корневой узел квеста
        quest.setRootNode(startNode);

        return quest;
    }
}
