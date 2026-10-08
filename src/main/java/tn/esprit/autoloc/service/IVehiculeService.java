package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Vehicule;

public interface IVehiculeService {

    Vehicule add(Vehicule entity);

    Vehicule update(Long id, Vehicule entity);

    Vehicule getById(Long id);

    List<Vehicule> getAll();

    void delete(Long id);
}