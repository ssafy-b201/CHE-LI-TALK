package com.ssafy.devway.domain.chat.repository;

import com.ssafy.devway.domain.chat.entity.Sentence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SentenceRepository extends JpaRepository<Sentence, Long> {

}
