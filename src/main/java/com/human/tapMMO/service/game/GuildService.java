package com.human.tapMMO.service.game;

import com.human.tapMMO.model.game.GuildRank;
import com.human.tapMMO.model.tables.Guild;
import com.human.tapMMO.model.tables.GuildBank;
import com.human.tapMMO.model.tables.GuildLog;
import com.human.tapMMO.repository.GuildLogRepository;
import com.human.tapMMO.repository.GuildRepository;
import com.human.tapMMO.runtime.game.actors.player.Player;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuildService {
    private final GuildRepository guildRepository;
    private final PlayerRepository playerRepository;
    private final GuildLogRepository guildLogRepository;

    private final List<GuildRank> MANAGEMENT_RANKS = Arrays.asList(GuildRank.MASTER, GuildRank.OFFICER);

    @Transactional
    public Guild createGuild(String name, String description, Player founder) {
        // Проверяем, нет ли уже гильдии с таким названием
        if (guildRepository.existsByName(name)) {
            throw new IllegalArgumentException("Guild with this name already exists");
        }

        // Проверяем, не состоит ли игрок уже в гильдии
        if (founder.getGuild() != null) {
            throw new IllegalArgumentException("Player is already in a guild");
        }

        // Создаем новую гильдию
        Guild guild = new Guild();
        guild.setName(name);
        guild.setDescription(description);
        guild.setMaster(founder);

        // Сохраняем гильдию
        guild = guildRepository.save(guild);

        // Добавляем основателя в гильдию как мастера
        founder.setGuild(guild);
        founder.setGuildRank(GuildRank.MASTER);
        playerRepository.save(founder);

        // Добавляем запись в лог
        GuildLog log = new GuildLog();
        log.setGuild(guild);
        log.setAction("GUILD_CREATED");
        log.setDetails("Guild created");
        log.setActor(founder);
        guildLogRepository.save(log);

        return guild;
    }

    @Transactional
    public void disbandGuild(Long guildId, Player player) {
        Guild guild = getGuildById(guildId);

        // Проверяем права
        if (!player.equals(guild.getMaster())) {
            throw new ForbiddenException("Only guild master can disband the guild");
        }

        // Очищаем привязку к гильдии у всех участников
        for (Player member : guild.getMembers()) {
            member.setGuild(null);
            member.setGuildRank(null);
            member.setGuildContribution(0);
            playerRepository.save(member);
        }

        // Удаляем гильдию
        guildRepository.delete(guild);
    }

    @Transactional
    public Guild updateGuild(Long guildId, Guild guildDetails, Player actor) {
        Guild guild = getGuildById(guildId);

        // Проверяем права
        if (!canManageGuild(actor, guild)) {
            throw new ForbiddenException("Insufficient permissions to update guild");
        }

        // Обновляем данные гильдии
        if (guildDetails.getDescription() != null) {
            guild.setDescription(guildDetails.getDescription());
        }

        if (guildDetails.getMessageOfTheDay() != null) {
            guild.setMessageOfTheDay(guildDetails.getMessageOfTheDay());
        }

        if (guildDetails.getGuildCrestUrl() != null) {
            guild.setGuildCrestUrl(guildDetails.getGuildCrestUrl());
        }

        // Сохраняем изменения
        guild = guildRepository.save(guild);

        // Добавляем запись в лог
        GuildLog log = new GuildLog();
        log.setGuild(guild);
        log.setAction("GUILD_UPDATED");
        log.setDetails("Guild details updated");
        log.setActor(actor);
        guildLogRepository.save(log);

        return guild;
    }

    @Transactional
    public void addMember(Long guildId, Long playerId, Player actor) {
        Guild guild = getGuildById(guildId);
        Player playerToAdd = playerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Player not found"));

        // Проверяем права
        if (!canManageGuild(actor, guild)) {
            throw new ForbiddenException("Insufficient permissions to add members");
        }

        // Проверяем, не состоит ли игрок уже в гильдии
        if (playerToAdd.getGuild() != null) {
            throw new IllegalArgumentException("Player is already in a guild");
        }

        // Проверяем, не превышено ли максимальное количество участников
        if (guild.getMembers().size() >= guild.getMaxMembers()) {
            throw new IllegalArgumentException("Guild has reached maximum member capacity");
        }

        // Добавляем игрока в гильдию
        playerToAdd.setGuild(guild);
        playerToAdd.setGuildRank(GuildRank.RECRUIT);
        playerRepository.save(playerToAdd);

        // Добавляем запись в лог
        GuildLog log = new GuildLog();
        log.setGuild(guild);
        log.setAction("MEMBER_ADDED");
        log.setDetails("New member joined the guild");
        log.setActor(actor);
        log.setTarget(playerToAdd);
        guildLogRepository.save(log);
    }

    @Transactional
    public void removeMember(Long guildId, Long playerId, Player actor) {
        Guild guild = getGuildById(guildId);
        Player playerToRemove = playerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Player not found"));

        // Проверяем, что игрок состоит в этой гильдии
        if (playerToRemove.getGuild() == null || !playerToRemove.getGuild().getId().equals(guildId)) {
            throw new IllegalArgumentException("Player is not a member of this guild");
        }

        // Нельзя исключить мастера гильдии
        if (playerToRemove.equals(guild.getMaster())) {
            throw new IllegalArgumentException("Cannot remove the guild master");
        }

        // Проверяем права: можно удалить себя или если есть права управления
        boolean canRemove = actor.equals(playerToRemove) || canManageGuild(actor, guild);

        // Офицер не может исключить другого офицера
        if (actor.getGuildRank() == GuildRank.OFFICER && playerToRemove.getGuildRank() == GuildRank.OFFICER) {
            canRemove = false;
        }

        if (!canRemove) {
            throw new ForbiddenException("Insufficient permissions to remove this member");
        }

        // Удаляем игрока из гильдии
        playerToRemove.setGuild(null);
        playerToRemove.setGuildRank(null);
        playerToRemove.setGuildContribution(0);
        playerRepository.save(playerToRemove);

        // Добавляем запись в лог
        String action = actor.equals(playerToRemove) ? "MEMBER_LEFT" : "MEMBER_KICKED";
        String details = actor.equals(playerToRemove) ? "Member left the guild" : "Member was kicked from the guild";

        GuildLog log = new GuildLog();
        log.setGuild(guild);
        log.setAction(action);
        log.setDetails(details);
        log.setActor(actor);
        log.setTarget(playerToRemove);
        guildLogRepository.save(log);
    }

    @Transactional
    public void changeRank(Long guildId, Long playerId, GuildRank newRank, Player actor) {
        Guild guild = getGuildById(guildId);
        Player targetPlayer = playerRepository.findById(playerId)
                .orElseThrow(() -> new NotFoundException("Player not found"));

        // Проверяем, что игрок состоит в этой гильдии
        if (targetPlayer.getGuild() == null || !targetPlayer.getGuild().getId().equals(guildId)) {
            throw new IllegalArgumentException("Player is not a member of this guild");
        }

        // Проверяем права
        if (!actor.equals(guild.getMaster())) {
            throw new ForbiddenException("Only guild master can change ranks");
        }

        // Мастер не может изменить свой ранг
        if (targetPlayer.equals(guild.getMaster()) && newRank != GuildRank.MASTER) {
            throw new IllegalArgumentException("Cannot change the rank of guild master");
        }

        // Меняем ранг
        targetPlayer.setGuildRank(newRank);
        playerRepository.save(targetPlayer);

        // Если меняется ранг на мастера, то старый мастер становится офицером
        if (newRank == GuildRank.MASTER && !targetPlayer.equals(guild.getMaster())) {
            Player oldMaster = guild.getMaster();
            oldMaster.setGuildRank(GuildRank.OFFICER);
            playerRepository.save(oldMaster);

            guild.setMaster(targetPlayer);
            guildRepository.save(guild);
        }

        // Добавляем запись в лог
        GuildLog log = new GuildLog();
        log.setGuild(guild);
        log.setAction("RANK_CHANGED");
        log.setDetails("Member rank changed to " + newRank);
        log.setActor(actor);
        log.setTarget(targetPlayer);
        guildLogRepository.save(log);
    }

    public List<Player> getGuildMembers(Long guildId) {
        // Проверяем существование гильдии
        if (!guildRepository.existsById(guildId)) {
            throw new NotFoundException("Guild not found");
        }

        return playerRepository.findByGuildId(guildId);
    }

    public Guild getGuildById(Long guildId) {
        return guildRepository.findById(guildId)
                .orElseThrow(() -> new NotFoundException("Guild not found"));
    }

    public Guild getGuildByName(String name) {
        return guildRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Guild not found"));
    }

    public List<Guild> searchGuilds(String searchTerm) {
        return guildRepository.searchGuildsByName(searchTerm);
    }

    @Transactional
    public void addGuildExperience(Long guildId, int experience) {
        Guild guild = getGuildById(guildId);
        guild.setExperience(guild.getExperience() + experience);

        // Проверяем, нужно ли повысить уровень гильдии
        int experienceForNextLevel = calculateExperienceForNextLevel(guild.getLevel());
        if (guild.getExperience() >= experienceForNextLevel) {
            guild.setLevel(guild.getLevel() + 1);
            guild.setExperience(guild.getExperience() - experienceForNextLevel);

            // Увеличиваем максимальное количество участников при повышении уровня
            guild.setMaxMembers(Math.min(100, guild.getMaxMembers() + 5));

            // Добавляем запись в лог о повышении уровня
            GuildLog log = new GuildLog();
            log.setGuild(guild);
            log.setAction("LEVEL_UP");
            log.setDetails("Guild reached level " + guild.getLevel());
            guildLogRepository.save(log);
        }

        guildRepository.save(guild);
    }

    private int calculateExperienceForNextLevel(int currentLevel) {
        // Формула расчета опыта для следующего уровня гильдии
        return 1000 * currentLevel * currentLevel;
    }

    @Transactional
    public void updateGuildBank(Long guildId, Long itemId, int quantity, int tabId, int slotId, Player actor) {
        Guild guild = getGuildById(guildId);

        // Проверяем права
        if (!canManageGuild(actor, guild)) {
            throw new ForbiddenException("Insufficient permissions to manage guild bank");
        }

        // Ищем предмет в банке
        GuildBank bankItem = guild.getBankItems().stream()
                .filter(item -> item.getTabId().equals(tabId) && item.getSlotId().equals(slotId))
                .findFirst()
                .orElse(null);

        if (bankItem == null) {
            // Создаем новую запись, если предмета в этом слоте нет
            if (quantity > 0) {
                bankItem = new GuildBank();
                bankItem.setGuild(guild);
                bankItem.setItemId(itemId);
                bankItem.setQuantity(quantity);
                bankItem.setTabId(tabId);
                bankItem.setSlotId(slotId);
                guild.getBankItems().add(bankItem);
            }
        } else {
            // Обновляем количество или удаляем запись
            if (quantity <= 0) {
                guild.getBankItems().remove(bankItem);
            } else {
                bankItem.setItemId(itemId);
                bankItem.setQuantity(quantity);
            }
        }

        guildRepository.save(guild);

        // Добавляем запись в лог
        GuildLog log = new GuildLog();
        log.setGuild(guild);
        log.setAction("BANK_UPDATED");
        log.setDetails("Guild bank item updated: itemId=" + itemId + ", quantity=" + quantity);
        log.setActor(actor);
        guildLogRepository.save(log);
    }

    public boolean canManageGuild(Player player, Guild guild) {
        return player.getGuild() != null &&
                player.getGuild().getId().equals(guild.getId()) &&
                MANAGEMENT_RANKS.contains(player.getGuildRank());
    }
}