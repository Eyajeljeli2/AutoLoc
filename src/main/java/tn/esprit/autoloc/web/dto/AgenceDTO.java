package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgenceDTO {

    private Long idAgence;

    @NotBlank(message = "Le nom de l'agence ne doit pas être vide")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "La ville de l'agence ne doit pas être vide")
    @Size(max = 50, message = "La ville ne doit pas dépasser 50 caractères")
    private String ville;

    @NotBlank(message = "L'adresse de l'agence ne doit pas être vide")
    @Size(max = 150, message = "L'adresse ne doit pas dépasser 150 caractères")
    private String adresse;

    @Pattern(regexp = "^$|[0-9+ ]{8,20}", message = "Le numéro de téléphone doit contenir entre 8 et 20 chiffres")
    private String telephone;
}

