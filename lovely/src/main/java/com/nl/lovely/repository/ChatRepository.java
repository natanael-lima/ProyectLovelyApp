package com.nl.lovely.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nl.lovely.entity.Chat;

@Repository
public interface ChatRepository extends JpaRepository<Chat,Long>{
	 Optional<Chat> findByMatchId(Long matchId);
}
