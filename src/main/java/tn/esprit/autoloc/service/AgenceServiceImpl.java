package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;
import tn.esprit.autoloc.web.dto.AgenceDTO;
import tn.esprit.autoloc.web.mapper.AgenceMapper;

@Service
@RequiredArgsConstructor
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;
    private final AgenceMapper agenceMapper;

    @Override
    public AgenceDTO create(AgenceDTO dto) {
        if (dto.getIdAgence() != null) {
            throw new IllegalArgumentException("Une nouvelle agence ne doit pas avoir d'identifiant");
        }
        validerAgence(dto);
        Agence entity = agenceMapper.toEntity(dto);
        Agence saved = agenceRepository.save(entity);
        return agenceMapper.toDto(saved);
    }

    @Override
    public AgenceDTO findById(Long id) {
        Agence agence = agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
        return agenceMapper.toDto(agence);
    }

    @Override
    public List<AgenceDTO> findAll() {
        return agenceMapper.toDtoList(agenceRepository.findAll());
    }

    @Override
    public AgenceDTO update(Long id, AgenceDTO dto) {
        validerAgence(dto);
        Agence existant = agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
        existant.setNom(dto.getNom());
        existant.setVille(dto.getVille());
        existant.setAdresse(dto.getAdresse());
        existant.setTelephone(dto.getTelephone());
        return agenceMapper.toDto(agenceRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", id);
        }
        agenceRepository.deleteById(id);
    }

    private void validerAgence(AgenceDTO dto) {
        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'agence ne doit pas être vide");
        }
        if (dto.getVille() == null || dto.getVille().trim().isEmpty()) {
            throw new IllegalArgumentException("La ville de l'agence ne doit pas être vide");
        }
        if (dto.getAdresse() == null || dto.getAdresse().trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse de l'agence ne doit pas être vide");
        }
    }
}
