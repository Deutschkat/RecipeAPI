package com.example.demo.services;

import com.example.demo.models.Recipe;
import com.example.demo.NoSuchRecipeException;
import com.example.demo.repos.RecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    @Autowired
    RecipeRepo recipeRepo;

    @Transactional
    public Recipe createNewRecipe(Recipe recipe, String username) throws IllegalStateException {
        recipe.validate();
        recipe.setUsername(username);
        recipe = recipeRepo.save(recipe);
        recipe.generateLocationURI();
        return recipe;
    }

    public Recipe getRecipeById(Long id) throws NoSuchRecipeException {
        Optional<Recipe> recipeOptional = recipeRepo.findById(id);

        if (recipeOptional.isEmpty()) {
            throw new NoSuchRecipeException("No recipe with ID " + id + " could be found.");
        }

        Recipe recipe = recipeOptional.get();
        recipe.generateLocationURI();
        return recipe;
    }

    public List<Recipe> getRecipesByName(String name) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByNameContainingIgnoreCase(name);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name.");
        }

        return matchingRecipes;
    }

    public List<Recipe> getAllRecipes() throws NoSuchRecipeException {
        List<Recipe> recipes = recipeRepo.findAll();

        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("There are no recipes yet :( feel free to add one though");
        }
        return recipes;
    }

    @Transactional
    public Recipe deleteRecipeById(Long id) throws NoSuchRecipeException {
        try {
            Recipe recipe = getRecipeById(id);
            recipeRepo.deleteById(id);
            return recipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException(e.getMessage() + " Could not delete.");
        }
    }

    @Transactional
    public Recipe updateRecipe(Recipe recipe, boolean forceIdCheck) throws NoSuchRecipeException {
        try {
            if (forceIdCheck) {
                getRecipeById(recipe.getId());
            }
            recipe.validate();
            Recipe savedRecipe = recipeRepo.save(recipe);
            savedRecipe.generateLocationURI();
            return savedRecipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException("The recipe you passed in did not have an ID found in the database." +
                    " Double check that it is correct. Or maybe you meant to POST a recipe not PATCH one.");
        }
    }

// Part # 3 - find by name and difficulty
    public List<Recipe> getRecipesByNameAndMaxDifficultyRating(String name, Integer maxDifficultyRating) throws NoSuchRecipeException{
        List<Recipe> matchingRecipes = recipeRepo.findByNameContainingIgnoreCaseAndDifficultyRatingLessThanEqual(name, maxDifficultyRating);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name and maximum difficulty rating.");
        }

        return matchingRecipes;
    }


    public List<Recipe> getRecipesByMinAvgRating(double minAvgRating){
        List<Recipe> allRecipes = recipeRepo.findAll();

        return allRecipes.stream()
                .filter(recipe -> recipe.getAverageRating() >= minAvgRating)
                .collect(Collectors.toList());

    }

    public List<Recipe> getRecipesByUsername(String username) throws NoSuchRecipeException {
        List<Recipe> userRecipes = recipeRepo.findByUsername(username);

        if (userRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found for the username: " + username);
        }

        return userRecipes;
    }

}