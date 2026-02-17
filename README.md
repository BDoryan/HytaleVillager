# Hytale Villager

## Présentation

Plugin Hytale orienté économie/village avec monnaie joueur, banques, comptes bancaires et chèques bancaires. Le plugin stocke tout en JSON local et crée des PNJ de banque au moment de la création.

## Fonctionnalités

* Gestion du solde joueur (`/money`, mise à jour admin).
* Création de banques (`VILLAGE` ou `GOVERNMENT`) avec PNJ associé.
* Comptes bancaires par joueur (ouverture, fermeture, détails).
* Transactions de dépôt/retrait et historique par compte.
* Chèques bancaires (création et dépôt via item).
* Stockage persistant en JSON (fichiers locaux).
* Commandes de debug PNJ.

## Commandes

### Monnaie

* `/money` : affiche votre solde.
* `/money <player>` : affiche le solde d’un autre joueur.
* `/money update <amount(+10/-10/500)> <player>` : ajoute/retire ou fixe un solde.

### Banque

* `/bank info <name>` : affiche les informations d’une banque.
* `/bank create <name> <type>` : crée une banque (`VILLAGE` ou `GOVERNMENT`).
* `/bank delete <name>` : supprime une banque et son PNJ.

### Comptes bancaires

* `/bank account list <bankName>` : liste vos comptes dans une banque.
* `/bank account open <bankName> <accountName>` : ouvre un compte.
* `/bank account close <bankName> <accountName>` : ferme un compte (solde à 0 requis).
* `/bank account details <bankName> <accountName>` : détails + transactions.

### Transactions

* `/bank account transaction deposit <bankName> <accountName> <amount>` : dépôt depuis le portefeuille.
* `/bank account transaction withdraw <bankName> <accountName> <amount>` : retrait vers le portefeuille.

### Chèques bancaires

* `/bank account bankcheck create <bankName> <accountName> <amount>` : crée un chèque bancaire (item).
* `/bank account bankcheck deposit <bankName> <accountName>` : dépose le chèque en main.

### Debug

* `/debug npc spawn` : fait apparaître un PNJ de debug.
* `/debug npc get <npcId>` : retrouve un PNJ par UUID.

## Prérequis

* Java 25 (toolchain configurée).
* Gradle (via `./gradlew`).
* Assets Hytale disponibles pour la compilation.

  * Windows : `%APPDATA%/Hytale/install/release/package/game/latest/Assets.zip`
  * Linux (Flatpak) : `~/.var/app/com.hypixel.HytaleLauncher/data/Hytale/install/release/package/game/latest/Assets.zip`
  * Surcharge via `gradle-local.properties` ou `gradle.properties` : `hytaleAssetsPath=<path>`

## Commandes techniques

```bash
./gradlew build
./gradlew runServer
./gradlew syncAssets
```

Notes :

* `runServer` peut s’appeler `server` selon la version du plugin Hytale.
* `syncAssets` copie les assets générés par le serveur vers `src/main/resources` (manifest exclu).
* Pour forcer un dossier serveur local : `hytaleServerPath=<path>` dans `gradle-local.properties` ou `gradle.properties`.

## Déploiement (optionnel)

```bash
./gradlew deployJar
./gradlew deployJarAndRestart
```

Configuration requise dans `gradle.properties` ou `gradle-local.properties` :

* `deployHost`, `deployUser`, `deployPort`, `deployPath`, `deployRestartCmd`

## Structure du code

```
src/main/java/hytale/doryanbessiere/fr/villager
├── HytaleVillager.java           # Point d’entrée du plugin
├── commands/                     # Commandes /money, /bank, /debug
├── components/                   # Composants (BankLinkComponent)
├── dto/                          # Objets de données (joueurs, banques, comptes)
├── exceptions/                   # Exceptions métier
├── items/                        # Items (BankCheck)
├── listeners/                    # Événements joueurs (connexion/déconnexion)
├── repository/                   # Interfaces + adaptateur fichiers
├── services/                     # Logique métier (monnaie, banques)
└── utils/                        # Stockage JSON, événements, helpers
```

## Stockage des données

Les données sont stockées localement en JSON :

* `hytale-villager/players/<uuid>.json`
* `hytale-villager/banks/<bankId>/metadata.json`
* `hytale-villager/banks/<bankId>/accounts/<accountId>.json`

## Ressources

* `src/main/resources/manifest.json` : template du manifest (rempli via `gradle.properties`).
* `src/main/resources/Server/Item/Items/BankCheck.json` : item de chèque bancaire.
* `src/main/resources/Server/NPC/Roles/Custom/LookAtMe.json` : rôle PNJ custom.
* `src/main/resources/Server/NPC/Roles/Intelligent/Passive/Klops_Merchant.json` : rôle PNJ marchand.
* `src/main/resources/Server.Languages.en-US/villager.lang` : langue.
