package com.example.demo.security;

import com.example.demo.profile.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

/** Who is making this request, taken from the verified JWT and never from the request body or URL. */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static Long id(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Profile profile)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return profile.getId();
    }

    /** Rejects the request with 403 unless the signed-in user is the owner of the resource. */
    public static void requireSelf(Authentication authentication, Long ownerId) {
        if (!id(authentication).equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own data");
        }
    }
}
