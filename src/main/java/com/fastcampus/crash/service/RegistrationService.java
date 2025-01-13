package com.fastcampus.crash.service;

import com.fastcampus.crash.exception.registration.RegistrationAlreadyExistsException;
import com.fastcampus.crash.exception.registration.RegistrationNotFoundException;
import com.fastcampus.crash.model.crashsession.CrashSessionRegistrationStatus;
import com.fastcampus.crash.model.entity.RegistrationEntity;
import com.fastcampus.crash.model.entity.UserEntity;
import com.fastcampus.crash.model.registration.Registration;
import com.fastcampus.crash.model.registration.RegistrationPostRequestBody;
import com.fastcampus.crash.repository.RegistrationEntityRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {

  @Autowired private RegistrationEntityRepository registrationEntityRepository;
  @Autowired private CrashSessionService crashSessionService;

  public List<Registration> getRegistrationsByCurrentUser(UserEntity currentUser) {
    var registrationEntities = registrationEntityRepository.findByUser(currentUser);
    return registrationEntities.stream().map(Registration::from).toList();
  }

  public Registration getRegistrationByRegistrationIdByCurrentUser(
      Long registrationId, UserEntity currentUser) {
    var registrationEntity =
        getRegistrationEntityByRegistrationIdAndUserEntity(registrationId, currentUser);
    return Registration.from(registrationEntity);
  }

  public RegistrationEntity getRegistrationEntityByRegistrationIdAndUserEntity(
      Long registrationId, UserEntity userEntity) {
    return registrationEntityRepository
        .findByRegistrationIdAndUser(registrationId, userEntity)
        .orElseThrow(() -> new RegistrationNotFoundException(registrationId, userEntity));
  }

  public Registration createRegistrationByCurrentUser(
      RegistrationPostRequestBody registrationPostRequestBody, UserEntity currentUser) {

    var crashSessionEntity =
        crashSessionService.getCrashSessionEntityBySessionId(
            registrationPostRequestBody.sessionId());

    // 이미 세션 등록 한 경우
    registrationEntityRepository
        .findByUserAndSession(currentUser, crashSessionEntity)
        .ifPresent(
            registrationEntity -> {
              throw new RegistrationAlreadyExistsException(
                  registrationEntity.getRegistrationId(), currentUser);
            });

    var registrationEntity = RegistrationEntity.of(currentUser, crashSessionEntity);
    return Registration.from(registrationEntityRepository.save(registrationEntity));
  }

  public void deleteRegistrationByRegistrationIdAndCurrentUser(
      Long registrationId, UserEntity currentUser) {
    var registrationEntity =
        getRegistrationEntityByRegistrationIdAndUserEntity(registrationId, currentUser);
    registrationEntityRepository.delete(registrationEntity);
  }

  public CrashSessionRegistrationStatus getCrashSessionRegistrationStatusBySessionIdAndCurrentUser(
      Long sessionId, UserEntity currentUser) {
    var crashSessionEntity = crashSessionService.getCrashSessionEntityBySessionId(sessionId);
    var registrationEntity = registrationEntityRepository.findByUserAndSession(currentUser, crashSessionEntity);
    return new CrashSessionRegistrationStatus(
            sessionId,
            registrationEntity.isPresent(), // 존재하면 true
            registrationEntity.map(RegistrationEntity::getRegistrationId).orElse(null)); // 존재하면 registrationId 없으면 null
  }
}
