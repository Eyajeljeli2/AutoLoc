package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.repository.IEmployeRepository;
import tn.esprit.autoloc.web.dto.EmployeDTO;
import tn.esprit.autoloc.web.mapper.EmployeMapper;

@Service
@RequiredArgsConstructor
public class EmployeServiceImpl implements IEmployeService {

    private final IEmployeRepository employeRepository;
    private final IAgenceRepository agenceRepository;
    private final EmployeMapper employeMapper;

    @Override
    public EmployeDTO create(EmployeDTO dto) {
        if (dto.getIdEmploye() != null) {
            throw new IllegalArgumentException("Un nouvel employé ne doit pas avoir d'identifiant");
        }
        validerEmploye(dto);
        Employe entity = employeMapper.toEntity(dto);
        if (dto.getIdAgence() != null) {
            Agence agence = agenceRepository.findById(dto.getIdAgence())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence", dto.getIdAgence()));
            entity.setAgence(agence);
        }
        Employe saved = employeRepository.save(entity);
        return employeMapper.toDto(saved);
    }

    @Override
    public EmployeDTO findById(Long id) {
        Employe employe = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
        return employeMapper.toDto(employe);
    }

    @Override
    public List<EmployeDTO> findAll() {
        return employeMapper.toDtoList(employeRepository.findAll());
    }

    @Override
    public EmployeDTO update(Long id, EmployeDTO dto) {
        validerEmploye(dto);
        Employe existant = employeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", id));
        existant.setNom(dto.getNom());
        existant.setPrenom(dto.getPrenom());
        existant.setRole(dto.getRole());
        if (dto.getIdAgence() != null) {
            Agence agence = agenceRepository.findById(dto.getIdAgence())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence", dto.getIdAgence()));
            existant.setAgence(agence);
        } else {
            existant.setAgence(null);
        }
        return employeMapper.toDto(employeRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!employeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employe", id);
        }
        employeRepository.deleteById(id);
    }

    private void validerEmploye(EmployeDTO dto) {
        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'employé ne doit pas être vide");
        }
        if (dto.getPrenom() == null || dto.getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le prénom de l'employé ne doit pas être vide");
        }
        if (dto.getRole() == null) {
            throw new IllegalArgumentException("Le rôle/poste de l'employé ne doit pas être vide");
        }
    }
}
