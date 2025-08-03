import java.time.LocalTime;

public class Task {

    public static Integer counterForTask = -1;
    Integer id;
    private String description;
    private String status;
    private LocalTime createdAt;
    private LocalTime updatedAt;

    public String toJson() {
        return String.format(
                "{ \"id\": %d, \"description\": \"%s\", \"status\": \"%s\", \"createdAt\": \"%s\", \"updatedAt\": \"%s\" },",
                id,
                escapeJson(description),
                escapeJson(status),
                escapeJson(createdAt.toString()),
                escapeJson(updatedAt.toString()));
    }

    private String escapeJson(String str) {
        return str.replace("\"", "\\\""); // escape quotes only
    }

    public Task(Integer id, String description, String status, LocalTime createdAt, LocalTime updatedAt2) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt2;

    }

    public Task() {
        Task.counterForTask++;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task [id=" + id + ", description=" + description + ", status=" + status + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt + "]";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
