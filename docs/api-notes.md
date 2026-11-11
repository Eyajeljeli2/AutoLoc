# Atelier 5 - Spring MVC et SpringDoc : API REST, Postman et Swagger

## A. Endpoints

Tableau récapitulatif de l'ensemble des endpoints de l'API REST AutoLoc :

| Contrôleur | Verbe et route | Service appelé | Code de succès | Codes d'erreur |
|---|---|---|---|---|
| `ContratController` | GET `/api/contrats` | `contratService.findAll()` | 200 OK | — |
| `ContratController` | GET `/api/contrats/{id}` | `contratService.findById(id)` | 200 OK | 404 NOT FOUND |
| `ContratController` | POST `/api/contrats` | `contratService.create(contrat)` | 201 CREATED | 400 BAD REQUEST |
| `ContratController` | PUT `/api/contrats/{id}` | `contratService.update(id, contrat)` | 200 OK | 400 BAD REQUEST, 404 NOT FOUND |
| `ContratController` | DELETE `/api/contrats/{id}` | `contratService.deleteById(id)` | 204 NO CONTENT | 404 NOT FOUND |
| `PaiementController` | GET `/api/paiements` | `paiementService.findAll()` | 200 OK | — |
| `PaiementController` | GET `/api/paiements/{id}` | `paiementService.findById(id)` | 200 OK | 404 NOT FOUND |
| `AgenceController` | GET `/api/agences` | `agenceService.findAll()` | 200 OK | — |
| `AgenceController` | GET `/api/agences/{id}` | `agenceService.findById(id)` | 200 OK | 404 NOT FOUND |
| `AgenceController` | POST `/api/agences` | `agenceService.create(agence)` | 201 CREATED | 400 BAD REQUEST |
| `AgenceController` | PUT `/api/agences/{id}` | `agenceService.update(id, agence)` | 200 OK | 400 BAD REQUEST, 404 NOT FOUND |
| `AgenceController` | DELETE `/api/agences/{id}` | `agenceService.deleteById(id)` | 204 NO CONTENT | 404 NOT FOUND |
| `EmployeController` | GET `/api/employes` | `employeService.findAll()` | 200 OK | — |
| `EmployeController` | GET `/api/employes/{id}` | `employeService.findById(id)` | 200 OK | 404 NOT FOUND |
| `EmployeController` | POST `/api/employes` | `employeService.create(employe)` | 201 CREATED | 400 BAD REQUEST |
| `EmployeController` | PUT `/api/employes/{id}` | `employeService.update(id, employe)` | 200 OK | 400 BAD REQUEST, 404 NOT FOUND |
| `EmployeController` | DELETE `/api/employes/{id}` | `employeService.deleteById(id)` | 204 NO CONTENT | 404 NOT FOUND |
| `VehiculeController` | GET `/api/vehicules` | `vehiculeService.findAll()` | 200 OK | — |
| `VehiculeController` | GET `/api/vehicules/{id}` | `vehiculeService.findById(id)` | 200 OK | 404 NOT FOUND |
| `VehiculeController` | POST `/api/vehicules` | `vehiculeService.create(vehicule)` | 201 CREATED | 400 BAD REQUEST |
| `VehiculeController` | PUT `/api/vehicules/{id}` | `vehiculeService.update(id, vehicule)` | 200 OK | 400 BAD REQUEST, 404 NOT FOUND |
| `VehiculeController` | DELETE `/api/vehicules/{id}` | `vehiculeService.deleteById(id)` | 204 NO CONTENT | 404 NOT FOUND |
| `EquipementController` | GET `/api/equipements` | `equipementService.findAll()` | 200 OK | — |
| `EquipementController` | GET `/api/equipements/{id}` | `equipementService.findById(id)` | 200 OK | 404 NOT FOUND |
| `EquipementController` | POST `/api/equipements` | `equipementService.create(equipement)` | 201 CREATED | 400 BAD REQUEST |
| `EquipementController` | PUT `/api/equipements/{id}` | `equipementService.update(id, equipement)` | 200 OK | 400 BAD REQUEST, 404 NOT FOUND |
| `EquipementController` | DELETE `/api/equipements/{id}` | `equipementService.deleteById(id)` | 204 NO CONTENT | 404 NOT FOUND |
| `ClientController` | GET `/api/clients` | `clientService.findAll()` | 200 OK | — |
| `ClientController` | GET `/api/clients/{id}` | `clientService.findById(id)` | 200 OK | 404 NOT FOUND |
| `ClientController` | POST `/api/clients` | `clientService.create(client)` | 201 CREATED | 400 BAD REQUEST |
| `ClientController` | PUT `/api/clients/{id}` | `clientService.update(id, client)` | 200 OK | 400 BAD REQUEST, 404 NOT FOUND |
| `ClientController` | DELETE `/api/clients/{id}` | `clientService.deleteById(id)` | 204 NO CONTENT | 404 NOT FOUND |
| `ReservationController` | GET `/api/reservations` | `reservationService.findAll()` | 200 OK | — |
| `ReservationController` | GET `/api/reservations/{id}` | `reservationService.findById(id)` | 200 OK | 404 NOT FOUND |
| `ReservationController` | POST `/api/reservations` | `reservationService.create(reservation)` | 201 CREATED | 400 BAD REQUEST |
| `ReservationController` | PUT `/api/reservations/{id}` | `reservationService.update(id, reservation)` | 200 OK | 400 BAD REQUEST, 404 NOT FOUND |
| `ReservationController` | DELETE `/api/reservations/{id}` | `reservationService.deleteById(id)` | 204 NO CONTENT | 404 NOT FOUND |
| `MaintenanceController` | GET `/api/maintenances` | `maintenanceService.findAll()` | 200 OK | — |
| `MaintenanceController` | GET `/api/maintenances/{id}` | `maintenanceService.findById(id)` | 200 OK | 404 NOT FOUND |
| `MaintenanceController` | POST `/api/maintenances` | `maintenanceService.create(maintenance)` | 201 CREATED | 400 BAD REQUEST |
| `MaintenanceController` | PUT `/api/maintenances/{id}` | `maintenanceService.update(id, maintenance)` | 200 OK | 400 BAD REQUEST, 404 NOT FOUND |
| `MaintenanceController` | DELETE `/api/maintenances/{id}` | `maintenanceService.deleteById(id)` | 204 NO CONTENT | 404 NOT FOUND |

---

## B. Annotations Jackson

### 1. Synthèse des associations et annotations appliquées

| Association | Annotation retenue | Justification |
|---|---|---|
| `Contrat ↔ Paiement` | `@JsonManagedReference("contrat-paiements")` sur `Contrat.paiements`<br>`@JsonBackReference("contrat-paiements")` sur `Paiement.contrat` | Vraie composition (`cascade = ALL`, `orphanRemoval = true`). Permet d'inclure les paiements dans la réponse JSON du contrat et de les créer simultanément sans déclencher de boucle infinie (`StackOverflowError`). |
| `Agence ↔ Vehicule` | `@JsonIgnore` sur `Agence.vehicules` | L'enfant (`Vehicule`) désigne son agence par son identifiant. `@JsonIgnore` sur la collection côté agence brise la récursion infinie. |
| `Agence ↔ Employe` | `@JsonIgnore` sur `Agence.employes` | L'employé référence son agence. La liste des employés dans l'agence est ignorée pour éviter les boucles et les réponses trop volumineuses. |
| `Client ↔ Reservation` | `@JsonIgnore` sur `Client.reservations` | La réservation porte la référence vers le client. `@JsonIgnore` sur la liste côté client évite la boucle sans bloquer la création de réservation avec `client: { idClient: ... }`. |
| `Vehicule ↔ Maintenance` | `@JsonIgnore` sur `Vehicule.maintenances` | La maintenance référence son véhicule. La liste côté véhicule est ignorée pour la sérialisation JSON. |
| `Vehicule ↔ Reservation` | `@JsonIgnore` sur `Vehicule.reservations` | Évite la boucle de sérialisation entre le véhicule et ses réservations. |
| `Vehicule ↔ Equipement` | `@JsonIgnore` sur `Equipement.vehicules` | Dans cette relation `@ManyToMany`, un équipement ignore sa liste de véhicules associés lors de la sérialisation. |
| `Reservation ↔ Contrat` | `@JsonIgnore` sur `Reservation.contrat` | Relation 1-1 bidirectionnelle : le contrat pointe vers la réservation, la référence inverse sur la réservation est ignorée pour rompre le cycle. |

### 2. Différence entre `@JsonBackReference` et `@JsonIgnore`
- **`@JsonBackReference` :** Fonctionne de concert avec `@JsonManagedReference`. Le champ annoté est omis lors de la sérialisation (ce qui coupe la boucle infinie), mais à la **désérialisation**, Jackson reconstitue automatiquement le lien bidirectionnel en injectant l'objet parent dans la référence de l'enfant.
- **`@JsonIgnore` :** Ignore totalement la propriété à la sérialisation (et par défaut à la désérialisation). Si on avait placé `@JsonBackReference` sur `Reservation.client`, Jackson aurait ignoré la propriété client lors du `POST` et la réservation aurait été persistée sans client. `@JsonIgnore` est donc utilisé sur la collection inverse quand l'enfant est créé séparément en désignant son parent.

---

## C. Postman et Swagger

### 1. Apports de Postman
- **Rôle :** Outil client d'exécution et de test dynamique des requêtes HTTP. Il permet d'automatiser des suites de tests avec assertions JavaScript (`pm.test()`), de vérifier les statuts de retour (200, 201, 204, 400, 404, 405), et de chaîner les scénarios en sauvegardant des variables d'environnement (`pm.environment.set()`).
- **Exemple concret rencontré dans l'atelier :**
  - Chaînage CRUD sur le contrat : lors du `POST /api/contrats`, le script After-response mémorise `contratId` (`pm.environment.set("contratId", pm.response.json().idContrat)`).
  - Ce `contratId` est ensuite automatiquement réutilisé pour les requêtes `GET /api/contrats/{{contratId}}`, `PUT /api/contrats/{{contratId}}` et `DELETE /api/contrats/{{contratId}}`.
  - Test de la cascade de suppression : après le `DELETE /api/contrats/{{contratId}}`, un `GET /api/paiements/{idPaiement}` confirme le statut **404**, prouvant que la cascade `REMOVE` a bien supprimé les paiements en base.

### 2. Apports de Swagger UI (SpringDoc OpenAPI)
- **Rôle :** Documentation interactive et standardisée de l'API (spécification OpenAPI 3.0), générée automatiquement à partir du code source. Il permet de visualiser les endpoints, les modèles de données (schemas), les formats attendus (dates, enums), et offre une interface « Try it out » pour tester directement les routes sans outil externe.
- **Exemple concret rencontré dans l'atelier :**
  - Consultation de `/api/swagger-ui.html` et `/api/v3/api-docs` : toutes les opérations sont regroupées par tag (`Contrats`, `Paiements`, `Agences`...).
  - Chaque méthode documente ses réponses possibles (`200`, `201`, `204`, `400`, `404`) et ses paramètres d'URL (`@Parameter`). Les schémas JSON décrivent exactement la structure de chaque entité.

### 3. Complémentarité
Postman sert à **tester et valider** les règles métier et les scénarios en profondeur, tandis que Swagger sert à **spécifier, documenter et explorer** le contrat d'API synchronisé en temps réel avec le code source.

---

## D. Anomalies SonarQube

| Règle SonarQube | Explication | Correction apportée |
|---|---|---|
| `java:S6813` (*Field dependency injection should be avoided*) | L'injection par champ (`@Autowired private ...`) masque les dépendances de la classe, empêche de déclarer les attributs en `final` (immuabilité) et rend les tests unitaires dépendants du conteneur. | Utilisation exclusive de l'injection par constructeur avec des champs `private final` et l'annotation Lombok `@RequiredArgsConstructor` sur tous les contrôleurs REST. |
| `java:S1128` (*Unused imports should be removed*) | La présence d'imports superflus ou de classes non utilisées encombre les fichiers sources et augmente la dette technique. | Nettoyage complet de tous les imports non utilisés dans le package `tn.esprit.autoloc.web.controller`. |
| `java:S1186` / Séparation stricte des couches (*Methods should not be empty / Architecture layered*) | Des contrôleurs qui feraient directement appel à la couche repository violent le principe d'architecture multicouche et rendent impossible l'application centralisée des règles de gestion. | Les neuf contrôleurs REST sont des composants minces qui délèguent l'intégralité de la logique métier aux interfaces `I...Service`, sans aucun accès direct aux repositories. |

---

## E. Questions de compréhension

### 1. Quel composant reçoit toutes les requêtes, et comment retrouve-t-il la méthode du contrôleur à appeler ?
Toutes les requêtes HTTP sont interceptées en point d'entrée unique par la **`DispatcherServlet`** (le *Front Controller* de Spring MVC). Pour déterminer quelle méthode de contrôleur exécuter, la `DispatcherServlet` consulte le composant **`HandlerMapping`** (notamment `RequestMappingHandlerMapping`). Ce dernier maintient une table de correspondance construite au démarrage en analysant les annotations `@RequestMapping`, `@GetMapping`, `@PostMapping`, etc., et fait correspondre l'URL et la méthode HTTP de la requête avec la méthode de contrôleur appropriée.

### 2. Pourquoi `ContratController` dépend-il de `IContratService` et non de `ContratRepository` ni de `ContratServiceImpl` ?
- **Non de `ContratRepository` :** Le contrôleur appartient à la couche présentation (web) et ne doit jamais dialoguer directement avec l'accès aux données. La couche Service est indispensable pour porter la logique métier, valider les règles de gestion, vérifier l'existence des entités et lever les exceptions appropriées.
- **Non de `ContratServiceImpl` :** Pour respecter le principe d'inversion des dépendances (le « D » des principes SOLID) et préserver un couplage faible. En dépendant de l'interface `IContratService`, le contrôleur ignore les détails d'implémentation, ce qui permet de remplacer ou enrichir le service (cache, sécurité, mocks de test) sans impacter le contrôleur.

### 3. Pourquoi un POST renvoie-t-il 201 et un DELETE 204 ? Pourquoi une ressource absente donne-t-elle 404 et non 500 ?
- **POST $\rightarrow$ 201 Created :** Le verbe POST a pour finalité la création d'une nouvelle ressource. La norme HTTP spécifie le code 201 pour confirmer que la ressource a bien été créée et lui associer son nouvel identifiant.
- **DELETE $\rightarrow$ 204 No Content :** Le verbe DELETE supprime la ressource ciblée. Le code 204 indique que l'opération a réussi et que le serveur ne renvoie aucun corps dans la réponse.
- **Ressource absente $\rightarrow$ 404 et non 500 :** Le statut 404 (Not Found) fait partie de la famille des erreurs client (4xx) : il indique que l'URI ou l'identifiant demandé n'existe pas. Le statut 500 (Internal Server Error) est réservé aux défaillances imprévues ou bogues internes du serveur. Une absence d'élément est un cas métier normal et prévisible, qui doit donc être traduit en 404 par le `GlobalExceptionHandler`.

### 4. Pourquoi `PaiementController` n'expose-t-il que des GET ? Quelle réponse reçoit un POST `/api/paiements` ?
- **Lecture seule :** Dans le modèle métier, un paiement est en relation de composition stricte avec son contrat. Il ne peut pas exister seul. Les opérations de création, modification ou suppression d'un paiement doivent obligatoirement s'effectuer via le `Contrat` parent (qui propage les changements par cascade `CascadeType.ALL` et `orphanRemoval = true`) afin de maintenir la cohérence des montants totaux.
- **Réponse à un POST `/api/paiements` :** Le serveur renvoie un code HTTP **`405 Method Not Allowed`**, car aucune méthode du contrôleur n'est configurée pour traiter le verbe POST sur cette route.

### 5. Quels risques présente l'exposition directe des entités JPA ? Pourquoi `Contrat.paiements` utilise-t-il `@JsonManagedReference` alors que `Agence.vehicules` utilise `@JsonIgnore` ?
- **Risques de l'exposition directe des entités JPA :**
  1. *Couplage fort :* Toute modification du modèle relationnel ou du schéma de base de données casse immédiatement les contrats des clients de l'API.
  2. *Fuite de données sensibles :* Exposition involontaire de champs internes, techniques ou confidentiels.
  3. *Problèmes de sérialisation et performance :* Boucles infinies de sérialisation, requêtes N+1 et exceptions `LazyInitializationException` ou proxys `ByteBuddyInterceptor`.
  4. *Failles de sécurité (Mass Assignment) :* Un client peut injecter des champs non souhaités (ex. forcer un identifiant ou un statut privilégié). *(L'introduction des DTO au Chapitre 10 résoudra ces problématiques).*
- **Pourquoi `@JsonManagedReference` pour `Contrat.paiements` vs `@JsonIgnore` pour `Agence.vehicules` :**
  - `Contrat.paiements` est une composition où le contrat doit inclure l'affichage de ses paiements et permettre leur création imbriquée lors d'un unique POST. `@JsonManagedReference` et `@JsonBackReference` permettent cette inclusion tout en évitant la boucle infinie.
  - `Agence.vehicules` est une relation simple où les véhicules ont leur propre cycle de vie indépendant. Inclure tous les véhicules d'une agence alourdirait inutilement la réponse JSON et introduirait des cycles ; `@JsonIgnore` supprime cette collection superflue, et chaque véhicule référence son agence simplement par son ID.
