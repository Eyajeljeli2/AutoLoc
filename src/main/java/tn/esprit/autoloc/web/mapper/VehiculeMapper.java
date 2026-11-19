package tn.esprit.autoloc.web.mapper;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.VehiculeDTO;

@Component
public class VehiculeMapper {

    public VehiculeDTO toDto(Vehicule vehicule) {
        if (vehicule == null) {
            return null;
        }
        List<Long> equipementIds = (vehicule.getEquipements() != null)
                ? vehicule.getEquipements().stream().map(Equipement::getIdEquipement).toList()
                : Collections.emptyList();

        return VehiculeDTO.builder()
                .idVehicule(vehicule.getIdVehicule())
                .immatriculation(vehicule.getImmatriculation())
                .marque(vehicule.getMarque())
                .modele(vehicule.getModele())
                .categorie(vehicule.getCategorie())
                .tarifJournalier(vehicule.getTarifJournalier())
                .statut(vehicule.getStatut())
                .idAgence(vehicule.getAgence() != null ? vehicule.getAgence().getIdAgence() : null)
                .nomAgence(vehicule.getAgence() != null ? vehicule.getAgence().getNom() : null)
                .equipementIds(equipementIds)
                .build();
    }

    public Vehicule toEntity(VehiculeDTO dto) {
        if (dto == null) {
            return null;
        }
        Vehicule vehicule = new Vehicule();
        vehicule.setIdVehicule(dto.getIdVehicule());
        vehicule.setImmatriculation(dto.getImmatriculation());
        vehicule.setMarque(dto.getMarque());
        vehicule.setModele(dto.getModele());
        vehicule.setCategorie(dto.getCategorie());
        vehicule.setTarifJournalier(dto.getTarifJournalier());
        vehicule.setStatut(dto.getStatut());
        if (dto.getIdAgence() != null) {
            Agence agence = new Agence();
            agence.setIdAgence(dto.getIdAgence());
            vehicule.setAgence(agence);
        }
        if (dto.getEquipementIds() != null) {
            List<Equipement> equipements = dto.getEquipementIds().stream().map(id -> {
                Equipement eq = new Equipement();
                eq.setIdEquipement(id);
                return eq;
            }).toList();
            vehicule.setEquipements(equipements);
        }
        return vehicule;
    }

    public List<VehiculeDTO> toDtoList(List<Vehicule> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toDto).toList();
    }
}

