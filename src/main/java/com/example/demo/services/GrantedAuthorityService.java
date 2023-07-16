package com.example.demo.services;

import com.example.demo.models.Role;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class GrantedAuthorityService {

    public ArrayList<Role> getGrantedAuthoritiesByUserId(Long userId) {
        return new ArrayList<>();
    }
}
