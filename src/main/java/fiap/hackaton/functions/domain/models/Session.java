package fiap.hackaton.functions.domain.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import fiap.hackaton.functions.domain.enums.FileStatus;

import java.util.UUID;

public class Session {
    @JsonProperty("SessionId")
    private UUID SessionId;

    @JsonProperty("Email")
    private String Email;

    @JsonProperty("Status")
    private FileStatus Status;

    public Session() {
    }

    public Session(UUID SessionId, String email, FileStatus Status) {
        this.SessionId = SessionId;
        this.Email = email;
        this.Status = Status;
    }

    public UUID getSessionId() {
        return SessionId;
    }

    public void setSessionId(UUID sessionId) {
        SessionId = sessionId;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public FileStatus getStatus() {
        return Status;
    }

    public void setStatus(FileStatus status) {
        Status = status;
    }
}