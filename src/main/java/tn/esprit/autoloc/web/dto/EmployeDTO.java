package tn.esprit.autoloc.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.*;
import tn.esprit.autoloc.domain.RoleEmploye;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeDTO {

    private Long idEmploye;

    @NotBlank(message = "Le nom de l'employé ne doit pas être vide")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom de l'employé ne doit pas être vide")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String prenom;

    @NotNull(message = "Le rôle de l'employé est obligatoire")
    private RoleEmploye role;

    private Long idAgence;
    private String nomAgence;

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

