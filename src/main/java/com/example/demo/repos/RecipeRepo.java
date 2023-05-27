package com.example.demo.repos;

import com.example.demo.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepo extends JpaRepository<Recipe, Long> {

    List<Recipe> findByNameContainingIgnoreCase(String name);

    List<Recipe> findByNameContainingIgnoreCaseAndDifficultyRatingLessThanEqual(String name, Integer difficultyRating);


    List<Recipe> findByUsername(String username);

}