package com.ssafy.devway.domain.attend.repository;

import com.ssafy.devway.domain.attend.entity.Attend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendRepository extends JpaRepository<Attend, Long> {

}