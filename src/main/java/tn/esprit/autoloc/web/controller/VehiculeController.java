package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

@Tag(name = "Vehicules", description = "Gestion des véhicules")
@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @Operation(summary = "Lister tous les véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules récupérée avec succès")
    @GetMapping
    public List<VehiculeDTO> findAll() {
        return vehiculeService.findAll();
    }

    @Operation(summary = "Consulter un véhicule par identifiant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @GetMapping("/{id}")
    public VehiculeDTO findById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return vehiculeService.findById(id);
    }

    @Operation(summary = "Créer un nouveau véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Valeur numérique négative ou identifiant présent")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeDTO create(@Valid @RequestBody VehiculeDTO entity) {
        return vehiculeService.create(entity);
    }

    @Operation(summary = "Modifier un véhicule existant")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule modifié avec succès"),
            @ApiResponse(responseCode = "400", description = "Données de modification invalides"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @PutMapping("/{id}")
    public VehiculeDTO update(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id, @Valid @RequestBody VehiculeDTO entity) {
        return vehiculeService.update(id, entity);
    }

    @Operation(summary = "Supprimer un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeService.deleteById(id);
    }
}
