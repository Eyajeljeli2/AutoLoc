package tn.esprit.autoloc.web.mapper;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.web.dto.AgenceDTO;

@Component
public class AgenceMapper {

    public AgenceDTO toDto(Agence agence) {
        if (agence == null) {
            return null;
        }
        return AgenceDTO.builder()
                .idAgence(agence.getIdAgence())
                .nom(agence.getNom())
                .ville(agence.getVille())
                .adresse(agence.getAdresse())
                .telephone(agence.getTelephone())
                .build();
    }

    public Agence toEntity(AgenceDTO dto) {
        if (dto == null) {
            return null;
        }
        Agence agence = new Agence();
        agence.setIdAgence(dto.getIdAgence());
        agence.setNom(dto.getNom());
        agence.setVille(dto.getVille());
        agence.setAdresse(dto.getAdresse());
        agence.setTelephone(dto.getTelephone());
        return agence;
    }

    public List<AgenceDTO> toDtoList(List<Agence> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toDto).toList();
    }
}

