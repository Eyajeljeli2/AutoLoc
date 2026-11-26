package tn.esprit.autoloc.service;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.mapper.ContratMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContratSchedulerService {

    private final IContratRepository contratRepository;
    private final ContratMapper contratMapper;

    /**
     * Tous les jours de la semaine à 7h00 du matin.
     * Identifie les contrats arrivant à échéance sous 48 heures.
     */
    @Scheduled(cron = "${autoloc.scheduler.contrats-echeance.cron:0 0 7 * * *}")
    @Transactional(readOnly = true)
    public List<ContratDTO> identifierContratsArrivantAEcheance() {
        LocalDate aujourdhui = LocalDate.now();
        LocalDate dans48Heures = aujourdhui.plusDays(2);

        log.info("[Scheduler Contrats 7h00] Recherche des contrats arrivant à échéance entre le {} et le {}...",
                aujourdhui, dans48Heures);

        List<Contrat> contrats = contratRepository.findContratsArrivantAEcheance(aujourdhui, dans48Heures);

        if (contrats.isEmpty()) {
            log.info("[Scheduler Contrats 7h00] Aucun contrat n'arrive à échéance sous 48 heures.");
        } else {
            log.info("[Scheduler Contrats 7h00] {} contrat(s) identifié(s) arrivant à échéance sous 48 heures :", contrats.size());
            for (Contrat c : contrats) {
                String clientInfo = (c.getReservation() != null && c.getReservation().getClient() != null)
                        ? c.getReservation().getClient().getNom() + " " + c.getReservation().getClient().getPrenom()
                        : "Client N/A";
                LocalDate dateFin = (c.getReservation() != null) ? c.getReservation().getDateFin() : null;
                log.warn(" -> Contrat #{} | Échéance : {} | Client : {} | Montant : {} TND | Statut résa : {}",
                        c.getIdContrat(), dateFin, clientInfo, c.getMontantTotal(),
                        c.getReservation() != null ? c.getReservation().getStatut() : "N/A");
            }
        }

        return contratMapper.toDtoList(contrats);
    }
}
