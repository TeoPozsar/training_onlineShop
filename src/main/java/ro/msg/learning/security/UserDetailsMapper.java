
package ro.msg.learning.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;

public class UserDetailsMapper {

    private UserDetailsMapper() {
    }

    public static UserDetails toUserDetails(
            String username,
            String passwordHash,
            String role) {

        return User.withUsername(username)
                .password(passwordHash)
                .roles(role)
                .build();
    }
}
