package tn.esprit.autoloc.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Contrat;

public interface IContratRepository extends JpaRepository<Contrat, Long> {

    Optional<Contrat> findFirstByDateSignatureAndReservationIsNull(LocalDate dateSignature);

    Optional<Contrat> findByReservationIdReservation(Long idReservation);
}
