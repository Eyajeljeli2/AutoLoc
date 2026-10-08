package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Reservation;

public interface IReservationService {

    List<Reservation> getAll();
}