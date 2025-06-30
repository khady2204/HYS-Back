package com.example.hysback.controller;

import com.example.hysback.entity.Suggestion;
import com.example.hysback.repository.SuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
@CrossOrigin("*")
public class SuggestionController {

    @Autowired
    private SuggestionRepository suggestionRepository;

    // 📋 Lister toutes les suggestions
    @GetMapping
    public List<Suggestion> getAllSuggestions() {
        return suggestionRepository.findAll();
    }

    // ➕ Ajouter une suggestion (utile pour les tests)
    @PostMapping
    public Suggestion addSuggestion(@RequestBody Suggestion suggestion) {
        return suggestionRepository.save(suggestion);
    }

    // ❌ Supprimer une suggestion
    @DeleteMapping("/{id}")
    public void deleteSuggestion(@PathVariable Long id) {
        suggestionRepository.deleteById(id);
    }
}
