package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.*;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculeDTO {

    private Long idVehicule;

    @NotBlank(message = "L'immatriculation est obligatoire")
    @Size(max = 20, message = "L'immatriculation ne doit pas dépasser 20 caractères")
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    @Size(max = 50, message = "La marque ne doit pas dépasser 50 caractères")
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire")
    @Size(max = 50, message = "Le modèle ne doit pas dépasser 50 caractères")
    private String modele;

    @NotNull(message = "La catégorie est obligatoire")
    private CategorieVehicule categorie;

    @NotNull(message = "Le tarif journalier est obligatoire")
    @DecimalMin(value = "0.0", inclusive = true, message = "Le tarif journalier ne peut pas être négatif")
    private BigDecimal tarifJournalier;

    @NotNull(message = "Le statut est obligatoire")
    private StatutVehicule statut;

    private Long idAgence;
    private String nomAgence;

    @Builder.Default
    private List<Long> equipementIds = new ArrayList<>();

    @JsonProperty("agence")
    private void unpackAgence(Map<String, Object> agence) {
        if (agence != null) {
            Object id = agence.get("idAgence");
            if (id == null) {
                id = agence.get("id");
            }
            if (id != null) {
                this.idAgence = Long.valueOf(id.toString());
            }
        }
    }
}

