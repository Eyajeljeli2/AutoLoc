package tn.esprit.autoloc.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.service.IAffectationService;
import tn.esprit.autoloc.web.dto.*;

@ExtendWith(MockitoExtension.class)
class AffectationControllerTest {

    @Mock
    private IAffectationService affectationService;

    @InjectMocks
    private AffectationController affectationController;

    @Test
    @DisplayName("affecterReservationAClient doit déléguer au service et retourner 200 OK")
    void testAffecterReservationAClient() {
        ReservationResponseDTO mockResponse = ReservationResponseDTO.builder()
                .idReservation(1L)
                .nomClient("Ben Salah")
                .prenomClient("Ahmed")
                .statut(StatutReservation.CONFIRMEE)
                .build();

        when(affectationService.affecterReservationAClient(1L, "Ben Salah", "Ahmed"))
                .thenReturn(mockResponse);

        ResponseEntity<ReservationResponseDTO> response =
                affectationController.affecterReservationAClient(1L, "Ben Salah", "Ahmed");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getIdReservation()).isEqualTo(1L);
        verify(affectationService, times(1)).affecterReservationAClient(1L, "Ben Salah", "Ahmed");
    }

    @Test
    @DisplayName("affecterVehiculeAEquipement doit déléguer au service et retourner 200 OK")
    void testAffecterVehiculeAEquipement() {
        VehiculeDTO mockVehicule = VehiculeDTO.builder()
                .idVehicule(2L)
                .immatriculation("215-TUN-1234")
                .build();

        when(affectationService.affecterVehiculeAEquipement(2L, 10L))
                .thenReturn(mockVehicule);

        ResponseEntity<VehiculeDTO> response =
                affectationController.affecterVehiculeAEquipement(2L, 10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getImmatriculation()).isEqualTo("215-TUN-1234");
        verify(affectationService, times(1)).affecterVehiculeAEquipement(2L, 10L);
    }

    @Test
    @DisplayName("affecterMaintenanceAvehicule doit déléguer au service et retourner 200 OK")
    void testAffecterMaintenanceAvehicule() {
        VehiculeDTO mockVehicule = VehiculeDTO.builder()
                .idVehicule(3L)
                .immatriculation("220-TUN-5678")
                .statut(StatutVehicule.MAINTENANCE)
                .build();

        when(affectationService.affecterMaintenanceAvehicule("Vidange moteur", "220-TUN-5678"))
                .thenReturn(mockVehicule);

        ResponseEntity<VehiculeDTO> response =
                affectationController.affecterMaintenanceAvehicule("Vidange moteur", "220-TUN-5678");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatut()).isEqualTo(StatutVehicule.MAINTENANCE);
        verify(affectationService, times(1)).affecterMaintenanceAvehicule("Vidange moteur", "220-TUN-5678");
    }

    @Test
    @DisplayName("affecterContratAReservation doit déléguer au service et retourner 200 OK")
    void testAffecterContratAReservation() {
        ContratDTO mockContrat = ContratDTO.builder()
                .idContrat(4L)
                .dateSignature(LocalDate.of(2026, 12, 1))
                .montantTotal(BigDecimal.valueOf(400.0))
                .build();

        LocalDate sig = LocalDate.of(2026, 12, 1);
        LocalDate debut = LocalDate.of(2026, 12, 2);
        LocalDate fin = LocalDate.of(2026, 12, 6);

        when(affectationService.affecterContratAReservation(sig, debut, fin))
                .thenReturn(mockContrat);

        ResponseEntity<ContratDTO> response =
                affectationController.affecterContratAReservation(sig, debut, fin);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getIdContrat()).isEqualTo(4L);
        verify(affectationService, times(1)).affecterContratAReservation(sig, debut, fin);
    }

    @Test
    @DisplayName("creerReservationEtContratAssocie doit déléguer au service et retourner 201 CREATED")
    void testCreerReservationEtContratAssocie() {
        ReservationWithContratRequestDTO request = ReservationWithContratRequestDTO.builder()
                .dateDebut(LocalDate.of(2026, 12, 10))
                .dateFin(LocalDate.of(2026, 12, 15))
                .idClient(1L)
                .idVehicule(2L)
                .dateSignature(LocalDate.of(2026, 12, 1))
                .build();

        ReservationResponseDTO mockResponse = ReservationResponseDTO.builder()
                .idReservation(5L)
                .idContrat(15L)
                .build();

        when(affectationService.creerReservationEtContratAssocie(request))
                .thenReturn(mockResponse);

        ResponseEntity<ReservationResponseDTO> response =
                affectationController.creerReservationEtContratAssocie(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getIdReservation()).isEqualTo(5L);
        verify(affectationService, times(1)).creerReservationEtContratAssocie(request);
    }
}
