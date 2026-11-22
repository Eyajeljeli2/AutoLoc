package tn.esprit.autoloc.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.domain.Client;

public interface IClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByNomAndPrenom(String nom, String prenom);

    Optional<Client> findFirstByNomIgnoreCaseAndPrenomIgnoreCase(String nom, String prenom);
}
