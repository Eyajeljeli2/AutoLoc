# Atelier 8 : Spring Boot Task Scheduling (@Scheduled & Cron)

Ce document récapitule l'implémentation complète des **tâches planifiées (Schedulers)** pour le projet **AutoLoc**.

---

## 1. Activation de la planification

La planification des tâches asynchrones est activée dans la classe principale [`AutolocApiApplication`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/AutolocApiApplication.java) via l'annotation :
```java
@SpringBootApplication
@EnableScheduling
public class AutolocApiApplication { ... }
```

---

## 2. Services planifiés implémentés

### A. Identification des contrats à échéance sous 48 heures

* **Classe :** [`ContratSchedulerService`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/service/ContratSchedulerService.java)
* **Méthode :** `identifierContratsArrivantAEcheance()`
* **Planification Cron :** `0 0 7 * * *` *(Tous les jours à 7h00 du matin)*
* **Expression Cron détaillée :**
  | Seconde | Minute | Heure | Jour du mois | Mois | Jour de la semaine |
  | :---: | :---: | :---: | :---: | :---: | :---: |
  | `0` | `0` | `7` | `*` | `*` | `*` (ou `MON-FRI`) |
* **Règle métier :**
  1. Calcule la fenêtre temporelle `[aujourd'hui, aujourd'hui + 48h]` (`today.plusDays(2)`).
  2. Recherche via requête JPQL optimisée dans [`IContratRepository`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/repository/IContratRepository.java) :
     - Les contrats valides (`valide = true`).
     - Dont la date de fin de réservation est comprise dans cet intervalle (`dateFin BETWEEN :start AND :end`).
     - Dont le statut de réservation n'est ni `ANNULEE`, ni `TERMINEE`.
  3. Journalise chaque contrat détecté sous forme de log d'avertissement (`log.warn(...)`) avec l'ID du contrat, le nom/prénom du client, la date d'échéance et le montant.
  4. Retourne la liste des contrats sous forme de `List<ContratDTO>`.

---

### B. Clôture des locations et libération des véhicules

* **Classe :** [`VehiculeSchedulerService`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/service/VehiculeSchedulerService.java)
* **Méthode :** `remettreStatutVehiculeDisponible()`
* **Planification Cron :** `0 0 1 * * *` *(Chaque jour à 1h00 du matin)*
* **Expression Cron détaillée :**
  | Seconde | Minute | Heure | Jour du mois | Mois | Jour de la semaine |
  | :---: | :---: | :---: | :---: | :---: | :---: |
  | `0` | `0` | `1` | `*` | `*` | `*` |
* **Règle métier :**
  1. Recherche les réservations dont la `dateFin` est échue (`dateFin <= aujourd'hui`) et dont le statut n'est pas encore `TERMINEE` ni `ANNULEE`.
  2. Fait basculer le statut de chaque réservation à `StatutReservation.TERMINEE`.
  3. Pour chaque véhicule rattaché :
     - Si le véhicule n'est pas en panne (`statut != StatutVehicule.MAINTENANCE`), son statut repasse automatiquement à `StatutVehicule.DISPONIBLE`.
     - Si le véhicule est en cours d'intervention de maintenance, son statut `MAINTENANCE` est préservé.
  4. Persiste les entités en base (`@Transactional`) et journalise chaque libération (`log.info(...)`).
  5. Retourne la liste des véhicules libérés sous forme de `List<VehiculeDTO>`.

---

## 3. Configuration externalisée (`application.properties`)

Les expressions cron sont configurables de manière centralisée dans [`application.properties`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/resources/application.properties) :

```properties
# Schedulers (Atelier 8)
autoloc.scheduler.contrats-echeance.cron=0 0 7 * * *
autoloc.scheduler.vehicules-disponibles.cron=0 0 1 * * *
```

---

## 4. Exposition REST pour déclenchement manuel et captures d'écran

Pour faciliter l'évaluation, les démonstrations et les captures d'écran sans devoir attendre 1h00 ou 7h00 du matin, un contrôleur REST dédié a été ajouté :

### Contrôleur : [`SchedulerController`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/controller/SchedulerController.java) (`/api/schedulers`)

| Méthode | Endpoint | Description | Code retour |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/schedulers/contrats-echeance-48h` | Déclenche immédiatement l'identification des contrats sous 48h | `200 OK` |
| `POST` | `/api/schedulers/liberer-vehicules` | Déclenche immédiatement la libération des véhicules et clôture des réservations | `200 OK` |

---

## 5. Validation par tests automatisés

La classe de test [`SchedulerServiceTest`](file:///c:/Users/DELL/Downloads/autoloc-api/src/test/java/tn/esprit/autoloc/service/SchedulerServiceTest.java) valide l'intégralité du comportement :
* **Test 1 :** Vérifie que seuls les contrats sous 48h sont identifiés (exclusion des contrats lointains et des contrats déjà terminés).
* **Test 2 :** Vérifie la bascule de réservation à `TERMINEE` et du véhicule à `DISPONIBLE`.
* **Test 3 :** Vérifie qu'un véhicule en statut `MAINTENANCE` conserve bien son statut.

Résultat d'exécution Maven :
```text
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 6. Guide pour les captures d'écran

1. **Lancer le backend :**
   ```powershell
   $env:JAVA_HOME = "C:\Users\DELL\.jdks\openjdk-21"; .\mvnw.cmd spring-boot:run
   ```
2. **Swagger UI :**
   - Accéder à `http://localhost:8082/api/swagger-ui/index.html`.
   - Dérouler la section **Schedulers** :
     - Tester `GET /api/schedulers/contrats-echeance-48h`
     - Tester `POST /api/schedulers/liberer-vehicules`
3. **Console Spring Boot :**
   - Capturer les logs formatés affichant :
     - `[Scheduler Contrats 7h00] ... contrat(s) identifié(s) arrivant à échéance sous 48 heures`
     - `[Scheduler Véhicules 1h00] ... réservation(s) fermée(s) et ... véhicule(s) libéré(s)`
4. **Postman :**
   - Exécuter les 2 requêtes du dossier **Atelier 8 - Schedulers**.
