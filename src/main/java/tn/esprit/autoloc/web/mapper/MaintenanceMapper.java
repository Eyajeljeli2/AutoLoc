package tn.esprit.autoloc.web.mapper;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.MaintenanceDTO;

@Component
public class MaintenanceMapper {

    public MaintenanceDTO toDto(Maintenance maintenance) {
        if (maintenance == null) {
            return null;
        }
        return MaintenanceDTO.builder()
                .idMaintenance(maintenance.getIdMaintenance())
                .dateDebut(maintenance.getDateDebut())
                .dateFin(maintenance.getDateFin())
                .description(maintenance.getDescription())
                .idVehicule(maintenance.getVehicule() != null ? maintenance.getVehicule().getIdVehicule() : null)
                .immatriculationVehicule(maintenance.getVehicule() != null ? maintenance.getVehicule().getImmatriculation() : null)
                .build();
    }

    public Maintenance toEntity(MaintenanceDTO dto) {
        if (dto == null) {
            return null;
        }
        Maintenance maintenance = new Maintenance();
        maintenance.setIdMaintenance(dto.getIdMaintenance());
        maintenance.setDateDebut(dto.getDateDebut());
        maintenance.setDateFin(dto.getDateFin());
        maintenance.setDescription(dto.getDescription());
        if (dto.getIdVehicule() != null) {
            Vehicule vehicule = new Vehicule();
            vehicule.setIdVehicule(dto.getIdVehicule());
            maintenance.setVehicule(vehicule);
        }
        return maintenance;
    }

    public List<MaintenanceDTO> toDtoList(List<Maintenance> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toDto).toList();
    }
}

