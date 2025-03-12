package fiap.hackaton.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationEvent {

    @JsonProperty("Email")
    private String Email;

    @JsonProperty("Message")
    private String Message;

    public NotificationEvent() {
    }

    public NotificationEvent(String email, String message) {
        Email = email;
        Message = message;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
