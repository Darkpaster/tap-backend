package com.human.tapMMO.service.game;

import com.human.tapMMO.repository.GroupMemberRepository;
import com.human.tapMMO.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
}
