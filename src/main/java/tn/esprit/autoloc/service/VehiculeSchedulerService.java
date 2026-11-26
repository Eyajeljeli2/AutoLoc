package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class VehiculeSchedulerService {

    private final IReservationRepository reservationRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final VehiculeMapper vehiculeMapper;
    private final ContratSchedulerService contratSchedulerService;

    /**
     * Chaque jour à 1h du matin.
     * Remet le statut d'un véhicule à DISPONIBLE à la fin d'une location
     * et ferme le statut de location (StatutReservation.TERMINEE).
     */
    @Scheduled(cron = "${autoloc.scheduler.vehicules-disponibles.cron:0 0 1 * * *}")
    @Transactional
    public List<VehiculeDTO> remettreStatutVehiculeDisponible() {
        LocalDate aujourdhui = LocalDate.now();
        log.info("[Scheduler Véhicules 1h00] Début du traitement des fins de location au {}...", aujourdhui);

        List<Reservation> reservationsAFermer = reservationRepository.findReservationsAFermer(aujourdhui);
        List<VehiculeDTO> vehiculesLiberes = new ArrayList<>();

        if (reservationsAFermer.isEmpty()) {
            log.info("[Scheduler Véhicules 1h00] Aucune location terminée à clôturer.");
        } else {
            for (Reservation reservation : reservationsAFermer) {
                // Clôture de la réservation
                reservation.setStatut(StatutReservation.TERMINEE);
                reservationRepository.save(reservation);

                // Remise du véhicule à DISPONIBLE (si non en maintenance)
                Vehicule vehicule = reservation.getVehicule();
                if (vehicule != null) {
                    if (vehicule.getStatut() != StatutVehicule.MAINTENANCE) {
                        vehicule.setStatut(StatutVehicule.DISPONIBLE);
                        Vehicule saved = vehiculeRepository.save(vehicule);
                        vehiculesLiberes.add(vehiculeMapper.toDto(saved));
                        log.info(" -> Véhicule #{} ({}) remis à DISPONIBLE pour la réservation #{} terminée le {}.",
                                vehicule.getIdVehicule(), vehicule.getImmatriculation(),
                                reservation.getIdReservation(), reservation.getDateFin());
                    } else {
                        log.warn(" -> Véhicule #{} ({}) reste en MAINTENANCE malgré la clôture de la réservation #{}.",
                                vehicule.getIdVehicule(), vehicule.getImmatriculation(), reservation.getIdReservation());
                    }
                }
            }
            log.info("[Scheduler Véhicules 1h00] {} réservation(s) fermée(s) et {} véhicule(s) libéré(s).",
                    reservationsAFermer.size(), vehiculesLiberes.size());
        }

        return vehiculesLiberes;
    }

    /**
     * Accès direct à l'identification des contrats arrivant à échéance sous 48h
     */
    public Object identifierContratsArrivantAEcheance() {
        return contratSchedulerService.identifierContratsArrivantAEcheance();
    }
}
