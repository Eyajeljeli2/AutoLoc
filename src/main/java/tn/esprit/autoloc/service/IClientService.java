package tn.esprit.autoloc.service;

import java.util.List;
import tn.esprit.autoloc.domain.Client;

public interface IClientService {

    Client add(Client entity);

    Client update(Long id, Client entity);

    Client getById(Long id);

    List<Client> getAll();

    void delete(Long id);
}