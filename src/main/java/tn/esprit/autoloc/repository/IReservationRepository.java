package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Reservation;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findFirstByDateDebutAndDateFin(LocalDate dateDebut, LocalDate dateFin);

    Optional<Reservation> findFirstByDateDebutAndDateFinAndContratIsNull(LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT r FROM Reservation r " +
           "WHERE r.dateFin <= :dateReference " +
           "AND r.statut NOT IN (tn.esprit.autoloc.domain.StatutReservation.ANNULEE, tn.esprit.autoloc.domain.StatutReservation.TERMINEE)")
    List<Reservation> findReservationsAFermer(@Param("dateReference") LocalDate dateReference);
}
