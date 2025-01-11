package com.fastcampus.crash.model.crashsession;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;

public record CrashSessionPatchRequestBody(
         String title,
         String body,
         CrashSessionCategory category,
         ZonedDateTime dateTime,
         Long speakerId) {}
