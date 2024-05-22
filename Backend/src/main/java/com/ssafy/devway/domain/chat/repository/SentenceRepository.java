package com.ssafy.devway.domain.chat.repository;

import com.ssafy.devway.domain.chat.entity.Sentence;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

	@Query("SELECT s FROM Sentence s WHERE s.chat.chatId = :chatId")
	List<Sentence> findByChatId(Long chatId);

	Sentence findBySentenceId(Long sentenceId);

}
