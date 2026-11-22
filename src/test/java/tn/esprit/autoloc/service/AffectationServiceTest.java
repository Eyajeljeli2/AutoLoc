package tn.esprit.autoloc.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.*;
import tn.esprit.autoloc.web.dto.*;

@SpringBootTest
@Transactional
class AffectationServiceTest {

    @Autowired
    private IAffectationService affectationService;

    @Autowired
    private IClientRepository clientRepository;

    @Autowired
    private IVehiculeRepository vehiculeRepository;

    @Autowired
    private IEquipementRepository equipementRepository;

    @Autowired
    private IMaintenanceRepository maintenanceRepository;

    @Autowired
    private IReservationRepository reservationRepository;

    @Autowired
    private IContratRepository contratRepository;

    @Test
    @DisplayName("Service 1 : affecterReservationAClient doit associer le client à la réservation")
    void testAffecterReservationAClient() {
        Client client = new Client();
        client.setNom("Ben Salah");
        client.setPrenom("Ahmed");
        client.setEmail("ahmed.bensalah.test@autoloc.tn");
        client.setNumPermis("TN-PERMIS-TEST-1");
        client.setDateInscription(LocalDate.now());
        client = clientRepository.save(client);

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("999-TUN-001");
        vehicule.setMarque("Renault");
        vehicule.setModele("Clio");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(BigDecimal.valueOf(90.0));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule = vehiculeRepository.save(vehicule);

        Client dummyClient = new Client();
        dummyClient.setNom("Autre");
        dummyClient.setPrenom("Client");
        dummyClient.setEmail("autre.client.test@autoloc.tn");
        dummyClient.setNumPermis("TN-PERMIS-DUMMY-1");
        dummyClient.setDateInscription(LocalDate.now());
        dummyClient = clientRepository.save(dummyClient);

        Reservation reservation = new Reservation();
        reservation.setDateDebut(LocalDate.now().plusDays(2));
        reservation.setDateFin(LocalDate.now().plusDays(5));
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setClient(dummyClient);
        reservation.setVehicule(vehicule);
        reservation = reservationRepository.save(reservation);

        ReservationResponseDTO response = affectationService.affecterReservationAClient(
                reservation.getIdReservation(), "Ben Salah", "Ahmed");

        assertThat(response).isNotNull();
        assertThat(response.getIdClient()).isEqualTo(client.getIdClient());
        assertThat(response.getNomClient()).isEqualTo("Ben Salah");
        assertThat(response.getPrenomClient()).isEqualTo("Ahmed");
    }

    @Test
    @DisplayName("Service 2 : affecterVehiculeAEquipement doit associer l'équipement au véhicule")
    void testAffecterVehiculeAEquipement() {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("999-TUN-002");
        vehicule.setMarque("Peugeot");
        vehicule.setModele("3008");
        vehicule.setCategorie(CategorieVehicule.SUV);
        vehicule.setTarifJournalier(BigDecimal.valueOf(180.0));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule = vehiculeRepository.save(vehicule);

        Equipement equipement = new Equipement();
        equipement.setLibelle("GPS Navigation Test");
        equipement = equipementRepository.save(equipement);

        VehiculeDTO updated = affectationService.affecterVehiculeAEquipement(
                vehicule.getIdVehicule(), equipement.getIdEquipement());

        assertThat(updated).isNotNull();
        assertThat(updated.getEquipementIds()).contains(equipement.getIdEquipement());
    }

    @Test
    @DisplayName("Service 3 : affecterMaintenanceAvehicule doit lier la maintenance et changer le statut à MAINTENANCE")
    void testAffecterMaintenanceAvehicule() {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("999-TUN-003");
        vehicule.setMarque("Volkswagen");
        vehicule.setModele("Golf");
        vehicule.setCategorie(CategorieVehicule.BERLINE);
        vehicule.setTarifJournalier(BigDecimal.valueOf(120.0));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule = vehiculeRepository.save(vehicule);

        VehiculeDTO updated = affectationService.affecterMaintenanceAvehicule(
                "Vidange et changement filtres test", "999-TUN-003");

        assertThat(updated).isNotNull();
        assertThat(updated.getStatut()).isEqualTo(StatutVehicule.MAINTENANCE);

        Vehicule reloaded = vehiculeRepository.findById(vehicule.getIdVehicule()).orElseThrow();
        assertThat(reloaded.getStatut()).isEqualTo(StatutVehicule.MAINTENANCE);
        assertThat(reloaded.getMaintenances()).hasSize(1);
        assertThat(reloaded.getMaintenances().get(0).getDescription()).isEqualTo("Vidange et changement filtres test");
    }

    @Test
    @DisplayName("Service 4 : affecterContratAReservation doit associer un contrat par période de dates")
    void testAffecterContratAReservation() {
        Client client = new Client();
        client.setNom("Trabelsi");
        client.setPrenom("Sarra");
        client.setEmail("sarra.trabelsi.test@autoloc.tn");
        client.setNumPermis("TN-PERMIS-TEST-4");
        client.setDateInscription(LocalDate.now());
        client = clientRepository.save(client);

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("999-TUN-004");
        vehicule.setMarque("Toyota");
        vehicule.setModele("Yaris");
        vehicule.setCategorie(CategorieVehicule.CITADINE);
        vehicule.setTarifJournalier(BigDecimal.valueOf(100.0));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule = vehiculeRepository.save(vehicule);

        LocalDate dateDebut = LocalDate.of(2027, 5, 10);
        LocalDate dateFin = LocalDate.of(2027, 5, 15); // 5 jours
        LocalDate dateSignature = LocalDate.of(2027, 5, 5);

        Reservation res = new Reservation();
        res.setDateDebut(dateDebut);
        res.setDateFin(dateFin);
        res.setStatut(StatutReservation.EN_ATTENTE);
        res.setClient(client);
        res.setVehicule(vehicule);
        res = reservationRepository.save(res);

        ContratDTO contratDto = affectationService.affecterContratAReservation(dateSignature, dateDebut, dateFin);

        assertThat(contratDto).isNotNull();
        assertThat(contratDto.getDateSignature()).isEqualTo(dateSignature);
        assertThat(contratDto.getIdReservation()).isEqualTo(res.getIdReservation());
        assertThat(contratDto.getMontantTotal()).isEqualByComparingTo(BigDecimal.valueOf(500.0)); // 5 * 100
        assertThat(contratDto.isValide()).isTrue();

        Reservation updatedRes = reservationRepository.findById(res.getIdReservation()).orElseThrow();
        assertThat(updatedRes.getStatut()).isEqualTo(StatutReservation.CONFIRMEE);
    }

    @Test
    @DisplayName("Service 5 : creerReservationEtContratAssocie doit créer réservation + contrat en une transaction")
    void testCreerReservationEtContratAssocie() {
        Client client = new Client();
        client.setNom("Gharbi");
        client.setPrenom("Karim");
        client.setEmail("karim.gharbi.test@autoloc.tn");
        client.setNumPermis("TN-PERMIS-TEST-5");
        client.setDateInscription(LocalDate.now());
        client = clientRepository.save(client);

        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation("999-TUN-005");
        vehicule.setMarque("Kia");
        vehicule.setModele("Sportage");
        vehicule.setCategorie(CategorieVehicule.SUV);
        vehicule.setTarifJournalier(BigDecimal.valueOf(150.0));
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        vehicule = vehiculeRepository.save(vehicule);

        LocalDate dateDebut = LocalDate.of(2027, 6, 1);
        LocalDate dateFin = LocalDate.of(2027, 6, 4); // 3 jours
        LocalDate dateSignature = LocalDate.of(2027, 5, 20);

        ReservationWithContratRequestDTO request = ReservationWithContratRequestDTO.builder()
                .dateDebut(dateDebut)
                .dateFin(dateFin)
                .idClient(client.getIdClient())
                .idVehicule(vehicule.getIdVehicule())
                .dateSignature(dateSignature)
                .valide(true)
                .build();

        ReservationResponseDTO response = affectationService.creerReservationEtContratAssocie(request);

        assertThat(response).isNotNull();
        assertThat(response.getIdReservation()).isNotNull();
        assertThat(response.getIdClient()).isEqualTo(client.getIdClient());
        assertThat(response.getIdVehicule()).isEqualTo(vehicule.getIdVehicule());
        assertThat(response.getIdContrat()).isNotNull();
        assertThat(response.getContrat()).isNotNull();
        assertThat(response.getContrat().getMontantTotal()).isEqualByComparingTo(BigDecimal.valueOf(450.0)); // 3 * 150

        Vehicule updatedVehicule = vehiculeRepository.findById(vehicule.getIdVehicule()).orElseThrow();
        assertThat(updatedVehicule.getStatut()).isEqualTo(StatutVehicule.LOUE);
    }

    @Test
    @DisplayName("Tests des cas d'erreur (validation et 404)")
    void testCasErreurs() {
        assertThrows(IllegalArgumentException.class, () ->
                affectationService.affecterReservationAClient(null, "Nom", "Prenom"));

        assertThrows(ResourceNotFoundException.class, () ->
                affectationService.affecterReservationAClient(999999L, "Inconnu", "Inconnu"));

        assertThrows(ResourceNotFoundException.class, () ->
                affectationService.affecterMaintenanceAvehicule("Vidange", "IMMAT-INEXISTANTE"));

        assertThrows(IllegalArgumentException.class, () ->
                affectationService.affecterContratAReservation(LocalDate.now(), LocalDate.now().plusDays(5), LocalDate.now().plusDays(2)));
    }
}
