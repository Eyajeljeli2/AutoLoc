package tn.esprit.autoloc.web.mapper;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.ReservationDTO;

@Component
public class ReservationMapper {

    public ReservationDTO toDto(Reservation reservation) {
        if (reservation == null) {
            return null;
        }
        return ReservationDTO.builder()
                .idReservation(reservation.getIdReservation())
                .dateDebut(reservation.getDateDebut())
                .dateFin(reservation.getDateFin())
                .statut(reservation.getStatut())
                .idClient(reservation.getClient() != null ? reservation.getClient().getIdClient() : null)
                .nomClient(reservation.getClient() != null ? reservation.getClient().getNom() + " " + reservation.getClient().getPrenom() : null)
                .idVehicule(reservation.getVehicule() != null ? reservation.getVehicule().getIdVehicule() : null)
                .immatriculationVehicule(reservation.getVehicule() != null ? reservation.getVehicule().getImmatriculation() : null)
                .build();
    }

    public Reservation toEntity(ReservationDTO dto) {
        if (dto == null) {
            return null;
        }
        Reservation reservation = new Reservation();
        reservation.setIdReservation(dto.getIdReservation());
        reservation.setDateDebut(dto.getDateDebut());
        reservation.setDateFin(dto.getDateFin());
        reservation.setStatut(dto.getStatut());
        if (dto.getIdClient() != null) {
            Client client = new Client();
            client.setIdClient(dto.getIdClient());
            reservation.setClient(client);
        }
        if (dto.getIdVehicule() != null) {
            Vehicule vehicule = new Vehicule();
            vehicule.setIdVehicule(dto.getIdVehicule());
            reservation.setVehicule(vehicule);
        }
        return reservation;
    }

    public List<ReservationDTO> toDtoList(List<Reservation> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toDto).toList();
    }
}

