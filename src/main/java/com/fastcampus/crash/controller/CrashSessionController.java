package com.fastcampus.crash.controller;

import com.fastcampus.crash.model.crashsession.CrashSession;
import com.fastcampus.crash.model.crashsession.CrashSessionPatchRequestBody;
import com.fastcampus.crash.model.crashsession.CrashSessionPostRequestBody;
import com.fastcampus.crash.model.sessionspeaker.SessionSpeaker;
import com.fastcampus.crash.model.sessionspeaker.SessionSpeakerPatchRequestBody;
import com.fastcampus.crash.model.sessionspeaker.SessionSpeakerPostRequestBody;
import com.fastcampus.crash.service.CrashSessionService;
import com.fastcampus.crash.service.SessionSpeakerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/crash-sessions")
public class CrashSessionController {

  @Autowired private CrashSessionService crashSessionService;

  @GetMapping
  public ResponseEntity<List<CrashSession>> getCrashSessions() {
    var crashSessions = crashSessionService.getCrashSessions();
    return ResponseEntity.ok(crashSessions);
  }

  @GetMapping("/{sessionId}")
  public ResponseEntity<CrashSession> getCrashSessionBySpeakerId(@PathVariable Long sessionId) {
    var crashSession = crashSessionService.getCrashSessionBySessionId(sessionId);
    return ResponseEntity.ok(crashSession);
  }

  @PostMapping
  public ResponseEntity<CrashSession> createCrashSession(
      @Valid @RequestBody CrashSessionPostRequestBody crashSessionPostRequestBody) {

    var crashSession = crashSessionService.createCrashSession(crashSessionPostRequestBody);
    return ResponseEntity.ok(crashSession);
  }

  @PatchMapping("/{sessionId}")
  public ResponseEntity<CrashSession> updateCrashSession(
      @PathVariable Long sessionId,
      @RequestBody CrashSessionPatchRequestBody crashSessionPatchRequestBody) {
    var crashSession = crashSessionService.updateCrashSession(sessionId, crashSessionPatchRequestBody);
    return ResponseEntity.ok(crashSession);
  }

  @DeleteMapping("/{sessionId}")
  public ResponseEntity<Void> deleteCrashSession(@PathVariable Long sessionId) {
    crashSessionService.deleteCrashSession(sessionId);
    return ResponseEntity.noContent().build();
  }
}
