package tacheA;
import java.time.LocalDateTime;

public class Notification {
    private String type;
    private String message;
    private LocalDateTime date;
    private boolean lu;
    private User user; // Association à un utilisateur

    // Constructeur par défaut
    public Notification() {}

    // Constructeur avec paramètres
    public Notification(String type, String message, LocalDateTime date, boolean lu, User user) {
        this.type = type;
        this.message = message;
        this.date = date;
        this.lu = lu;
        this.user = user;
    }

    // Getters
    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public boolean isLu() {
        return lu;
    }

    public User getUser() {
        return user;
    }

    // Setters
    public void setType(String type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
