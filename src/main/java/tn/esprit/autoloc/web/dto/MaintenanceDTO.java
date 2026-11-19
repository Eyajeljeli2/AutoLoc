package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Map;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceDTO {

    private Long idMaintenance;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères")
    private String description;

    private Long idVehicule;
    private String immatriculationVehicule;

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

