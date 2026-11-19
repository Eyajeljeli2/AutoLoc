# Atelier 6 - Clean Code : DTO, Mappers, Validation et Gestion Centralisée des Erreurs

## 1. Contexte et Objectifs

Cet atelier met en œuvre les principes du **Clean Code** et les bonnes pratiques d'architecture logicielle :
1. **Découplage API / Base de données (DTO Pattern) :** Remplacer l'exposition directe des entités JPA par des objets de transfert de données (*Data Transfer Objects* - DTO) afin d'optimiser la bande passante, masquer la structure interne de la base de données et éliminer définitivement les risques de boucles infinies de sérialisation Jackson.
2. **Mappers dédiés :** Centraliser les transformations bidirectionnelles Entité $\leftrightarrow$ DTO dans des composants Spring (`@Component`).
3. **Validation déclarative des données :** Utiliser les annotations de l'API standard Jakarta Validation (`spring-boot-starter-validation`) sur les DTO pour valider automatiquement les entrées HTTP avant l'exécution du code métier.
4. **Gestion centralisée des erreurs :** Intercepter globalement toutes les exceptions et erreurs de validation via une classe annotée `@RestControllerAdvice` pour produire des réponses HTTP JSON standardisées.

---

## 2. Architecture des DTOs et Mappers

### 2.1 Les 9 DTOs (package `tn.esprit.autoloc.web.dto`)

| DTO | Champs exposés | Rôle & Optimisations apportées |
|---|---|---|
| `AgenceDTO` | `idAgence`, `nom`, `ville`, `adresse`, `telephone` | Masque les collections `employes` et `vehicules`. |
| `EmployeDTO` | `idEmploye`, `nom`, `prenom`, `role`, `idAgence`, `nomAgence` | Évite l'imbrication lourde de l'objet Agence complet ; expose directement l'ID et le nom de l'agence. Supporte également la désérialisation d'objets agence imbriqués. |
| `VehiculeDTO` | `idVehicule`, `immatriculation`, `marque`, `modele`, `categorie`, `tarifJournalier`, `statut`, `idAgence`, `nomAgence`, `equipementIds` | Remplace les listes d'objets `Equipement` et `Maintenance` par des listes d'identifiants simples. |
| `EquipementDTO` | `idEquipement`, `libelle` | Masque la liste des véhicules associés. |
| `ClientDTO` | `idClient`, `nom`, `prenom`, `email`, `telephone`, `numPermis`, `dateInscription` | Masque la collection des réservations du client. |
| `ReservationDTO` | `idReservation`, `dateDebut`, `dateFin`, `statut`, `idClient`, `nomClient`, `idVehicule`, `immatriculationVehicule` | Échange compact : identifiants et libellés essentiels du client et du véhicule sans charger leurs graphes complets. |
| `ContratDTO` | `idContrat`, `dateSignature`, `montantTotal`, `valide`, `idReservation`, `paiements` | Permet d'embarquer la liste des paiements en DTO (`PaiementDTO`) pour la composition tout en protégeant les clés étrangères. |
| `PaiementDTO` | `idPaiement`, `montant`, `datePaiement`, `modePaiement`, `idContrat` | DTO simple pour la consultation des paiements en lecture seule. |
| `MaintenanceDTO` | `idMaintenance`, `dateDebut`, `dateFin`, `description`, `idVehicule`, `immatriculationVehicule` | Référence le véhicule par son identifiant et son immatriculation. |

### 2.2 Les 9 Mappers (package `tn.esprit.autoloc.web.mapper`)

Chaque entité possède son mapper dédié injecté dans la couche Service :
- `AgenceMapper` : conversion `Agence` $\leftrightarrow$ `AgenceDTO`
- `EmployeMapper` : conversion `Employe` $\leftrightarrow$ `EmployeDTO`
- `VehiculeMapper` : conversion `Vehicule` $\leftrightarrow$ `VehiculeDTO`
- `EquipementMapper` : conversion `Equipement` $\leftrightarrow$ `EquipementDTO`
- `ClientMapper` : conversion `Client` $\leftrightarrow$ `ClientDTO`
- `ReservationMapper` : conversion `Reservation` $\leftrightarrow$ `ReservationDTO`
- `ContratMapper` : conversion `Contrat` $\leftrightarrow$ `ContratDTO`
- `PaiementMapper` : conversion `Paiement` $\leftrightarrow$ `PaiementDTO`
- `MaintenanceMapper` : conversion `Maintenance` $\leftrightarrow$ `MaintenanceDTO`

---

## 3. Validation Déclarative des Données

La dépendance `spring-boot-starter-validation` est activée dans `pom.xml`. Les contraintes de validation appliquées sur les champs des DTO sont :

| Annotation | DTOs concernés | Description du contrôle |
|---|---|---|
| `@NotBlank` | `AgenceDTO.nom`, `AgenceDTO.ville`, `AgenceDTO.adresse`, `EmployeDTO.nom`, `EmployeDTO.prenom`, `VehiculeDTO.immatriculation`, `VehiculeDTO.marque`, `VehiculeDTO.modele`, `EquipementDTO.libelle`, `ClientDTO.nom`, `ClientDTO.prenom`, `ClientDTO.email`, `ClientDTO.numPermis` | Rejette les chaînes nulles, vides ou ne contenant que des espaces. |
| `@NotNull` | `EmployeDTO.role`, `VehiculeDTO.categorie`, `VehiculeDTO.tarifJournalier`, `VehiculeDTO.statut`, `ReservationDTO.dateDebut`, `ReservationDTO.dateFin`, `ReservationDTO.statut`, `ContratDTO.dateSignature`, `ContratDTO.montantTotal`, `PaiementDTO.montant`, `PaiementDTO.datePaiement`, `PaiementDTO.modePaiement`, `MaintenanceDTO.dateDebut` | Rejette les valeurs nulles pour les types obligatoires (enums, dates, montants). |
| `@Size(max = ...)` | `nom`, `ville`, `adresse`, `immatriculation`, `marque`, `modele`, `description`, etc. | Empêche les dépassements de taille des colonnes correspondantes en base de données. |
| `@DecimalMin` | `VehiculeDTO.tarifJournalier` (min 0.0), `ContratDTO.montantTotal` (min 0.0), `PaiementDTO.montant` (min 0.01) | Garantit que les montants et tarifs ne peuvent pas être négatifs (ou nuls pour un paiement). |
| `@Email` | `ClientDTO.email` | Vérifie la conformité syntaxique de l'adresse de courrier électronique. |
| `@Pattern` | `AgenceDTO.telephone`, `ClientDTO.telephone` | Valide le format du numéro de téléphone (8 à 20 chiffres). |
| `@Valid` | `ContratDTO.paiements`, contrôleurs REST (`@Valid @RequestBody ...`) | Déclenche la validation en cascade sur les listes imbriquées et valide automatiquement le corps des requêtes HTTP. |

---

## 4. Gestion Centralisée des Erreurs (`GlobalExceptionHandler`)

La classe `tn.esprit.autoloc.web.controller.GlobalExceptionHandler` intercepte les exceptions et produit des réponses uniformes sous la structure `ErrorResponse` :

```json
{
  "statut": 400,
  "message": "Erreur de validation des champs",
  "horodatage": "2026-11-19T00:33:04",
  "erreurs": {
    "email": "Format d'e-mail invalide",
    "nom": "Le nom du client ne doit pas être vide"
  }
}
```

### Exceptions gérées :
1. **`MethodArgumentNotValidException` (HTTP 400 BAD REQUEST) :**
   Déclenchée automatiquement quand un DTO annoté avec `@Valid` dans un contrôleur échoue à la validation. L'exception extrait chaque champ en erreur avec son message associé dans un dictionnaire `erreurs`.
2. **`ResourceNotFoundException` (HTTP 404 NOT FOUND) :**
   Déclenchée quand un identifiant demandé est inexistant en base de données.
3. **`IllegalArgumentException` (HTTP 400 BAD REQUEST) :**
   Déclenchée pour les règles de gestion métier supplémentaires (ex. date de fin antérieure à la date de début, tentative de création avec un identifiant déjà positionné).
4. **`HttpMessageNotReadableException` (HTTP 400 BAD REQUEST) :**
   Déclenchée lors d'un JSON mal formé ou d'une valeur d'énumération incompatible.
5. **`HttpRequestMethodNotSupportedException` (HTTP 405 METHOD NOT ALLOWED) :**
   Déclenchée lors de l'appel d'un verbe non autorisé (ex. `POST /api/paiements`).
6. **`Exception` (HTTP 500 INTERNAL SERVER ERROR) :**
   Capture générique des anomalies imprévues du serveur.

---

## 5. Exemples d'Exécution

### 5.1 Création réussie avec DTO (`POST /api/agences`)
- **Corps de la requête :**
  ```json
  {
    "nom": "Agence Tunis Centre",
    "ville": "Tunis",
    "adresse": "Avenue Habib Bourguiba",
    "telephone": "71000000"
  }
  ```
- **Réponse HTTP 201 CREATED :**
  ```json
  {
    "idAgence": 1,
    "nom": "Agence Tunis Centre",
    "ville": "Tunis",
    "adresse": "Avenue Habib Bourguiba",
    "telephone": "71000000"
  }
  ```

### 5.2 Échec de validation automatique (`POST /api/clients`)
- **Corps de la requête :**
  ```json
  {
    "nom": "",
    "prenom": "Sonia",
    "email": "invalide-email",
    "numPermis": "TN-123456"
  }
  ```
- **Réponse HTTP 400 BAD REQUEST :**
  ```json
  {
    "statut": 400,
    "message": "Erreur de validation des champs",
    "horodatage": "2026-11-19T00:35:12",
    "erreurs": {
      "nom": "Le nom du client ne doit pas être vide",
      "email": "Format d'e-mail invalide"
    }
  }
  ```

### 5.3 Recherche d'un contrat inexistant (`GET /api/contrats/9999`)
- **Réponse HTTP 404 NOT FOUND :**
  ```json
  {
    "statut": 404,
    "message": "Contrat introuvable avec l'identifiant 9999",
    "horodatage": "2026-11-19T00:35:15"
  }
  ```

### 5.4 Tentative de création sur service en lecture seule (`POST /api/paiements`)
- **Réponse HTTP 405 METHOD NOT ALLOWED :**
  ```json
  {
    "statut": 405,
    "message": "Méthode HTTP non supportée : POST",
    "horodatage": "2026-11-19T00:35:20"
  }
  ```
