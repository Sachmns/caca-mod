# CACA Mod — Minecraft 1.21.10 (Fabric)

Mod humoristique complet pour Minecraft **1.21.10** sur **Fabric Loader**, qui ajoute le "CACA System" : une touche personnalisable pour faire caca, un item CACA, un item CACA DORÉ rare (0,5%), un bloc, des sons, des particules et un système alimentaire absurde.

## 1. Contenu du mod

- **Touche "Faire caca"** (par défaut `K`), reconfigurable dans *Options > Contrôles > Raccourcis clavier > CACA Mod*. Cooldown de 30 secondes entre deux utilisations.
- **Bloc `caca_block`** : posé au sol près du joueur, cassable, donne l'item correspondant.
- **Item `CACA`** : comestible, tooltip *"C'est vraiment du CACA..."*, donne Nausée + Faim en le mangeant.
- **Item `CACA DORÉ`** : 0,5% de chance (1/200) à chaque utilisation de la touche, tooltip *"Un CACA extrêmement rare et précieux..."*, donne Nausée (courte) + Vitesse II en le mangeant.
- **Sons** : bruit de pet (entendu dans un rayon **exact de 10 blocs**, et nulle part au-delà), plop à l'apparition, son de cassage, son de dégustation, jingle spécial pour le CACA DORÉ.
- **Particules** à l'apparition du CACA.
- **Messages drôles** dans l'action bar quand on mange du CACA / CACA DORÉ, et message de chat spécial à l'obtention d'un CACA DORÉ.

## 2. Structure du projet

```
caca-mod/
├── build.gradle
├── gradle.properties
├── settings.gradle
├── src/
│   ├── main/                          (code commun : serveur + client)
│   │   ├── java/com/mod/caca/
│   │   │   ├── CacaMod.java            (point d'entrée principal)
│   │   │   ├── block/ModBlocks.java
│   │   │   ├── item/ModItems.java
│   │   │   ├── network/FaireCacaPayload.java
│   │   │   ├── sound/ModSounds.java
│   │   │   └── event/
│   │   │       ├── CacaEvent.java              (logique serveur : pose du bloc, tirage, son)
│   │   │       ├── CacaBlockBreakHandler.java
│   │   │       └── CacaBlockDropRegistry.java
│   │   └── resources/
│   │       ├── fabric.mod.json
│   │       ├── pack.mcmeta
│   │       └── assets/cacamod/ (textures, sons, lang, modèles, blockstates)
│   └── client/                        (code client uniquement)
│       └── java/com/mod/caca/
│           ├── client/CacaModClient.java        (point d'entrée client)
│           ├── client/CacaTooltipHandler.java
│           └── keybind/ModKeybinds.java
```

Cette séparation `main` / `client` utilise `splitEnvironmentSourceSets()` de Fabric Loom : elle garantit à la compilation qu'aucun code client (touches, rendu, tooltips) ne peut accidentellement tourner sur un serveur dédié — une cause fréquente de crash serveur dans les mods Fabric.

## 3. Pré-requis

- **Java 21** (JDK), vérifiable avec `java -version`.
- **Git** (optionnel, pour cloner si vous versionnez le projet).
- Une connexion internet la première fois (Gradle doit télécharger Minecraft, les mappings Yarn, Fabric Loader et Fabric API).
- **IntelliJ IDEA** (recommandé) ou tout IDE supportant Gradle, si vous voulez éditer/déboguer le code.

Aucune installation manuelle de Minecraft Forge/Fabric n'est nécessaire pour *développer* : Gradle + Fabric Loom téléchargent et préparent tout automatiquement dans un environnement de développement isolé.

## 4. Installer les dépendances et compiler le mod

Depuis la racine du projet (`caca-mod/`) :

```bash
# Sous Linux / macOS
./gradlew build

# Sous Windows
gradlew.bat build
```

> Le wrapper Gradle (`gradlew` / `gradlew.bat`) n'est pas inclus dans cette archive pour des raisons de taille. Si vous ne l'avez pas, générez-le avec une installation locale de Gradle 8.x :
> ```bash
> gradle wrapper --gradle-version 8.14
> ```
> Ensuite relancez `./gradlew build`.

La première compilation peut prendre plusieurs minutes : Gradle télécharge Minecraft 1.21.10, les mappings Yarn, Fabric Loader et Fabric API.

Le jar compilé apparaît dans :
```
build/libs/cacamod-1.0.0.jar
```

## 5. Lancer Minecraft avec Fabric (test en environnement de développement)

Pour lancer directement le jeu avec le mod chargé, sans passer par un launcher externe :

```bash
./gradlew runClient
```

Cela lance une instance de Minecraft 1.21.10 avec Fabric Loader et votre mod déjà actifs. Un compte de développement (offline) est utilisé par défaut.

Pour tester en multijoueur / serveur dédié :

```bash
./gradlew runServer
```

(à exécuter une première fois pour générer les fichiers `eula.txt` etc. dans `run/`, accepter l'EULA, puis relancer.)

## 6. Installer le mod dans une installation Minecraft "normale" (launcher officiel)

Si vous voulez utiliser le mod compilé dans votre propre installation Minecraft (pas l'environnement de dev) :

1. Installez **Fabric Loader** pour Minecraft 1.21.10 via l'[installeur officiel Fabric](https://fabricmc.net/use/installer/).
2. Téléchargez la version de **Fabric API** correspondant à 1.21.10 (depuis Modrinth ou CurseForge) et placez le `.jar` dans le dossier `mods/` de votre installation Minecraft (`%appdata%/.minecraft/mods` sous Windows, `~/.minecraft/mods` sous Linux/macOS, `~/Library/Application Support/minecraft/mods` sous macOS).
3. Copiez `build/libs/cacamod-1.0.0.jar` (généré à l'étape 4) dans ce même dossier `mods/`.
4. Lancez Minecraft via le launcher officiel, en sélectionnant le profil **Fabric Loader 1.21.10**.

## 7. Tester le mod

Une fois en jeu :

1. Ouvrez **Options > Contrôles > Raccourcis clavier**, descendez jusqu'à la catégorie **CACA Mod**, vérifiez/changez la touche "Faire caca" (par défaut `K`).
2. En jeu, appuyez sur la touche : un bloc de CACA doit apparaître au sol, avec particules et bruit de pet.
3. Demandez à un autre joueur de se placer à moins de 10 blocs : il doit entendre le pet. Au-delà de 10 blocs, il ne doit rien entendre.
4. Cassez le bloc : il donne un item **CACA** (ou, avec 0,5% de chance, un **CACA DORÉ** — vous pouvez forcer le test en spammant la touche, statistiquement 1 essai sur 200 sera doré).
5. Faites clic droit avec l'item en main pour le manger : nausée + faim (ou nausée + vitesse pour le doré), message drôle dans l'action bar.
6. Vérifiez le tooltip humoristique en survolant l'item dans l'inventaire.

## 8. Personnaliser les assets

Les textures et sons fournis sont des **placeholders fonctionnels** générés automatiquement (textures 16×16 façon pixel art, sons synthétiques simples). Pour les remplacer par vos propres créations :

- **Textures** : remplacez les fichiers PNG 16×16 dans `src/main/resources/assets/cacamod/textures/item/` et `textures/block/`.
- **Sons** : remplacez les fichiers `.ogg` dans `src/main/resources/assets/cacamod/sounds/` (gardez les mêmes noms de fichiers, ou mettez à jour `sounds.json` en conséquence).
- **Textes** : éditez `src/main/resources/assets/cacamod/lang/fr_fr.json` et `en_us.json`.

Après toute modification de ressources, relancez `./gradlew build` ou `./gradlew runClient`.

## 9. Notes techniques

- **Mappings** : Yarn (dernière version officiellement supportée pour 1.21.10 ; à partir de 1.21.11 Fabric recommande de migrer vers les mappings Mojang).
- **API alimentaire** : utilise `FoodComponent` + `ConsumableComponent` séparés (API introduite en 1.21.5), avec `ApplyEffectsConsumeEffect` pour les effets de statut à la consommation.
- **Réseau** : le tirage du CACA DORÉ et la pose du bloc sont entièrement gérés côté serveur (autoritaire), via un paquet réseau client→serveur (`FaireCacaPayload`), pour garantir que le son de pet est correctement diffusé à tous les joueurs dans le rayon de 10 blocs et pas seulement à celui qui appuie sur la touche.
