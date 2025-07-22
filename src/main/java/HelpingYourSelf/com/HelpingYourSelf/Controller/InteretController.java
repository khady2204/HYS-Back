package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Interet;
import HelpingYourSelf.com.HelpingYourSelf.Repository.InteretRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interets")
@RequiredArgsConstructor
public class InteretController {

    private final InteretRepository interetRepository;

    // ✅ POST /api/interets — Ajouter un intérêt
    @PostMapping
    public ResponseEntity<Interet> createInteret(@RequestBody Interet interet) {
        Interet saved = interetRepository.save(interet);
        return ResponseEntity.ok(saved);
    }

    // ✅ GET /api/interets — Liste rapide
    @GetMapping
    public ResponseEntity<List<Interet>> getAll() {
        return ResponseEntity.ok(interetRepository.findAll());
    }

    // ✅ GET /api/listeinterets — alias personnalisé
    @GetMapping("/listeinterets")
    public ResponseEntity<List<Interet>> listeInterets() {
        return ResponseEntity.ok(interetRepository.findAll());
    }
}
