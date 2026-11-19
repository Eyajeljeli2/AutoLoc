package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {

    private Long idClient;

    @NotBlank(message = "Le nom du client ne doit pas être vide")
    @Size(max = 50, message = "Le nom ne doit pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom du client ne doit pas être vide")
    @Size(max = 50, message = "Le prénom ne doit pas dépasser 50 caractères")
    private String prenom;

    @NotBlank(message = "L'e-mail du client ne doit pas être vide")
    @Email(message = "Format d'e-mail invalide")
    @Size(max = 100, message = "L'e-mail ne doit pas dépasser 100 caractères")
    private String email;

    @Pattern(regexp = "^$|[0-9+ ]{8,20}", message = "Le numéro de téléphone doit contenir entre 8 et 20 chiffres")
    private String telephone;

    @NotBlank(message = "Le numéro de permis est obligatoire")
    @Size(max = 30, message = "Le numéro de permis ne doit pas dépasser 30 caractères")
    private String numPermis;

    private LocalDate dateInscription;
}

