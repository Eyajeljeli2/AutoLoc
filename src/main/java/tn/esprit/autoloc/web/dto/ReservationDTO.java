package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Map;
import lombok.*;
import tn.esprit.autoloc.domain.StatutReservation;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {

    private Long idReservation;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDate dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate dateFin;

    @NotNull(message = "Le statut de réservation est obligatoire")
    private StatutReservation statut;

    private Long idClient;
    private String nomClient;

    private Long idVehicule;
    private String immatriculationVehicule;

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

