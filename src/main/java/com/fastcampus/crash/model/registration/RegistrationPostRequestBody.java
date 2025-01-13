package com.fastcampus.crash.model.registration;

import com.fastcampus.crash.model.crashsession.CrashSessionCategory;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record RegistrationPostRequestBody(
       @NotNull Long sessionId) {}
