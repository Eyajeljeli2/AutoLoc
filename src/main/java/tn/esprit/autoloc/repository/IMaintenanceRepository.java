package tn.esprit.autoloc.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Maintenance;

public interface IMaintenanceRepository extends JpaRepository<Maintenance, Long> {

    Optional<Maintenance> findFirstByDescription(String description);

    Optional<Maintenance> findFirstByDescriptionAndVehiculeIsNull(String description);
}
