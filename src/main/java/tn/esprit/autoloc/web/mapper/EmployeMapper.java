package tn.esprit.autoloc.web.mapper;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.web.dto.EmployeDTO;

@Component
public class EmployeMapper {

    public EmployeDTO toDto(Employe employe) {
        if (employe == null) {
            return null;
        }
        return EmployeDTO.builder()
                .idEmploye(employe.getIdEmploye())
                .nom(employe.getNom())
                .prenom(employe.getPrenom())
                .role(employe.getRole())
                .idAgence(employe.getAgence() != null ? employe.getAgence().getIdAgence() : null)
                .nomAgence(employe.getAgence() != null ? employe.getAgence().getNom() : null)
                .build();
    }

    public Employe toEntity(EmployeDTO dto) {
        if (dto == null) {
            return null;
        }
        Employe employe = new Employe();
        employe.setIdEmploye(dto.getIdEmploye());
        employe.setNom(dto.getNom());
        employe.setPrenom(dto.getPrenom());
        employe.setRole(dto.getRole());
        if (dto.getIdAgence() != null) {
            Agence agence = new Agence();
            agence.setIdAgence(dto.getIdAgence());
            employe.setAgence(agence);
        }
        return employe;
    }

    public List<EmployeDTO> toDtoList(List<Employe> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toDto).toList();
    }
}

