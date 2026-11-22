package tn.esprit.autoloc.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.*;
import tn.esprit.autoloc.domain.StatutReservation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de réponse complet pour une réservation avec son client, véhicule et contrat associé")
public class ReservationResponseDTO {

    @Schema(description = "Identifiant de la réservation", example = "1")
    private Long idReservation;

    @Schema(description = "Date de début de la réservation", example = "2026-12-01")
    private LocalDate dateDebut;

    @Schema(description = "Date de fin de la réservation", example = "2026-12-05")
    private LocalDate dateFin;

    @Schema(description = "Statut de la réservation", example = "CONFIRMEE")
    private StatutReservation statut;

    // Informations du Client
    @Schema(description = "Identifiant du client associé", example = "1")
    private Long idClient;

    @Schema(description = "Nom du client", example = "Ben Salah")
    private String nomClient;

    @Schema(description = "Prénom du client", example = "Ahmed")
    private String prenomClient;

    @Schema(description = "Email du client", example = "ahmed.bensalah@gmail.com")
    private String emailClient;

    // Informations du Véhicule
    @Schema(description = "Identifiant du véhicule associé", example = "1")
    private Long idVehicule;

    @Schema(description = "Immatriculation du véhicule", example = "215-TUN-1234")
    private String immatriculationVehicule;

    @Schema(description = "Marque du véhicule", example = "Peugeot")
    private String marqueVehicule;

    @Schema(description = "Modèle du véhicule", example = "208")
    private String modeleVehicule;

    // Informations du Contrat associé
    @Schema(description = "Identifiant du contrat associé s'il existe", example = "1")
    private Long idContrat;

    @Schema(description = "Détails du contrat associé")
    private ContratDTO contrat;
}
