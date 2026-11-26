package tn.esprit.autoloc.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

@SpringBootTest
@Transactional
class SchedulerServiceTest {

    @Autowired
    private ContratSchedulerService contratSchedulerService;

    @Autowired
    private VehiculeSchedulerService vehiculeSchedulerService;

    @Autowired
    private IClientRepository clientRepository;

    @Autowired
    private IVehiculeRepository vehiculeRepository;

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IContratRepository contratRepository;

    @Test
    @DisplayName("Scheduler Contrats : doit identifier uniquement les contrats expirant sous 48h")
    void testIdentifierContratsArrivantAEcheance() {
        Client client = new Client();
        client.setNom("Ben Mahmoud");
        client.setPrenom("Yassine");
        client.setEmail("yassine.bm@autoloc.tn");
        client.setNumPermis("TN-PERMIS-SCHED-1");
        client.setDateInscription(LocalDate.now());
        client = clientRepository.save(client);

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("777-TUN-001");
        vehicule.setMarque("Audi");
        vehicule.setModele("A3");
        vehicule.setCategorie(CategorieVehicule.BERLINE);
        vehicule.setTarifJournalier(BigDecimal.valueOf(180.0));
        vehicule.setStatut(StatutVehicule.LOUE);
        vehicule = vehiculeRepository.save(vehicule);

        LocalDate today = LocalDate.now();

        // 1. Contrat expirant demain (sous 48h) -> DOIT être identifié
        Reservation resExpiringSoon = new Reservation();
        resExpiringSoon.setDateDebut(today.minusDays(3));
        resExpiringSoon.setDateFin(today.plusDays(1)); // dans 24h
        resExpiringSoon.setStatut(StatutReservation.CONFIRMEE);
        resExpiringSoon.setClient(client);
        resExpiringSoon.setVehicule(vehicule);
        resExpiringSoon = reservationRepository.save(resExpiringSoon);

        Contrat contratExpiringSoon = new Contrat();
        contratExpiringSoon.setDateSignature(today.minusDays(4));
        contratExpiringSoon.setMontantTotal(BigDecimal.valueOf(720.0));
        contratExpiringSoon.setValide(true);
        contratExpiringSoon.setReservation(resExpiringSoon);
        contratExpiringSoon = contratRepository.save(contratExpiringSoon);

        // 2. Contrat expirant dans 10 jours (> 48h) -> NE DOIT PAS être identifié
        Reservation resFar = new Reservation();
        resFar.setDateDebut(today.minusDays(1));
        resFar.setDateFin(today.plusDays(10));
        resFar.setStatut(StatutReservation.CONFIRMEE);
        resFar.setClient(client);
        resFar.setVehicule(vehicule);
        resFar = reservationRepository.save(resFar);

        Contrat contratFar = new Contrat();
        contratFar.setDateSignature(today.minusDays(2));
        contratFar.setMontantTotal(BigDecimal.valueOf(1980.0));
        contratFar.setValide(true);
        contratFar.setReservation(resFar);
        contratRepository.save(contratFar);

        // 3. Contrat expirant demain mais déjà TERMINEE -> NE DOIT PAS être identifié
        Reservation resTerminee = new Reservation();
        resTerminee.setDateDebut(today.minusDays(5));
        resTerminee.setDateFin(today.plusDays(1));
        resTerminee.setStatut(StatutReservation.TERMINEE);
        resTerminee.setClient(client);
        resTerminee.setVehicule(vehicule);
        resTerminee = reservationRepository.save(resTerminee);

        Contrat contratTermine = new Contrat();
        contratTermine.setDateSignature(today.minusDays(6));
        contratTermine.setMontantTotal(BigDecimal.valueOf(1080.0));
        contratTermine.setValide(true);
        contratTermine.setReservation(resTerminee);
        contratRepository.save(contratTermine);

        // Exécution du scheduler
        List<ContratDTO> resultats = contratSchedulerService.identifierContratsArrivantAEcheance();

        assertThat(resultats).isNotEmpty();
        List<Long> idsIdentifies = resultats.stream().map(ContratDTO::getIdContrat).toList();
        assertThat(idsIdentifies).contains(contratExpiringSoon.getIdContrat());
        assertThat(idsIdentifies).doesNotContain(contratFar.getIdContrat());
        assertThat(idsIdentifies).doesNotContain(contratTermine.getIdContrat());
    }

    @Test
    @DisplayName("Scheduler Véhicules : remet le véhicule à DISPONIBLE et la réservation à TERMINEE")
    void testRemettreStatutVehiculeDisponible() {
        Client client = new Client();
        client.setNom("Ayari");
        client.setPrenom("Salma");
        client.setEmail("salma.ayari@autoloc.tn");
        client.setNumPermis("TN-PERMIS-SCHED-2");
        client.setDateInscription(LocalDate.now());
        client = clientRepository.save(client);

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("777-TUN-002");
        vehicule.setMarque("Mercedes");
        vehicule.setModele("Classe A");
        vehicule.setCategorie(CategorieVehicule.BERLINE);
        vehicule.setTarifJournalier(BigDecimal.valueOf(250.0));
        vehicule.setStatut(StatutVehicule.LOUE);
        vehicule = vehiculeRepository.save(vehicule);

        LocalDate today = LocalDate.now();

        // Réservation qui s'est terminée hier
        Reservation reservation = new Reservation();
        reservation.setDateDebut(today.minusDays(5));
        reservation.setDateFin(today.minusDays(1));
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        reservation = reservationRepository.save(reservation);

        // Exécution du scheduler de libération
        List<VehiculeDTO> vehiculesLiberes = vehiculeSchedulerService.remettreStatutVehiculeDisponible();

        assertThat(vehiculesLiberes).isNotEmpty();
        List<Long> idsLiberes = vehiculesLiberes.stream().map(VehiculeDTO::getIdVehicule).toList();
        assertThat(idsLiberes).contains(vehicule.getIdVehicule());

        // Vérification de la persistance en base
        Reservation resUpdated = reservationRepository.findById(reservation.getIdReservation()).orElseThrow();
        assertThat(resUpdated.getStatut()).isEqualTo(StatutReservation.TERMINEE);

        Vehicule vehiculeUpdated = vehiculeRepository.findById(vehicule.getIdVehicule()).orElseThrow();
        assertThat(vehiculeUpdated.getStatut()).isEqualTo(StatutVehicule.DISPONIBLE);
    }

    @Test
    @DisplayName("Scheduler Véhicules : un véhicule en MAINTENANCE ne doit pas être forcé à DISPONIBLE")
    void testVehiculeEnMaintenanceResteEnMaintenance() {
        Client client = new Client();
        client.setNom("Hammami");
        client.setPrenom("Nader");
        client.setEmail("nader.hammami@autoloc.tn");
        client.setNumPermis("TN-PERMIS-SCHED-3");
        client.setDateInscription(LocalDate.now());
        client = clientRepository.save(client);

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("777-TUN-003");
        vehicule.setMarque("Hyundai");
        vehicule.setModele("Tucson");
        vehicule.setCategorie(CategorieVehicule.SUV);
        vehicule.setTarifJournalier(BigDecimal.valueOf(160.0));
        vehicule.setStatut(StatutVehicule.MAINTENANCE); // Actuellement en panne/maintenance
        vehicule = vehiculeRepository.save(vehicule);

        LocalDate today = LocalDate.now();

        Reservation reservation = new Reservation();
        reservation.setDateDebut(today.minusDays(4));
        reservation.setDateFin(today.minusDays(1));
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        reservation = reservationRepository.save(reservation);

        vehiculeSchedulerService.remettreStatutVehiculeDisponible();

        Reservation resUpdated = reservationRepository.findById(reservation.getIdReservation()).orElseThrow();
        assertThat(resUpdated.getStatut()).isEqualTo(StatutReservation.TERMINEE);

        Vehicule vehiculeUpdated = vehiculeRepository.findById(vehicule.getIdVehicule()).orElseThrow();
        assertThat(vehiculeUpdated.getStatut()).isEqualTo(StatutVehicule.MAINTENANCE);
    }
}
