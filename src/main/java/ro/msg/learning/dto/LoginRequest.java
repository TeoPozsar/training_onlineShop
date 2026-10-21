package ro.msg.learning.dto;

public record LoginRequest(
        String username,
        String password
) {
}
