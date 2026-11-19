package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipementDTO {

    private Long idEquipement;

    @NotBlank(message = "Le libellé de l'équipement ne doit pas être vide")
    @Size(max = 100, message = "Le libellé ne doit pas dépasser 100 caractères")
    private String libelle;
}

