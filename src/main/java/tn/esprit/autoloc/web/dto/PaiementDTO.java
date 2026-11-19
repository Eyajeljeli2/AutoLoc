package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;
import tn.esprit.autoloc.domain.ModePaiement;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementDTO {

    private Long idPaiement;

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "0.01", inclusive = true, message = "Le montant doit être supérieur à zéro")
    private BigDecimal montant;

    @NotNull(message = "La date de paiement est obligatoire")
    private LocalDate datePaiement;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    private Long idContrat;
}

