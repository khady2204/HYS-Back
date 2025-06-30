package com.example.hysback.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    private String prenom; // ✅ Ajouté ici

    private String photoUrl; // Lien de la photo

    private int compatibilite; // % entre 0 et 100

    private int distanceKm;

    private String interetsCommuns;
}
