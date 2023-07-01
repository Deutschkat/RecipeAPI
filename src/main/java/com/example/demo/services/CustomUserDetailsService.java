package com.example.demo.services;

import com.example.demo.models.CustomUserDetails;
import com.example.demo.models.Recipe;
import com.example.demo.models.Review;
import com.example.demo.models.Role;
import com.example.demo.repos.UserRepo;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.CascadeType;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails optionalUser = userRepo.findByUsername(username);

        if (optionalUser == null) {
            throw new UsernameNotFoundException(username + " is not a valid username! Check for typos and try again.");
        }

        return optionalUser;
    }

    @Transactional(readOnly = true)
    public CustomUserDetails getUserByUserId(Long userId) throws EntityNotFoundException {
        Optional<CustomUserDetails> optional = userRepo.findById(userId);
        CustomUserDetails user;
        if (optional.isEmpty()) {
            throw new EntityNotFoundException("User not found.");
        } else {
            user = optional.get();
        }
        //call unproxy() to ensure all related entities are loaded—no lazy load exceptions.
        return (CustomUserDetails) Hibernate.unproxy(user);
    }

    @Transactional(readOnly = true)
    public CustomUserDetails getUser (String username) throws EntityNotFoundException {
        return userRepo.findByUsername(username);
    }

    public CustomUserDetails createNewUser (CustomUserDetails userDetails) {
        userDetails.setId(null);
        userDetails.getAuthorities().forEach(a -> a.setId(null));

        //override or set user settings to correct values
        userDetails.setAccountNonExpired(true);
        userDetails.setAccountNonLocked(true);
        userDetails.setCredentialsNonExpired(true);
        userDetails.setEnabled(true);
        userDetails.setAuthorities(Collections.singletonList(new Role(Role.Roles.ROLE_USER)));

        checkPassword(userDetails.getPassword());
        userDetails.setPassword(encoder.encode(userDetails.getPassword()));
        try {
            return userRepo.save(userDetails);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    private void checkPassword (String password) {
        if (password == null) {
            throw new IllegalStateException("You must set a password");
        }
        if (password.length() < 6) {
            throw new IllegalStateException("Password is too short. Must be longer than 6 characters");
        }
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Recipe> recipes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;
}