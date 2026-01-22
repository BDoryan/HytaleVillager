# Hytale Villager

## Presentation
Plugin Hytale oriente economie/village avec monnaie joueur, banques, comptes bancaires et checks bancaires. Le plugin stocke tout en JSON local et cree des PNJ de banque au moment de la creation.

## Fonctionnalites
- Gestion du solde joueur (/money, mise a jour admin).
- Creation de banques (VILLAGE ou GOVERNMENT) avec PNJ associe.
- Comptes bancaires par joueur (ouverture, fermeture, details).
- Transactions de depot/retrait et historique par compte.
- Checks bancaires (creation et depot via item).
- Stockage persistant en JSON (fichiers locaux).
- Commandes de debug PNJ.

## Commandes
### Monnaie
- `/money` : affiche votre solde.
- `/money <player>` : affiche le solde d'un autre joueur.
- `/money update <amount(+10/-10/500)> <player>` : ajoute/retire ou fixe un solde.

### Banque
- `/bank info <name>` : affiche les informations d'une banque.
- `/bank create <name> <type>` : cree une banque (`VILLAGE` ou `GOVERNMENT`).
- `/bank delete <name>` : supprime une banque et son PNJ.

### Comptes bancaires
- `/bank account list <bankName>` : liste vos comptes dans une banque.
- `/bank account open <bankName> <accountName>` : ouvre un compte.
- `/bank account close <bankName> <accountName>` : ferme un compte (solde 0 requis).
- `/bank account details <bankName> <accountName>` : details + transactions.

### Transactions
- `/bank account transaction deposit <bankName> <accountName> <amount>` : depot depuis le portefeuille.
- `/bank account transaction withdraw <bankName> <accountName> <amount>` : retrait vers le portefeuille.

### Checks bancaires
- `/bank account bankcheck create <bankName> <accountName> <amount>` : cree un check bancaire (item).
- `/bank account bankcheck deposit <bankName> <accountName>` : depose le check en main.

### Debug
- `/debug npc spawn` : spawn un PNJ de debug.
- `/debug npc get <npcId>` : retrouve un PNJ par UUID.

## Prerequis
- Java 25 (toolchain configuree).
- Gradle (via `./gradlew`).
- Assets Hytale disponibles pour la compilation.
  - Windows: `%APPDATA%/Hytale/install/release/package/game/latest/Assets.zip`
  - Linux (Flatpak): `~/.var/app/com.hypixel.HytaleLauncher/data/Hytale/install/release/package/game/latest/Assets.zip`

## Commandes techniques
```bash
./gradlew build
./gradlew runServer
./gradlew syncAssets
```

Notes:
- `runServer` peut s'appeler `server` selon la version du plugin Hytale.
- `syncAssets` copie les assets generes par le serveur vers `src/main/resources` (manifest exclu).

## Deploiement (optionnel)
```bash
./gradlew deployJar
./gradlew deployJarAndRestart
```

Configuration requise dans `gradle.properties` ou `gradle-local.properties`:
- `deployHost`, `deployUser`, `deployPort`, `deployPath`, `deployRestartCmd`

## Structure du code
```
src/main/java/hytale/doryanbessiere/fr/villager
├── HytaleVillager.java           # Point d'entree du plugin
├── commands/                     # Commandes /money, /bank, /debug
├── components/                   # Components (BankLinkComponent)
├── dto/                          # Objets de donnees (joueurs, banques, comptes)
├── exceptions/                   # Exceptions metier
├── items/                        # Items (BankCheck)
├── listeners/                    # Events joueurs (connexion/deconnexion)
├── repository/                   # Interfaces + adapter fichiers
├── services/                     # Logique metier (monnaie, banques)
└── utils/                        # Stockage JSON, events, helpers
```

## Stockage des donnees
Les donnees sont stockees localement en JSON:
- `hytale-villager/players/<uuid>.json`
- `hytale-villager/banks/<bankId>/metadata.json`
- `hytale-villager/banks/<bankId>/accounts/<accountId>.json`

## Ressources
- `src/main/resources/manifest.json` : template du manifest (rempli via `gradle.properties`).
- `src/main/resources/Server/Item/Items/BankCheck.json` : item de check bancaire.
- `src/main/resources/Server/NPC/Roles/Custom/LookAtMe.json` : role PNJ custom.
- `src/main/resources/Server/NPC/Roles/Intelligent/Passive/Klops_Merchant.json` : role PNJ marchand.
- `src/main/resources/Server.Languages.en-US/villager.lang` : langue.
