package tn.esprit.autoloc.service;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.mapper.ContratMapper;

@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;
    private final ContratMapper contratMapper;

    @Override
    public ContratDTO create(ContratDTO dto) {
        if (dto.getIdContrat() != null) {
            throw new IllegalArgumentException("Un nouveau contrat ne doit pas avoir d'identifiant");
        }
        validerContrat(dto);
        Contrat entity = contratMapper.toEntity(dto);
        if (dto.getIdReservation() != null) {
            Reservation res = reservationRepository.findById(dto.getIdReservation())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation", dto.getIdReservation()));
            entity.setReservation(res);
        }
        if (entity.getPaiements() != null) {
            for (Paiement p : entity.getPaiements()) {
                p.setContrat(entity);
            }
        }
        Contrat saved = contratRepository.save(entity);
        return contratMapper.toDto(saved);
    }

    @Override
    public ContratDTO findById(Long id) {
        Contrat contrat = contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
        return contratMapper.toDto(contrat);
    }

    @Override
    public List<ContratDTO> findAll() {
        return contratMapper.toDtoList(contratRepository.findAll());
    }

    @Override
    public ContratDTO update(Long id, ContratDTO dto) {
        validerContrat(dto);
        Contrat existant = contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", id));
        existant.setDateSignature(dto.getDateSignature());
        existant.setMontantTotal(dto.getMontantTotal());
        existant.setValide(dto.isValide());
        if (dto.getIdReservation() != null) {
            Reservation res = reservationRepository.findById(dto.getIdReservation())
                    .orElseThrow(() -> new ResourceNotFoundException("Reservation", dto.getIdReservation()));
            existant.setReservation(res);
        }
        return contratMapper.toDto(contratRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!contratRepository.existsById(id)) {
            throw new ResourceNotFoundException("Contrat", id);
        }
        contratRepository.deleteById(id);
    }

    private void validerContrat(ContratDTO dto) {
        if (dto.getMontantTotal() != null && dto.getMontantTotal().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le montant total ne peut pas être négatif");
        }
    }
}
