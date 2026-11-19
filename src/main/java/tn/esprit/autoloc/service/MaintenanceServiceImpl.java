package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.MaintenanceDTO;
import tn.esprit.autoloc.web.mapper.MaintenanceMapper;

@Service
@RequiredArgsConstructor
public class MaintenanceServiceImpl implements IMaintenanceService {

    private final IMaintenanceRepository maintenanceRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final MaintenanceMapper maintenanceMapper;

    @Override
    public MaintenanceDTO create(MaintenanceDTO dto) {
        if (dto.getIdMaintenance() != null) {
            throw new IllegalArgumentException("Une nouvelle maintenance ne doit pas avoir d'identifiant");
        }
        validerMaintenance(dto);
        Maintenance entity = maintenanceMapper.toEntity(dto);
        if (dto.getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(dto.getIdVehicule())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", dto.getIdVehicule()));
            entity.setVehicule(vehicule);
        }
        Maintenance saved = maintenanceRepository.save(entity);
        return maintenanceMapper.toDto(saved);
    }

    @Override
    public MaintenanceDTO findById(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        return maintenanceMapper.toDto(maintenance);
    }

    @Override
    public List<MaintenanceDTO> findAll() {
        return maintenanceMapper.toDtoList(maintenanceRepository.findAll());
    }

    @Override
    public MaintenanceDTO update(Long id, MaintenanceDTO dto) {
        validerMaintenance(dto);
        Maintenance existant = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", id));
        existant.setDateDebut(dto.getDateDebut());
        existant.setDateFin(dto.getDateFin());
        existant.setDescription(dto.getDescription());
        if (dto.getIdVehicule() != null) {
            Vehicule vehicule = vehiculeRepository.findById(dto.getIdVehicule())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicule", dto.getIdVehicule()));
            existant.setVehicule(vehicule);
        }
        return maintenanceMapper.toDto(maintenanceRepository.save(existant));
    }

    @Override
    public void deleteById(Long id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Maintenance", id);
        }
        maintenanceRepository.deleteById(id);
    }

    private void validerMaintenance(MaintenanceDTO dto) {
        if (dto.getDateDebut() != null && dto.getDateFin() != null
                && dto.getDateFin().isBefore(dto.getDateDebut())) {
            throw new IllegalArgumentException("La date de fin ne peut pas être antérieure à la date de début");
        }
    }
}
