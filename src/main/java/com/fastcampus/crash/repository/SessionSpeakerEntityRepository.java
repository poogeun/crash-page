package com.fastcampus.crash.repository;

import com.fastcampus.crash.model.entity.SessionSpeakerEntity;
import com.fastcampus.crash.model.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionSpeakerEntityRepository extends JpaRepository<SessionSpeakerEntity, Long> {
}
