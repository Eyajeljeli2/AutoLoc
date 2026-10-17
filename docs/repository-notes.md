# Atelier 3 - Notes sur la couche Repository

## Choix d'interface

Toutes les interfaces etendent JpaRepository<Entite, Long> : CRUD complet, findAll renvoie une List (et non un Iterable), tri et pagination disponibles, saveAndFlush pour une ecriture immediate. Le prefixe I respecte la convention de nommage AutoLoc.

| Interface | Etend | Justification |
|---|---|---|
| IAgenceRepository | JpaRepository<Agence, Long> | CRUD complet, findAll renvoie une List, tri et pagination. |
| IEmployeRepository | JpaRepository<Employe, Long> | CRUD complet, findAll renvoie une List, tri et pagination. |
| IVehiculeRepository | JpaRepository<Vehicule, Long> | CRUD complet, findAll renvoie une List, tri et pagination. |
| IEquipementRepository | JpaRepository<Equipement, Long> | CRUD complet, findAll renvoie une List, tri et pagination. |
| IClientRepository | JpaRepository<Client, Long> | CRUD complet, findAll renvoie une List, tri et pagination. |
| IReservationRepository | JpaRepository<Reservation, Long> | CRUD complet, findAll renvoie une List, tri et pagination. |
| IContratRepository | JpaRepository<Contrat, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible. |
| IPaiementRepository | JpaRepository<Paiement, Long> | Lecture des paiements ; creation et suppression passent par le Contrat (composition). |
| IMaintenanceRepository | JpaRepository<Maintenance, Long> | CRUD complet, findAll renvoie une List, tri et pagination. |

## Anomalies SonarQube for IDE

| Anomalie | Regle / explication | Correction apportee |
|---|---|---|
| Appel d'une methode @Transactional via this (ClientServiceImpl, VehiculeServiceImpl) | Une methode transactionnelle appelee directement par this contourne le proxy Spring, la transaction n'est pas appliquee | update() appelle maintenant findById() du repository au lieu de this.getById() |
| Trop de break/continue dans une boucle (BeanCopyUtils) | Une boucle ne doit pas contenir plus d'un break ou continue, sinon le flux est difficile a lire | Les deux continue remplaces par une condition booleenne copiable |
| Methode vide (AutolocApiApplicationTests.contextLoads) | Une methode vide doit expliquer pourquoi elle est vide | Ajout d'un commentaire expliquant que le test verifie le demarrage du contexte |