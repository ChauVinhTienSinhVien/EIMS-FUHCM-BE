package com.fullsnacke.eimsfuhcmbe.util;

import com.fullsnacke.eimsfuhcmbe.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public class SecurityUntil {

    public static Optional<User> getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return Optional.of((User) principal);
            } else {
                throw new IllegalArgumentException("User not found");
            }
        }
        return Optional.empty();
    }
}
