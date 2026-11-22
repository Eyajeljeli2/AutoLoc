package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.web.dto.ReservationDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;

@Tag(name = "Reservations", description = "Gestion des réservations de véhicules")
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final IReservationService reservationService;

    @Operation(summary = "Lister toutes les réservations")
    @ApiResponse(responseCode = "200", description = "Liste des réservations récupérée avec succès")
    @GetMapping
    public List<ReservationDTO> findAll() {
        return reservationService.findAll();
    }

    @Operation(summary = "Consulter une réservation par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    @GetMapping("/{id}")
    public ReservationDTO findById(@Parameter(description = "Identifiant de la réservation") @PathVariable Long id) {
        return reservationService.findById(id);
    }

    @Operation(summary = "Créer une nouvelle réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Date de fin antérieure ou identifiant présent")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationDTO create(@Valid @RequestBody ReservationDTO reservation) {
        return reservationService.create(reservation);
    }

    @Operation(summary = "Modifier une réservation existante")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation modifiée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de modification invalides"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    @PutMapping("/{id}")
    public ReservationDTO update(@Parameter(description = "Identifiant de la réservation") @PathVariable Long id, @Valid @RequestBody ReservationDTO reservation) {
        return reservationService.update(id, reservation);
    }

    @Operation(summary = "Supprimer une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réservation supprimée avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de la réservation") @PathVariable Long id) {
        reservationService.deleteById(id);
    }

    @Operation(summary = "Affecter une réservation à un client (Atelier 7)",
               description = "Associe une réservation existante à un client identifié par son nom et prénom")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation affectée avec succès"),
            @ApiResponse(responseCode = "404", description = "Réservation ou client introuvable")
    })
    @PutMapping("/{idReservation}/affecter-client")
    public ReservationResponseDTO affecterReservationAClient(
            @PathVariable Long idReservation,
            @RequestParam String nom,
            @RequestParam String prenom) {
        return reservationService.affecterReservationAClient(idReservation, nom, prenom);
    }

    @Operation(summary = "Créer une réservation et son contrat associé (Atelier 7)",
               description = "Crée simultanément une réservation et un contrat en une seule transaction")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation et contrat créés avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Client ou véhicule introuvable")
    })
    @PostMapping("/avec-contrat")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponseDTO creerReservationEtContratAssocie(
            @Valid @RequestBody ReservationWithContratRequestDTO requestDTO) {
        return reservationService.creerReservationEtContratAssocie(requestDTO);
    }
}
