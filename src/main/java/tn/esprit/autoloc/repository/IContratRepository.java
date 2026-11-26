package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.autoloc.domain.Contrat;

public interface IContratRepository extends JpaRepository<Contrat, Long> {

    Optional<Contrat> findFirstByDateSignatureAndReservationIsNull(LocalDate dateSignature);

    Optional<Contrat> findByReservationIdReservation(Long idReservation);

    @Query("SELECT c FROM Contrat c JOIN c.reservation r " +
           "WHERE c.valide = true " +
           "AND r.dateFin BETWEEN :start AND :end " +
           "AND r.statut NOT IN (tn.esprit.autoloc.domain.StatutReservation.ANNULEE, tn.esprit.autoloc.domain.StatutReservation.TERMINEE)")
    List<Contrat> findContratsArrivantAEcheance(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
