package tn.esprit.autoloc.web.mapper;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.dto.PaiementDTO;

@Component
@RequiredArgsConstructor
public class ContratMapper {

    private final PaiementMapper paiementMapper;

    public ContratDTO toDto(Contrat contrat) {
        if (contrat == null) {
            return null;
        }
        List<PaiementDTO> paiements = (contrat.getPaiements() != null)
                ? contrat.getPaiements().stream().map(paiementMapper::toDto).toList()
                : Collections.emptyList();

        return ContratDTO.builder()
                .idContrat(contrat.getIdContrat())
                .dateSignature(contrat.getDateSignature())
                .montantTotal(contrat.getMontantTotal())
                .valide(contrat.isValide())
                .idReservation(contrat.getReservation() != null ? contrat.getReservation().getIdReservation() : null)
                .paiements(paiements)
                .build();
    }

    public Contrat toEntity(ContratDTO dto) {
        if (dto == null) {
            return null;
        }
        Contrat contrat = new Contrat();
        contrat.setIdContrat(dto.getIdContrat());
        contrat.setDateSignature(dto.getDateSignature());
        contrat.setMontantTotal(dto.getMontantTotal());
        contrat.setValide(dto.isValide());
        if (dto.getIdReservation() != null) {
            Reservation reservation = new Reservation();
            reservation.setIdReservation(dto.getIdReservation());
            contrat.setReservation(reservation);
        }
        if (dto.getPaiements() != null) {
            List<Paiement> paiements = dto.getPaiements().stream().map(pDto -> {
                Paiement p = paiementMapper.toEntity(pDto);
                p.setContrat(contrat);
                return p;
            }).toList();
            contrat.setPaiements(paiements);
        }
        return contrat;
    }

    public List<ContratDTO> toDtoList(List<Contrat> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(this::toDto).toList();
    }
}

