package com.fastcampus.crash.model.crashsession;

import com.fasterxml.jackson.annotation.JsonInclude;

// registrationId 가 null 인 경우 해당 필드 출력되지 않음
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CrashSessionRegistrationStatus(
        Long sessionId,
        boolean isRegistered,
        Long registrationId
) {}
