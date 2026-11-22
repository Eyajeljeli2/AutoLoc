# Atelier 7 : Spring Data JPA Affectations

Ce document récapitule l'implémentation complète des **5 services d'affectation** et de leurs contrôleurs associés pour le projet **AutoLoc**.

---

## 1. Respect strict des signatures

Toutes les signatures demandées sont implémentées à l'identique dans [`IAffectationService`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/service/IAffectationService.java) et [`AffectationServiceImpl`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/service/AffectationServiceImpl.java), et sont également disponibles au niveau des services de domaine respectifs ([`IReservationService`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/service/IReservationService.java), [`IVehiculeService`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/service/IVehiculeService.java), [`IContratService`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/service/IContratService.java)) :

```java
ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom);

VehiculeDTO affecterVehiculeAEquipement(Long idVehicule, Long idEquipement);

VehiculeDTO affecterMaintenanceAvehicule(String description, String immatriculation);

ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin);

ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO);
```

> **Règle architecturale respectée :** Les réponses des services sont exclusivement des **DTOs** et les contrôleurs sont de purs relais HTTP sans **aucune logique métier** (Clean Code & SoC).

---

## 2. Détail des 5 services d'affectation

### Service 1 : `affecterReservationAClient`
* **Signature :** `ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom)`
* **Logique métier :**
  1. Vérifie l'existence de la réservation `idReservation` (sinon `ResourceNotFoundException`).
  2. Recherche le client par son `nom` et `prenom` (recherche insensible à la casse via `IClientRepository.findFirstByNomIgnoreCaseAndPrenomIgnoreCase`).
  3. Associe le client à la réservation (`reservation.setClient(client)`).
  4. Sauvegarde la réservation et renvoie le DTO enrichi [`ReservationResponseDTO`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/dto/ReservationResponseDTO.java) (contenant les coordonnées du client et du véhicule).

### Service 2 : `affecterVehiculeAEquipement`
* **Signature :** `VehiculeDTO affecterVehiculeAEquipement(Long idVehicule, Long idEquipement)`
* **Logique métier :**
  1. Charge l'entité [`Vehicule`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/domain/Vehicule.java) (propriétaire de la relation `@ManyToMany` `vehicule_equipement`).
  2. Charge l'entité [`Equipement`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/domain/Equipement.java).
  3. Ajoute de manière bidirectionnelle l'équipement à la collection sans doublon.
  4. Sauvegarde le véhicule et retourne le [`VehiculeDTO`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/dto/VehiculeDTO.java) à jour avec sa liste d'identifiants d'équipements (`equipementIds`).

### Service 3 : `affecterMaintenanceAvehicule`
* **Signature :** `VehiculeDTO affecterMaintenanceAvehicule(String description, String immatriculation)`
* **Logique métier :**
  1. Recherche le véhicule par son `immatriculation` unique via `IVehiculeRepository.findByImmatriculation`.
  2. Récupère la maintenance correspondante par sa description ou instancie une nouvelle intervention datée du jour (`LocalDate.now()`).
  3. Lie la maintenance au véhicule et persiste la maintenance.
  4. Fait basculer le statut du véhicule à `StatutVehicule.MAINTENANCE`.
  5. Sauvegarde le véhicule et renvoie le [`VehiculeDTO`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/dto/VehiculeDTO.java).

### Service 4 : `affecterContratAReservation`
* **Signature :** `ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin)`
* **Logique métier :**
  1. Valide la chronologie des dates (`dateFin` non antérieure à `dateDebut`).
  2. Recherche la réservation correspondant à l'intervalle `[dateDebut, dateFin]` via `IReservationRepository.findFirstByDateDebutAndDateFinAndContratIsNull` ou `findFirstByDateDebutAndDateFin`.
  3. Récupère le contrat existant ou en crée un nouveau avec `dateSignature`.
  4. Calcule dynamiquement le `montantTotal` (durée en jours $\times$ tarif journalier du véhicule) si non renseigné.
  5. Active le contrat (`valide = true`), passe le statut de la réservation à `StatutReservation.CONFIRMEE`.
  6. Sauvegarde et retourne le [`ContratDTO`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/dto/ContratDTO.java).

### Service 5 : `creerReservationEtContratAssocie`
* **Signature :** `ReservationResponseDTO creerReservationEtContratAssocie(ReservationWithContratRequestDTO requestDTO)`
* **Logique métier :**
  1. Transaction atomique (`@Transactional`) assurant la cohérence totale.
  2. Valide les données de requête [`ReservationWithContratRequestDTO`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/dto/ReservationWithContratRequestDTO.java).
  3. Associe le client et le véhicule existants, crée la réservation.
  4. Calcule le montant total en fonction de la durée et du tarif journalier du véhicule.
  5. Crée le contrat lié avec sa signature, montant, statut de validité et éventuels paiements initiaux.
  6. Passe le statut du véhicule à `StatutVehicule.LOUE` lorsque la réservation est confirmée.
  7. Retourne le DTO complet [`ReservationResponseDTO`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/dto/ReservationResponseDTO.java) encapsulant la réservation, le client, le véhicule et l'objet imbriqué [`ContratDTO`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/dto/ContratDTO.java).

---

## 3. Endpoints REST exposés

### Contrôleur central : [`AffectationController`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/controller/AffectationController.java) (`/api/affectations`)

| Méthode | Endpoint | Description | Code retour |
| :--- | :--- | :--- | :--- |
| `PUT` | `/api/affectations/reservations/{idReservation}/client?nom=...&prenom=...` | Affecter réservation à client | `200 OK` |
| `PUT` | `/api/affectations/vehicules/{idVehicule}/equipements/{idEquipement}` | Affecter équipement à véhicule | `200 OK` |
| `POST` | `/api/affectations/maintenances/vehicule?description=...&immatriculation=...` | Affecter maintenance à véhicule | `200 OK` |
| `POST` | `/api/affectations/contrats/reservation?dateSignature=...&dateDebut=...&dateFin=...` | Affecter contrat à réservation | `200 OK` |
| `POST` | `/api/affectations/reservations/avec-contrat` | Créer réservation + contrat | `201 CREATED` |

> Les contrôleurs de domaine ([`ReservationController`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/controller/ReservationController.java), [`VehiculeController`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/controller/VehiculeController.java), [`ContratController`](file:///c:/Users/DELL/Downloads/autoloc-api/src/main/java/tn/esprit/autoloc/web/controller/ContratController.java)) exposent également ces opérations comme alias d'API REST standards.

---

## 4. Tests unitaires et d'intégration

Deux suites de tests dédiées ont été ajoutées et validées :
1. [`AffectationServiceTest`](file:///c:/Users/DELL/Downloads/autoloc-api/src/test/java/tn/esprit/autoloc/service/AffectationServiceTest.java) :
   - 6 tests `@SpringBootTest` avec base de données et gestion transactionnelle couvrant les 5 affectations et les cas d'erreur (404, dates incohérentes, validation).
2. [`AffectationControllerTest`](file:///c:/Users/DELL/Downloads/autoloc-api/src/test/java/tn/esprit/autoloc/web/controller/AffectationControllerTest.java) :
   - 5 tests unitaires vérifiant la stricte délégation du contrôleur vers le service et les codes de statut HTTP `200 OK` / `201 CREATED`.

Résultat d'exécution :
```bash
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

---

## 5. Guide pour les captures d'écran (Rapport d'atelier)

1. **Lancement de l'API :**
   ```powershell
   $env:JAVA_HOME = "C:\Users\DELL\.jdks\openjdk-21"; .\mvnw.cmd spring-boot:run
   ```
2. **Swagger UI :**
   - Ouvrir `http://localhost:8082/api/swagger-ui/index.html` (ou `http://localhost:8082/api/swagger-ui.html`).
   - Dérouler le tag **Affectations** pour voir les 5 endpoints documentés avec schémas DTO.
3. **Collection Postman :**
   - Importer `AutoLoc.postman_collection.json`.
   - Exécuter le dossier **Atelier 7 - Affectations** (les 5 requêtes pré-configurées avec assertions automatisées).
