package tn.esprit.autoloc.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.util.BeanCopyUtils;

@Service
@RequiredArgsConstructor
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;

    @Override
    @Transactional
    public Vehicule add(Vehicule entity) {
        return vehiculeRepository.save(entity);
    }

    @Override
    @Transactional
    public Vehicule update(Long id, Vehicule entity) {
        Vehicule existing = getById(id);
        BeanCopyUtils.copyNonNull(entity, existing);
        return vehiculeRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public Vehicule getById(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vehicule introuvable : id = " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vehicule> getAll() {
        return vehiculeRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        vehiculeRepository.deleteById(id);
    }
}