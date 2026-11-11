# Atelier 4 - Inversion de contrôle, injection de dépendances et couche Service

## A. Modes d'injection comparés

Comparaison entre l'injection par constructeur et l'injection par attribut (testée à l'étape 2) :

| Critère | Injection par constructeur | Injection par attribut (`@Autowired`) |
|---|---|---|
| Champ `final` possible ? | **Oui** (le champ `final` est obligatoirement initialisé dans le constructeur). | **Non** (le conteneur injecte la valeur par réflexion après l'instanciation de l'objet). |
| Dépendance visible dans la signature de la classe ? | **Oui** (les dépendances apparaissent explicitement dans les paramètres du constructeur). | **Non** (la dépendance est masquée en tant que champ privé au sein de la classe). |
| Classe utilisable avec `new` hors conteneur sans dépendance `null` ? | **Oui** (le compilateur impose de fournir toutes les dépendances requises lors de l'appel au constructeur). | **Non** (l'appel à `new` n'injecte rien, laissant les champs annotés à `null`, avec risque élevé de `NullPointerException`). |
| Signalé par SonarQube for IDE ? | **Non** (respecte les règles de Clean Code et les bonnes pratiques de conception logicielle). | **Oui** (déclenche la règle `java:S6813` : *"Field dependency injection should be avoided"*). |

---

## B. Services

Tableau récapitulatif des neuf services de l'application AutoLoc :

| Service | Dépendances injectées | Mode | Justification |
|---|---|---|---|
| `ContratServiceImpl` | `IContratRepository` | Constructeur | Dépendance obligatoire, champ `final` immuable, garantit l'impossibilité d'instancier un service incomplet et facilite les tests unitaires. |
| `AgenceServiceImpl` | `IAgenceRepository` | Constructeur | Dépendance obligatoire pour les opérations CRUD sur les agences, champ `final`, découplage via l'interface du repository. |
| `EmployeServiceImpl` | `IEmployeRepository` | Constructeur | Dépendance obligatoire pour la persistance des employés, champ `final`, composant métier sans état (*stateless*). |
| `VehiculeServiceImpl` | `IVehiculeRepository` | Constructeur | Dépendance obligatoire, champ `final` immuable, conformité SonarQube et injection sûre générée par `@RequiredArgsConstructor`. |
| `EquipementServiceImpl` | `IEquipementRepository` | Constructeur | Dépendance obligatoire pour l'accès aux données des équipements, champ `final`, respect des conventions de conception AutoLoc. |
| `ClientServiceImpl` | `IClientRepository` | Constructeur | Dépendance obligatoire pour la gestion des clients, champ `final`, robustesse et testabilité unitaire hors conteneur. |
| `ReservationServiceImpl` | `IReservationRepository` | Constructeur | Dépendance obligatoire pour la gestion des réservations, champ `final`, respect du principe d'inversion des dépendances. |
| `PaiementServiceImpl` | `IPaiementRepository` | Constructeur | Dépendance obligatoire, champ `final`. Service en lecture seule (`findById`, `findAll`) car la création/suppression de paiements est gérée par le contrat. |
| `MaintenanceServiceImpl` | `IMaintenanceRepository` | Constructeur | Dépendance obligatoire pour le suivi des maintenances des véhicules, champ `final`, injection par constructeur garantissant l'intégrité de l'objet. |

---

## C. Messages d'erreur

Réponses aux trois scénarios d'erreurs du conteneur Spring IoC (étape 3) :

### Message 1
```text
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.ContratController
required a bean of type 'tn.esprit.autoloc.service.IContratService' that could not be found.
```
- **Causes les plus probables côté `ContratServiceImpl` :**
  1. **Annotation manquante :** Oubli de l'annotation `@Service` (ou `@Component`) sur la classe `ContratServiceImpl`. Spring ne détecte donc pas la classe lors du scan des composants et aucun bean implémentant `IContratService` n'est enregistré.
  2. **Package incorrect :** La classe `ContratServiceImpl` est située dans un package en dehors de l'arborescence scannée par `@SpringBootApplication` (hors du package racine `tn.esprit.autoloc` ou de ses sous-packages).
  *(Une autre cause possible est l'oubli de la clause `implements IContratService` sur la classe).*

### Message 2
```text
Parameter 0 of constructor in tn.esprit.autoloc.web.controller.NotificationController
required a single bean, but 2 were found: emailNotificateur, smsNotificateur
```
- **Deux corrections possibles :**
  1. **Solution 1 — `@Primary` :** Placer l'annotation `@Primary` sur la **classe** de l'un des deux beans candidats (ex. `@Component @Primary public class EmailNotificateur implements INotificateur`). Cela indique à Spring de choisir cette implémentation par défaut en cas d'ambiguïté.
  2. **Solution 2 — `@Qualifier` :** Placer l'annotation `@Qualifier("nomDuBean")` sur le **paramètre du constructeur** de la classe cible `NotificationController` (ex. `public NotificationController(@Qualifier("emailNotificateur") INotificateur notificateur)`). Cela sélectionne explicitement le bean voulu au point d'injection.

### Message 3
```text
The dependencies of some of the beans in the application context form a cycle:
clientServiceImpl -> reservationServiceImpl -> clientServiceImpl
```
- **Pourquoi l'injection par constructeur rend-elle ce cycle impossible à contourner ?**
  Avec l'injection par constructeur, pour instancier le bean `ClientServiceImpl`, Spring doit d'abord résoudre et lui fournir ses arguments de constructeur, ce qui requiert d'instancier `ReservationServiceImpl`. Mais pour instancier `ReservationServiceImpl`, Spring doit d'abord posséder une instance de `ClientServiceImpl`. Aucun objet ne peut être créé en premier, ce qui bloque immédiatement le conteneur avec une `BeanCurrentlyInCreationException`. Contrairement à l'injection par attribut ou setter où les instances peuvent être créées vides avec un constructeur sans paramètre puis câblées par la suite (masquant ainsi le couplage vicieux), l'injection par constructeur force à ce que l'objet soit toujours complètement initialisé dès sa construction.
- **Piste de conception pour supprimer le cycle :**
  Appliquer le principe de responsabilité unique (SRP). Extraire la responsabilité croisée ou partagée dans un nouveau service intermédiaire (par exemple `ClientReservationOrchestratorService`), ou revoir les responsabilités pour rendre la dépendance strictement unidirectionnelle (par exemple, seul le service de réservation fait appel au repository client), ou encore découpler les services via la publication d'événements Spring (`ApplicationEventPublisher`).

---

## D. Anomalies SonarQube for IDE

| Règle | Explication | Correction apportée |
|---|---|---|
| `java:S6813` (*Field dependency injection should be avoided*) | L'injection par attribut via `@Autowired` masque les dépendances de la classe, empêche de déclarer les champs `final` (immuables), couple fortement le code au framework et rend les tests unitaires risqués car l'instanciation manuelle (`new`) produit des objets invalides avec des dépendances `null`. | Remplacement par l'injection par constructeur avec des champs `private final` et l'annotation Lombok `@RequiredArgsConstructor`. |
| `java:S112` (*Generic exceptions should never be thrown*) | Lever des exceptions génériques comme `RuntimeException` ou `Exception` empêche les couches appelantes (notamment la couche web / REST) de distinguer les erreurs métier spécifiques et de produire les statuts HTTP appropriés (par exemple 404 NOT FOUND). | Utilisation de l'exception métier dédiée `ResourceNotFoundException` indiquant explicitement le nom de la ressource et l'identifiant introuvable. |
| `java:S1128` (*Unused imports should be removed*) | La présence d'imports superflus ou obsolètes (tels que `jakarta.persistence.EntityNotFoundException`, `org.springframework.transaction.annotation.Transactional` ou des classes utilitaires inutilisées) encombre le code source, augmente la dette technique et nuit à la lisibilité. | Suppression de tous les imports inutiles dans l'ensemble des classes du package `tn.esprit.autoloc.service` et `tn.esprit.autoloc.web.controller`. |

---

## E. Questions de compréhension

### 1. Où est le `new` de `ContratServiceImpl` et qui l'exécute ?
Il n'y a aucun appel `new ContratServiceImpl(...)` écrit dans le code source de notre application. C'est le conteneur Spring IoC (`ApplicationContext` / `BeanFactory`) qui instancie la classe par réflexion (introspection Java) au démarrage de l'application, après l'avoir découverte grâce à l'annotation `@Service` lors de la phase de scan des composants (*component scan*).

### 2. Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?
Pour respecter le principe d'inversion des dépendances (le « D » de SOLID) et garantir un couplage faible entre les couches. En dépendant d'une interface abstraite et non d'une implémentation concrète, le contrôleur ne subit pas les changements d'implémentation interne et peut être testé unitairement en toute simplicité en lui injectant un bouchon (*mock*) de l'interface.

### 3. Pourquoi un service singleton doit-il rester sans état ? Donner un exemple de bug.
Dans Spring, les beans sont par défaut de scope singleton : une seule et unique instance est partagée par l'ensemble des threads de l'application (chaque requête HTTP entrante étant exécutée par un thread distinct). Si le service stockait un état mutable dans un attribut d'instance (par exemple `private Contrat contratEnCours;`), des requêtes simultanées modifieraient et écraseraient concurremment cet état (*race condition*). Par exemple, un thread traitant la commande de l'utilisateur A positionne `contratEnCours`, mais avant d'effectuer la validation, un thread traitant l'utilisateur B écrase `contratEnCours` avec son propre contrat : l'utilisateur A finira par valider et payer le contrat de l'utilisateur B, causant une grave corruption de données.

### 4. Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?
Si l'on transmettait directement l'objet reçu à la méthode `save()` du repository, Hibernate effectuerait un `merge()`. Tous les attributs non spécifiés dans l'objet reçu écraseraient les valeurs en base de données par des `null`. De surcroît, pour les relations gérées avec `orphanRemoval = true` (comme la collection des paiements d'un contrat), une collection non renseignée ou vide dans la requête entraînerait la suppression immédiate et en cascade de tous les paiements associés existant en base. Charger l'entité existante avant de recopier les champs modifiables autorisés protège l'intégrité de l'identifiant et des associations.

### 5. Quelle différence entre `@Component` et `@Bean` ? Entre `@Primary` et `@Qualifier` ? Pourquoi `IPaiementService` n'expose-t-il ni création, ni modification, ni suppression ?
- **`@Component` vs `@Bean` :** `@Component` est une annotation de niveau classe appliquée directement sur le code source de nos composants pour qu'ils soient détectés automatiquement par le scan de Spring. `@Bean` est une annotation de niveau méthode, utilisée au sein d'une classe `@Configuration`, pour déclarer et instancier explicitement un bean (indispensable pour intégrer des classes externes ou du JDK dont nous ne possédons pas le code source).
- **`@Primary` vs `@Qualifier` :** `@Primary` se place sur une classe de bean pour la désigner comme choix préférentiel par défaut lorsque plusieurs implémentations du même type existent. `@Qualifier("nomDuBean")` se place au point d'injection pour sélectionner précisément le bean souhaité en levant toute ambiguïté.
- **Pourquoi `IPaiementService` est en lecture seule :** Selon la conception du domaine métier (composition `Contrat` $\rightarrow$ `Paiement`), un paiement n'a pas de cycle de vie autonome ; il est intimement subordonné à son contrat d'origine. Les opérations de création, modification ou suppression d'un paiement doivent obligatoirement s'effectuer via le `Contrat` (qui propage les changements via `CascadeType.ALL` et `orphanRemoval = true`) afin de garantir la cohérence des montants facturés et des règles de gestion financière.
