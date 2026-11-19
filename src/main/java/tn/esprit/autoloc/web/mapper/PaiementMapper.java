package tn.esprit.autoloc.web.mapper;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.web.dto.PaiementDTO;

@Component
public class PaiementMapper {

    public PaiementDTO toDto(Paiement paiement) {
        if (paiement == null) {
            return null;
        }
        return PaiementDTO.builder()
                .idPaiement(paiement.getIdPaiement())
                .montant(paiement.getMontant())
                .datePaiement(paiement.getDatePaiement())
                .modePaiement(paiement.getModePaiement())
                .idContrat(paiement.getContrat() != null ? paiement.getContrat().getIdContrat() : null)
                .build();
    }

    public Paiement toEntity(PaiementDTO dto) {
        if (dto == null) {
            return null;
        }
        Paiement paiement = new Paiement();
        paiement.setIdPaiement(dto.getIdPaiement());
        paiement.setMontant(dto.getMontant());
        paiement.setDatePaiement(dto.getDatePaiement());
        paiement.setModePaiement(dto.getModePaiement());
        if (dto.getIdContrat() != null) {
            Contrat contrat = new Contrat();
            contrat.setIdContrat(dto.getIdContrat());
            paiement.setContrat(contrat);
        }
        return paiement;
    }

    public List<PaiementDTO> toDtoList(List<Paiement> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toDto).toList();
    }
}

