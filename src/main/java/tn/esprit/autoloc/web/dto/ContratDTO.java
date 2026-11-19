package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratDTO {

    private Long idContrat;

    @NotNull(message = "La date de signature est obligatoire")
    private LocalDate dateSignature;

    @NotNull(message = "Le montant total est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le montant total ne peut pas être négatif")
    private BigDecimal montantTotal;

    private boolean valide;

    private Long idReservation;

    @Valid
    @Builder.Default
    private List<PaiementDTO> paiements = new ArrayList<>();

    @JsonProperty("reservation")
    private void unpackReservation(Map<String, Object> reservation) {
        if (reservation != null) {
            Object id = reservation.get("idReservation");
            if (id == null) {
                id = reservation.get("id");
            }
            if (id != null) {
                this.idReservation = Long.valueOf(id.toString());
            }
        }
    }
}

