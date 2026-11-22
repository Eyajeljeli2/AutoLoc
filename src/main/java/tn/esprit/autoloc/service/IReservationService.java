package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.web.dto.ReservationDTO;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;

public interface IReservationService {

    ReservationDTO create(ReservationDTO dto);

    ReservationDTO findById(Long id);

    List<ReservationDTO> findAll();

    ReservationDTO update(Long id, ReservationDTO dto);

    void deleteById(Long id);

    ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom);

    ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO);
}
