package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.web.dto.EquipementDTO;
import tn.esprit.autoloc.web.mapper.EquipementMapper;

@Service
@RequiredArgsConstructor
public class EquipementServiceImpl implements IEquipementService {

    private final IEquipementRepository equipementRepository;
    private final EquipementMapper equipementMapper;

    @Override
    public EquipementDTO create(EquipementDTO dto) {
        if (dto.getIdEquipement() != null) {
            throw new IllegalArgumentException("Un nouvel équipement ne doit pas avoir d'identifiant");
        }
        validerEquipement(dto);
        Equipement entity = equipementMapper.toEntity(dto);
        Equipement saved = equipementRepository.save(entity);
        return equipementMapper.toDto(saved);
    }

    @Override
    public EquipementDTO findById(Long id) {
        Equipement equipement = equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
        return equipementMapper.toDto(equipement);
    }

    @Override
    public List<EquipementDTO> findAll() {
        return equipementMapper.toDtoList(equipementRepository.findAll());
    }

    @Override
    public EquipementDTO update(Long id, EquipementDTO dto) {
        validerEquipement(dto);
        Equipement existant = equipementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", id));
        existant.setLibelle(dto.getLibelle());
        return equipementMapper.toDto(equipementRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!equipementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Equipement", id);
        }
        equipementRepository.deleteById(id);
    }

    private void validerEquipement(EquipementDTO dto) {
        if (dto.getLibelle() == null || dto.getLibelle().trim().isEmpty()) {
            throw new IllegalArgumentException("Le libellé de l'équipement ne doit pas être vide");
        }
    }
}
