package com.ssafy.devway.domain.chat.repository;

import com.ssafy.devway.domain.chat.entity.Chat;
import com.ssafy.devway.domain.member.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByMember(Member member);
    Chat findByChatId(Long ChatId);

}
