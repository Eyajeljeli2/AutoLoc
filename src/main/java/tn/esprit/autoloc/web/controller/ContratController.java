package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.web.dto.ContratDTO;

@Tag(name = "Contrats", description = "Gestion des contrats de location")
@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
public class ContratController {

    private final IContratService contratService;

    @Operation(summary = "Lister tous les contrats")
    @ApiResponse(responseCode = "200", description = "Liste des contrats récupérée avec succès")
    @GetMapping
    public List<ContratDTO> findAll() {
        return contratService.findAll();
    }

    @Operation(summary = "Consulter un contrat par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @GetMapping("/{id}")
    public ContratDTO findById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        return contratService.findById(id);
    }

    @Operation(summary = "Créer un nouveau contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou présence d'un identifiant")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratDTO create(@Valid @RequestBody ContratDTO contrat) {
        return contratService.create(contrat);
    }

    @Operation(summary = "Modifier un contrat existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de modification invalides"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @PutMapping("/{id}")
    public ContratDTO update(@Parameter(description = "Identifiant du contrat") @PathVariable Long id,
            @Valid @RequestBody ContratDTO contrat) {
        return contratService.update(id, contrat);
    }

    @Operation(summary = "Supprimer un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contrat supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        contratService.deleteById(id);
    }

    @Operation(summary = "Affecter un contrat à une réservation (Atelier 7)",
               description = "Crée ou associe un contrat à la réservation correspondant aux dates début et fin spécifiées")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat affecté avec succès"),
            @ApiResponse(responseCode = "400", description = "Dates invalides"),
            @ApiResponse(responseCode = "404", description = "Aucune réservation trouvée pour cette période")
    })
    @PostMapping("/affecter-reservation")
    public ContratDTO affecterContratAReservation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateSignature,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return contratService.affecterContratAReservation(dateSignature, dateDebut, dateFin);
    }
}
