package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IAffectationService;
import tn.esprit.autoloc.web.dto.*;

@RestController
@RequestMapping({"/affectations", "/api/affectations"})
@RequiredArgsConstructor
@Tag(name = "Affectations", description = "API des services d'affectation métier (Atelier 7 Spring Data JPA)")
public class AffectationController {

    private final IAffectationService affectationService;

    @Operation(summary = "Affecter une réservation à un client",
               description = "Associe une réservation existante à un client identifié par son nom et prénom")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation affectée avec succès au client"),
            @ApiResponse(responseCode = "400", description = "Paramètres invalides"),
            @ApiResponse(responseCode = "404", description = "Réservation ou Client introuvable")
    })
    @PutMapping("/reservations/{idReservation}/client")
    public ResponseEntity<ReservationResponseDTO> affecterReservationAClient(
            @Parameter(description = "Identifiant de la réservation", required = true)
            @PathVariable Long idReservation,
            @Parameter(description = "Nom de famille du client", required = true)
            @RequestParam String nom,
            @Parameter(description = "Prénom du client", required = true)
            @RequestParam String prenom) {
        return ResponseEntity.ok(affectationService.affecterReservationAClient(idReservation, nom, prenom));
    }

    @Operation(summary = "Affecter un équipement à un véhicule",
               description = "Associe un équipement à un véhicule (relation ManyToMany)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement affecté au véhicule avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou Équipement introuvable")
    })
    @PutMapping("/vehicules/{idVehicule}/equipements/{idEquipement}")
    public ResponseEntity<VehiculeDTO> affecterVehiculeAEquipement(
            @Parameter(description = "Identifiant du véhicule", required = true)
            @PathVariable Long idVehicule,
            @Parameter(description = "Identifiant de l'équipement", required = true)
            @PathVariable Long idEquipement) {
        return ResponseEntity.ok(affectationService.affecterVehiculeAEquipement(idVehicule, idEquipement));
    }

    @Operation(summary = "Affecter une maintenance à un véhicule",
               description = "Associe une maintenance (existante ou créée) à un véhicule via son immatriculation et met à jour son statut à MAINTENANCE")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance affectée avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @PostMapping("/maintenances/vehicule")
    public ResponseEntity<VehiculeDTO> affecterMaintenanceAvehicule(
            @Parameter(description = "Description de l'intervention de maintenance", required = true)
            @RequestParam String description,
            @Parameter(description = "Immatriculation du véhicule", required = true)
            @RequestParam String immatriculation) {
        return ResponseEntity.ok(affectationService.affecterMaintenanceAvehicule(description, immatriculation));
    }

    @Operation(summary = "Affecter un contrat à une réservation par dates",
               description = "Crée ou associe un contrat à la réservation correspondant à la période donnée")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat affecté avec succès"),
            @ApiResponse(responseCode = "400", description = "Dates invalides"),
            @ApiResponse(responseCode = "404", description = "Aucune réservation trouvée pour cette période")
    })
    @PostMapping("/contrats/reservation")
    public ResponseEntity<ContratDTO> affecterContratAReservation(
            @Parameter(description = "Date de signature du contrat (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateSignature,
            @Parameter(description = "Date de début de la réservation (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @Parameter(description = "Date de fin de la réservation (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin) {
        return ResponseEntity.ok(affectationService.affecterContratAReservation(dateSignature, dateDebut, dateFin));
    }

    @Operation(summary = "Créer une réservation et son contrat associé",
               description = "Crée de façon transactionnelle une réservation et son contrat en une seule requête")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation et contrat créés avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Client ou Véhicule introuvable")
    })
    @PostMapping("/reservations/avec-contrat")
    public ResponseEntity<ReservationResponseDTO> creerReservationEtContratAssocie(
            @Valid @RequestBody ReservationWithContratRequestDTO requestDTO) {
        ReservationResponseDTO created = affectationService.creerReservationEtContratAssocie(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
