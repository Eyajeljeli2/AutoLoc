package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.web.dto.ClientDTO;
import tn.esprit.autoloc.web.mapper.ClientMapper;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientDTO create(ClientDTO dto) {
        if (dto.getIdClient() != null) {
            throw new IllegalArgumentException("Un nouveau client ne doit pas avoir d'identifiant");
        }
        validerClient(dto);
        Client entity = clientMapper.toEntity(dto);
        Client saved = clientRepository.save(entity);
        return clientMapper.toDto(saved);
    }

    @Override
    public ClientDTO findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        return clientMapper.toDto(client);
    }

    @Override
    public List<ClientDTO> findAll() {
        return clientMapper.toDtoList(clientRepository.findAll());
    }

    @Override
    public ClientDTO update(Long id, ClientDTO dto) {
        validerClient(dto);
        Client existant = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        existant.setNom(dto.getNom());
        existant.setPrenom(dto.getPrenom());
        existant.setEmail(dto.getEmail());
        existant.setTelephone(dto.getTelephone());
        existant.setNumPermis(dto.getNumPermis());
        existant.setDateInscription(dto.getDateInscription());
        return clientMapper.toDto(clientRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", id);
        }
        clientRepository.deleteById(id);
    }

    private void validerClient(ClientDTO dto) {
        if (dto.getNom() == null || dto.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du client ne doit pas être vide");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'e-mail du client ne doit pas être vide");
        }
    }
}
