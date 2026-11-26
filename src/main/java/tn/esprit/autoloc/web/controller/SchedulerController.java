package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.autoloc.service.ContratSchedulerService;
import tn.esprit.autoloc.service.VehiculeSchedulerService;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

@Tag(name = "Schedulers", description = "Gestion et déclenchement manuel des tâches planifiées (Atelier 8)")
@RestController
@RequestMapping({"/schedulers", "/api/schedulers"})
@RequiredArgsConstructor
public class SchedulerController {

    private final ContratSchedulerService contratSchedulerService;
    private final VehiculeSchedulerService vehiculeSchedulerService;

    @Operation(summary = "Identifier les contrats arrivant à échéance sous 48h (déclenchement manuel)",
               description = "Exécute à la demande le traitement planifié quotidien de 7h00 du matin")
    @ApiResponse(responseCode = "200", description = "Liste des contrats arrivant à échéance sous 48 heures")
    @GetMapping("/contrats-echeance-48h")
    public ResponseEntity<List<ContratDTO>> triggerContratsEcheance() {
        return ResponseEntity.ok(contratSchedulerService.identifierContratsArrivantAEcheance());
    }

    @Operation(summary = "Libérer les véhicules de locations terminées (déclenchement manuel)",
               description = "Exécute à la demande le traitement planifié quotidien de 1h00 du matin (clôture des réservations et libération des véhicules)")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules remis à l'état DISPONIBLE")
    @PostMapping("/liberer-vehicules")
    public ResponseEntity<List<VehiculeDTO>> triggerLibererVehicules() {
        return ResponseEntity.ok(vehiculeSchedulerService.remettreStatutVehiculeDisponible());
    }
}
