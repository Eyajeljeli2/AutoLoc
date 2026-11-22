package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.*;
import tn.esprit.autoloc.domain.StatutReservation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de requête pour la création simultanée d'une réservation et de son contrat associé")
public class ReservationWithContratRequestDTO {

    @NotNull(message = "La date de début est obligatoire")
    @Schema(description = "Date de début de la réservation", example = "2026-12-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    @Schema(description = "Date de fin de la réservation", example = "2026-12-05", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateFin;

    @Schema(description = "Statut initial de la réservation", example = "CONFIRMEE")
    private StatutReservation statut;

    @NotNull(message = "L'identifiant du client est obligatoire")
    @Schema(description = "Identifiant du client associé", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idClient;

    @NotNull(message = "L'identifiant du véhicule est obligatoire")
    @Schema(description = "Identifiant du véhicule associé", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idVehicule;

    @NotNull(message = "La date de signature du contrat est obligatoire")
    @Schema(description = "Date de signature du contrat", example = "2026-11-25", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate dateSignature;

    @DecimalMin(value = "0.0", inclusive = true, message = "Le montant total ne peut pas être négatif")
    @Schema(description = "Montant total du contrat (calculé automatiquement si omis)", example = "450.00")
    private BigDecimal montantTotal;

    @Builder.Default
    @Schema(description = "Indicateur de validité du contrat", example = "true")
    private Boolean valide = true;

    @Builder.Default
    @Schema(description = "Liste optionnelle de paiements associés")
    private List<PaiementDTO> paiements = new ArrayList<>();

    @JsonProperty("reservation")
    private void unpackReservation(Map<String, Object> res) {
        if (res != null) {
            if (res.get("dateDebut") != null) {
                this.dateDebut = LocalDate.parse(res.get("dateDebut").toString());
            }
            if (res.get("dateFin") != null) {
                this.dateFin = LocalDate.parse(res.get("dateFin").toString());
            }
            if (res.get("statut") != null) {
                this.statut = StatutReservation.valueOf(res.get("statut").toString());
            }
            if (res.get("idClient") != null) {
                this.idClient = Long.valueOf(res.get("idClient").toString());
            }
            if (res.get("idVehicule") != null) {
                this.idVehicule = Long.valueOf(res.get("idVehicule").toString());
            }
        }
    }

    @JsonProperty("contrat")
    private void unpackContrat(Map<String, Object> ct) {
        if (ct != null) {
            if (ct.get("dateSignature") != null) {
                this.dateSignature = LocalDate.parse(ct.get("dateSignature").toString());
            }
            if (ct.get("montantTotal") != null) {
                this.montantTotal = new BigDecimal(ct.get("montantTotal").toString());
            }
            if (ct.get("valide") != null) {
                this.valide = Boolean.valueOf(ct.get("valide").toString());
            }
        }
    }

    @JsonProperty("client")
    private void unpackClient(Map<String, Object> client) {
        if (client != null) {
            Object id = client.get("idClient");
            if (id == null) {
                id = client.get("id");
            }
            if (id != null) {
                this.idClient = Long.valueOf(id.toString());
            }
        }
    }

    @JsonProperty("vehicule")
    private void unpackVehicule(Map<String, Object> vehicule) {
        if (vehicule != null) {
            Object id = vehicule.get("idVehicule");
            if (id == null) {
                id = vehicule.get("id");
            }
            if (id != null) {
                this.idVehicule = Long.valueOf(id.toString());
            }
        }
    }
}
