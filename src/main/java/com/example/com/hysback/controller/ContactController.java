package com.example.hysback.controller;

import com.example.hysback.entity.Contact;
import com.example.hysback.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin("*") // pour permettre les appels front
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    // 🔍 GET : tous les contacts favoris
    @GetMapping
    public List<Contact> getFavoris() {
        return contactRepository.findAll().stream()
                .filter(Contact::isFavori)
                .toList();
    }

    // ➕ POST : ajouter un contact
    @PostMapping
    public Contact addContact(@RequestBody Contact contact) {
        return contactRepository.save(contact);
    }

    // ❌ DELETE : supprimer un contact par id
    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable Long id) {
        contactRepository.deleteById(id);
    }
}
