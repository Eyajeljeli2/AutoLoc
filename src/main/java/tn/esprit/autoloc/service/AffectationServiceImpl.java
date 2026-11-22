package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.*;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.*;
import tn.esprit.autoloc.web.dto.*;
import tn.esprit.autoloc.web.mapper.ContratMapper;
import tn.esprit.autoloc.web.mapper.PaiementMapper;
import tn.esprit.autoloc.web.mapper.ReservationMapper;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;

@Service
@RequiredArgsConstructor
public class AffectationServiceImpl implements IAffectationService {

    private final IReservationRepository reservationRepository;
    private final IClientRepository clientRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IEquipementRepository equipementRepository;
    private final IMaintenanceRepository maintenanceRepository;
    private final IContratRepository contratRepository;

    private final ReservationMapper reservationMapper;
    private final VehiculeMapper vehiculeMapper;
    private final ContratMapper contratMapper;
    private final PaiementMapper paiementMapper;

    @Override
    @Transactional
    public ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom) {
        if (idReservation == null) {
            throw new IllegalArgumentException("L'identifiant de la réservation est obligatoire");
        }
        if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank()) {
            throw new IllegalArgumentException("Le nom et le prénom du client sont obligatoires");
        }

        Reservation reservation = reservationRepository.findById(idReservation)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", idReservation));

        Client client = clientRepository.findFirstByNomIgnoreCaseAndPrenomIgnoreCase(nom.trim(), prenom.trim())
                .or(() -> clientRepository.findByNomAndPrenom(nom.trim(), prenom.trim()))
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec le nom '" + nom + "' et le prénom '" + prenom + "'"));

        reservation.setClient(client);
        if (client.getReservations() != null && !client.getReservations().contains(reservation)) {
            client.getReservations().add(reservation);
        }

        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationMapper.toResponseDto(savedReservation);
    }

    @Override
    @Transactional
    public VehiculeDTO affecterVehiculeAEquipement(Long idVehicule, Long idEquipement) {
        if (idVehicule == null || idEquipement == null) {
            throw new IllegalArgumentException("Les identifiants du véhicule et de l'équipement sont obligatoires");
        }

        Vehicule vehicule = vehiculeRepository.findById(idVehicule)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", idVehicule));

        Equipement equipement = equipementRepository.findById(idEquipement)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", idEquipement));

        if (vehicule.getEquipements() == null) {
            vehicule.setEquipements(new ArrayList<>());
        }
        if (!vehicule.getEquipements().contains(equipement)) {
            vehicule.getEquipements().add(equipement);
        }

        if (equipement.getVehicules() == null) {
            equipement.setVehicules(new ArrayList<>());
        }
        if (!equipement.getVehicules().contains(vehicule)) {
            equipement.getVehicules().add(vehicule);
        }

        Vehicule savedVehicule = vehiculeRepository.save(vehicule);
        return vehiculeMapper.toDto(savedVehicule);
    }

    @Override
    @Transactional
    public VehiculeDTO affecterMaintenanceAvehicule(String description, String immatriculation) {
        if (immatriculation == null || immatriculation.isBlank()) {
            throw new IllegalArgumentException("L'immatriculation du véhicule est obligatoire");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("La description de la maintenance est obligatoire");
        }

        Vehicule vehicule = vehiculeRepository.findByImmatriculation(immatriculation.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Véhicule introuvable avec l'immatriculation : " + immatriculation));

        Maintenance maintenance = maintenanceRepository.findFirstByDescription(description.trim())
                .orElseGet(() -> {
                    Maintenance m = new Maintenance();
                    m.setDescription(description.trim());
                    m.setDateDebut(LocalDate.now());
                    return m;
                });

        if (maintenance.getDateDebut() == null) {
            maintenance.setDateDebut(LocalDate.now());
        }
        maintenance.setVehicule(vehicule);
        maintenanceRepository.save(maintenance);

        if (vehicule.getMaintenances() == null) {
            vehicule.setMaintenances(new ArrayList<>());
        }
        if (!vehicule.getMaintenances().contains(maintenance)) {
            vehicule.getMaintenances().add(maintenance);
        }
        vehicule.setStatut(StatutVehicule.MAINTENANCE);

        Vehicule savedVehicule = vehiculeRepository.save(vehicule);
        return vehiculeMapper.toDto(savedVehicule);
    }

    @Override
    @Transactional
    public ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin) {
        if (dateSignature == null || dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("Les dates de signature, de début et de fin sont obligatoires");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }

        Reservation reservation = reservationRepository.findFirstByDateDebutAndDateFinAndContratIsNull(dateDebut, dateFin)
                .or(() -> reservationRepository.findFirstByDateDebutAndDateFin(dateDebut, dateFin))
                .orElseThrow(() -> new ResourceNotFoundException("Aucune réservation trouvée pour la période du " + dateDebut + " au " + dateFin));

        Contrat contrat = reservation.getContrat();
        if (contrat == null) {
            contrat = contratRepository.findFirstByDateSignatureAndReservationIsNull(dateSignature)
                    .orElseGet(() -> {
                        Contrat c = new Contrat();
                        c.setDateSignature(dateSignature);
                        c.setValide(true);
                        return c;
                    });
        }

        contrat.setDateSignature(dateSignature);
        contrat.setReservation(reservation);
        contrat.setValide(true);

        if (contrat.getMontantTotal() == null || contrat.getMontantTotal().compareTo(BigDecimal.ZERO) <= 0) {
            long days = ChronoUnit.DAYS.between(dateDebut, dateFin);
            long duree = days > 0 ? days : 1;
            BigDecimal tarif = (reservation.getVehicule() != null && reservation.getVehicule().getTarifJournalier() != null)
                    ? reservation.getVehicule().getTarifJournalier()
                    : BigDecimal.valueOf(100);
            contrat.setMontantTotal(tarif.multiply(BigDecimal.valueOf(duree)));
        }

        reservation.setContrat(contrat);
        reservation.setStatut(StatutReservation.CONFIRMEE);

        Contrat savedContrat = contratRepository.save(contrat);
        reservationRepository.save(reservation);
        return contratMapper.toDto(savedContrat);
    }

    @Override
    @Transactional
    public ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO) {
        if (requestDTO == null) {
            throw new IllegalArgumentException("Les données de la réservation et du contrat sont obligatoires");
        }
        if (requestDTO.getDateDebut() == null || requestDTO.getDateFin() == null) {
            throw new IllegalArgumentException("Les dates de début et de fin sont obligatoires");
        }
        if (requestDTO.getDateFin().isBefore(requestDTO.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
        if (requestDTO.getIdClient() == null) {
            throw new IllegalArgumentException("L'identifiant du client est obligatoire");
        }
        if (requestDTO.getIdVehicule() == null) {
            throw new IllegalArgumentException("L'identifiant du véhicule est obligatoire");
        }

        Client client = clientRepository.findById(requestDTO.getIdClient())
                .orElseThrow(() -> new ResourceNotFoundException("Client", requestDTO.getIdClient()));

        Vehicule vehicule = vehiculeRepository.findById(requestDTO.getIdVehicule())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", requestDTO.getIdVehicule()));

        Reservation reservation = new Reservation();
        reservation.setDateDebut(requestDTO.getDateDebut());
        reservation.setDateFin(requestDTO.getDateFin());
        reservation.setStatut(requestDTO.getStatut() != null ? requestDTO.getStatut() : StatutReservation.CONFIRMEE);
        reservation.setClient(client);
        reservation.setVehicule(vehicule);
        Reservation savedReservation = reservationRepository.save(reservation);

        BigDecimal montant = requestDTO.getMontantTotal();
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            long days = ChronoUnit.DAYS.between(requestDTO.getDateDebut(), requestDTO.getDateFin());
            long duree = days > 0 ? days : 1;
            BigDecimal tarif = (vehicule.getTarifJournalier() != null)
                    ? vehicule.getTarifJournalier()
                    : BigDecimal.valueOf(100);
            montant = tarif.multiply(BigDecimal.valueOf(duree));
        }

        Contrat contrat = new Contrat();
        contrat.setDateSignature(requestDTO.getDateSignature() != null ? requestDTO.getDateSignature() : LocalDate.now());
        contrat.setMontantTotal(montant);
        contrat.setValide(requestDTO.getValide() != null ? requestDTO.getValide() : true);
        contrat.setReservation(savedReservation);

        if (requestDTO.getPaiements() != null && !requestDTO.getPaiements().isEmpty()) {
            List<Paiement> paiements = requestDTO.getPaiements().stream().map(pDto -> {
                Paiement p = paiementMapper.toEntity(pDto);
                p.setContrat(contrat);
                return p;
            }).toList();
            contrat.setPaiements(paiements);
        }

        Contrat savedContrat = contratRepository.save(contrat);
        savedReservation.setContrat(savedContrat);

        if (savedReservation.getStatut() == StatutReservation.CONFIRMEE) {
            vehicule.setStatut(StatutVehicule.LOUE);
            vehiculeRepository.save(vehicule);
        }

        return reservationMapper.toResponseDto(savedReservation, savedContrat);
    }
}
