package com.human.tapMMO.service.game.social;

import com.human.tapMMO.model.tables.FriendRequest;
import com.human.tapMMO.repository.CharacterRepository;
import com.human.tapMMO.repository.FriendRequestRepository;
import com.human.tapMMO.runtime.game.social.friends.FriendRequestStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final CharacterRepository characterRepository;
    private final FriendRequestRepository friendRequestRepository;

    /**
     * Отправка заявки в друзья
     */
    @Transactional
    public FriendRequestDto sendFriendRequest(Long senderId, Long receiverId) {
        // Проверка, не отправляет ли пользователь заявку самому себе
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("Нельзя отправить заявку в друзья самому себе");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь-отправитель не найден"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь-получатель не найден"));

        // Проверка, существует ли уже заявка между этими пользователями
        boolean requestExists = friendRequestRepository.existsBySenderAndReceiverAndStatus(
                sender, receiver, FriendRequestStatus.PENDING);

        if (requestExists) {
            throw new IllegalStateException("Заявка в друзья уже отправлена и ждет рассмотрения");
        }

        // Проверка, являются ли пользователи уже друзьями
        if (sender.getFriends().contains(receiver)) {
            throw new IllegalStateException("Пользователи уже являются друзьями");
        }

        // Создание заявки
        FriendRequest friendRequest = FriendRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .status(FriendRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        FriendRequest savedRequest = friendRequestRepository.save(friendRequest);
        return convertToDto(savedRequest);
    }

    /**
     * Принятие заявки в друзья
     */
    @Transactional
    public void acceptFriendRequest(Long userId, Long requestId) {
        FriendRequest request = getFriendRequestForUser(userId, requestId);

        // Обновление статуса заявки
        request.setStatus(FriendRequestStatus.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());
        friendRequestRepository.save(request);

        // Добавление пользователей в списки друзей друг друга
        User sender = request.getSender();
        User receiver = request.getReceiver();

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        userRepository.save(sender);
        userRepository.save(receiver);
    }

    /**
     * Отклонение заявки в друзья
     */
    @Transactional
    public void rejectFriendRequest(Long userId, Long requestId) {
        FriendRequest request = getFriendRequestForUser(userId, requestId);

        request.setStatus(FriendRequestStatus.REJECTED);
        request.setUpdatedAt(LocalDateTime.now());
        friendRequestRepository.save(request);
    }

    /**
     * Удаление друга
     */
    @Transactional
    public void removeFriend(Long userId, Long friendId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException("Друг не найден"));

        if (!user.getFriends().contains(friend)) {
            throw new IllegalStateException("Пользователи не являются друзьями");
        }

        // Удаление из списков друзей обоих пользователей
        user.getFriends().remove(friend);
        friend.getFriends().remove(user);

        userRepository.save(user);
        userRepository.save(friend);
    }

    /**
     * Получение списка друзей пользователя
     */
    @Transactional(readOnly = true)
    public List<UserDto> getUserFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        return user.getFriends().stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение входящих заявок в друзья
     */
    @Transactional(readOnly = true)
    public List<FriendRequestDto> getIncomingFriendRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        List<FriendRequest> requests = friendRequestRepository.findByReceiverAndStatus(
                user, FriendRequestStatus.PENDING);

        return requests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Получение исходящих заявок в друзья
     */
    @Transactional(readOnly = true)
    public List<FriendRequestDto> getOutgoingFriendRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        List<FriendRequest> requests = friendRequestRepository.findBySenderAndStatus(
                user, FriendRequestStatus.PENDING);

        return requests.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Поиск пользователей по имени для добавления в друзья
     */
    @Transactional(readOnly = true)
    public List<UserDto> searchUsers(String query) {
        List<User> users = userRepository.searchUsers(query);

        return users.stream()
                .map(this::convertToUserDto)
                .collect(Collectors.toList());
    }

    // Вспомогательные методы
    private FriendRequest getFriendRequestForUser(Long userId, Long requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка в друзья не найдена"));

        // Проверка, что текущий пользователь является получателем заявки
        if (!request.getReceiver().getId().equals(userId)) {
            throw new IllegalStateException("У вас нет доступа к этой заявке");
        }

        if (request.getStatus() != FriendRequestStatus.PENDING) {
            throw new IllegalStateException("Заявка уже обработана");
        }

        return request;
    }

    private UserDto convertToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .characterName(user.getCharacterName())
                .online(user.getLastOnline() > System.currentTimeMillis() - 300000) // 5 минут для статуса онлайн
                .lastOnline(user.getLastOnline())
                .friendCount(user.getFriendCount())
                .build();
    }

    private FriendRequestDto convertToDto(FriendRequest request) {
        return FriendRequestDto.builder()
                .id(request.getId())
                .sender(convertToUserDto(request.getSender()))
                .receiver(convertToUserDto(request.getReceiver()))
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }
}

