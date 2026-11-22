package tn.esprit.autoloc.service;

import java.time.LocalDate;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

public interface IAffectationService {

    ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom);

    VehiculeDTO affecterVehiculeAEquipement(Long idVehicule, Long idEquipement);

    VehiculeDTO affecterMaintenanceAvehicule(String description, String immatriculation);

    ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin);

    ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO);
}
