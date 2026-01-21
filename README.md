# Hytale Villager

## Presentation
Plugin Hytale oriente "village" avec une economie simple, un systeme de banques et des comptes joueurs persistants. Il sert de base technique pour gerer la monnaie et des institutions bancaires en jeu.

## Fonctionnalites
- Gestion de la monnaie des joueurs (consultation et mise a jour).
- Creation de banques (type village/gouvernement).
- Ouverture de comptes bancaires par joueur.
- Stockage persistant en JSON (fichiers locaux).

## Commandes
### Monnaie
- `/money` : affiche votre solde.
- `/money <player>` : affiche le solde d'un autre joueur.
- `/money update <amount(+10/-10/500)> <player>` : ajoute/retire ou fixe un solde.

### Banque
- `/bank info <name>` : affiche les informations d'une banque.
- `/bank create <name> <type>` : cree une banque (`VILLAGE` ou `GOVERNMENT`).
- `/bank account list <bank_name>` : liste vos comptes dans une banque.
- `/bank account open <bank_name> <accountName>` : ouvre un compte.
- `/bank account details <bank_name> <accountName>` : commande presente mais logique a completer.

## Prerequis
- Java 25 (toolchain configuree).
- Gradle (via `./gradlew`).
- Assets Hytale disponibles pour la compilation (voir `build.gradle.kts`).

## Commandes techniques
```bash
./gradlew build
./gradlew runServer
./gradlew syncAssets
```

Notes:
- `runServer` peut s'appeler `server` selon la version du plugin Hytale.
- `syncAssets` copie les assets generes par le serveur vers `src/main/resources`.

## Structure du code
```
src/main/java/hytale/doryanbessiere/villager
├── HytaleVillager.java           # Point d'entree du plugin
├── commands/                     # Commandes /money, /bank, /bank account
├── dto/                          # Objets de donnees (joueurs, banques, comptes)
├── exceptions/                   # Exceptions metier
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
- `src/main/resources/manifest.json` : template du manifest (remplis via `gradle.properties`).
- `src/main/resources/Server/Item/Items/MoneyTransaction.json` : item de transaction.
- `src/main/resources/Server.Languages.en-US/villager.lang` : langue.
