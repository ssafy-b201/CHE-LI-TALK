package com.ssafy.devway.domain.chat.repository;

import com.ssafy.devway.domain.chat.entity.Chat;
import com.ssafy.devway.domain.chat.entity.Sentence;
import com.ssafy.devway.domain.member.entity.Member;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByMember(Member member);
    Chat findByChatId(Long ChatId);
    Chat findByMemberAndCreatedAt(Member member, LocalDateTime createdAt);


}
