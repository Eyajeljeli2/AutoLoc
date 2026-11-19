package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;
    private final VehiculeMapper vehiculeMapper;

    @Override
    public VehiculeDTO create(VehiculeDTO dto) {
        if (dto.getIdVehicule() != null) {
            throw new IllegalArgumentException("Un nouveau véhicule ne doit pas avoir d'identifiant");
        }
        validerVehicule(dto);
        Vehicule entity = vehiculeMapper.toEntity(dto);
        if (dto.getIdAgence() != null) {
            Agence agence = agenceRepository.findById(dto.getIdAgence())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence", dto.getIdAgence()));
            entity.setAgence(agence);
        }
        if (dto.getEquipementIds() != null && !dto.getEquipementIds().isEmpty()) {
            List<Equipement> equipements = equipementRepository.findAllById(dto.getEquipementIds());
            entity.setEquipements(equipements);
        }
        Vehicule saved = vehiculeRepository.save(entity);
        return vehiculeMapper.toDto(saved);
    }

    @Override
    public VehiculeDTO findById(Long id) {
        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
        return vehiculeMapper.toDto(vehicule);
    }

    @Override
    public List<VehiculeDTO> findAll() {
        return vehiculeMapper.toDtoList(vehiculeRepository.findAll());
    }

    @Override
    public VehiculeDTO update(Long id, VehiculeDTO dto) {
        validerVehicule(dto);
        Vehicule existant = vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", id));
        existant.setImmatriculation(dto.getImmatriculation());
        existant.setMarque(dto.getMarque());
        existant.setModele(dto.getModele());
        existant.setCategorie(dto.getCategorie());
        existant.setTarifJournalier(dto.getTarifJournalier());
        existant.setStatut(dto.getStatut());
        if (dto.getIdAgence() != null) {
            Agence agence = agenceRepository.findById(dto.getIdAgence())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence", dto.getIdAgence()));
            existant.setAgence(agence);
        } else {
            existant.setAgence(null);
        }
        if (dto.getEquipementIds() != null) {
            List<Equipement> equipements = equipementRepository.findAllById(dto.getEquipementIds());
            existant.setEquipements(equipements);
        }
        return vehiculeMapper.toDto(vehiculeRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!vehiculeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicule", id);
        }
        vehiculeRepository.deleteById(id);
    }

    private void validerVehicule(VehiculeDTO dto) {
        if (dto.getTarifJournalier() != null && dto.getTarifJournalier().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le tarif journalier ne peut pas être négatif");
        }
    }
}
