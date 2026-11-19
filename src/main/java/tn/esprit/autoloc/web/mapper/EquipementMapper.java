package tn.esprit.autoloc.web.mapper;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.web.dto.EquipementDTO;

@Component
public class EquipementMapper {

    public EquipementDTO toDto(Equipement equipement) {
        if (equipement == null) {
            return null;
        }
        return EquipementDTO.builder()
                .idEquipement(equipement.getIdEquipement())
                .libelle(equipement.getLibelle())
                .build();
    }

    public Equipement toEntity(EquipementDTO dto) {
        if (dto == null) {
            return null;
        }
        Equipement equipement = new Equipement();
        equipement.setIdEquipement(dto.getIdEquipement());
        equipement.setLibelle(dto.getLibelle());
        return equipement;
    }

    public List<EquipementDTO> toDtoList(List<Equipement> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toDto).toList();
    }
}

