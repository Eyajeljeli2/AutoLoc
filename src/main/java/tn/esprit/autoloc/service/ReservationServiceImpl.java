package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.ReservationDTO;
import tn.esprit.autoloc.web.mapper.ReservationMapper;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IClientRepository clientRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public ReservationDTO create(ReservationDTO dto) {
        if (dto.getIdReservation() != null) {
            throw new IllegalArgumentException("Une nouvelle réservation ne doit pas avoir d'identifiant");
        }
        validerReservation(dto);
        Reservation entity = reservationMapper.toEntity(dto);
        if (dto.getIdClient() != null) {
            Client client = clientRepository.findById(dto.getIdClient())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", dto.getIdClient()));
            entity.setClient(client);
        }
        if (dto.getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(dto.getIdVehicule())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", dto.getIdVehicule()));
            entity.setVehicule(vehicule);
        }
        Reservation saved = reservationRepository.save(entity);
        return reservationMapper.toDto(saved);
    }

    @Override
    public ReservationDTO findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        return reservationMapper.toDto(reservation);
    }

    @Override
    public List<ReservationDTO> findAll() {
        return reservationMapper.toDtoList(reservationRepository.findAll());
    }

    @Override
    public ReservationDTO update(Long id, ReservationDTO dto) {
        validerReservation(dto);
        Reservation existant = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", id));
        existant.setDateDebut(dto.getDateDebut());
        existant.setDateFin(dto.getDateFin());
        existant.setStatut(dto.getStatut());
        if (dto.getIdClient() != null) {
            Client client = clientRepository.findById(dto.getIdClient())
                    .orElseThrow(() -> new ResourceNotFoundException("Client", dto.getIdClient()));
            existant.setClient(client);
        }
        if (dto.getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(dto.getIdVehicule())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", dto.getIdVehicule()));
            existant.setVehicule(vehicule);
        }
        return reservationMapper.toDto(reservationRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reservation", id);
        }
        reservationRepository.deleteById(id);
    }

    private void validerReservation(ReservationDTO dto) {
        if (dto.getDateDebut() != null && dto.getDateFin() != null
                && dto.getDateFin().isBefore(dto.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
