# FitnessFlow Android App

Application Android de fitness en **Java** avec architecture **MVVM**.

## Fonctionnalités

- Accueil avec catégories de fitness en **RecyclerView** grille 2 colonnes
- Navigation par **Intent** vers l’écran des exercices
- Filtrage des exercices selon la catégorie sélectionnée
- Navigation vers un écran de détail d’exercice
- Authentification locale (Sign Up / Login)
- Planning hebdomadaire avec statut YES/NO
- Historique des semaines archivées
- **ViewModel** + **LiveData** en Java
- Cartes Material Design avec images intégrées
- Persistance locale via **SQLite** (`SQLiteOpenHelper`)

## Structure

- `data/model` : modèles `Category` et `Exercise`
- `data/repository` : repository SQLite (`AuthRepository`, `WeeklyPlanRepository`, `MySQLiteHelper`) + données fitness
- `ui` : écrans fitness + auth + planning + historique
- `viewmodel` : fitness + auth + weekly/history

## Lancer le projet

1. Ouvrir le dossier dans **Android Studio**.
2. Attendre la sync Gradle.
3. Lancer un émulateur (Device Manager) ou brancher un téléphone Android (USB debug).
4. Cliquer sur **Run 'app'**.

## Où placer vos images

Placez vos fichiers `.png` ou `.jpg` ici :

- `app/src/main/res/drawable/`

Exemples de noms recommandés (minuscules + underscore) :

- `cat_chest.png`
- `cat_arms.png`
- `cat_back.png`
- `cat_legs.png`
- `cat_shoulders.png`
- `ex_pushups.png`
- `ex_biceps_curl.png`

Ensuite, remplacez les ressources dans `FitnessRepository.java` :

- Catégories : `new Category(..., R.drawable.cat_chest)`
- Exercices : `new Exercise(..., R.drawable.ex_pushups)`

## Ajouter d'autres champs (niveau, équipement, répétitions)

1. Ajouter des champs dans `Exercise.java` (ex: `difficulty`, `equipment`, `reps`).
2. Compléter le constructeur + getters.
3. Renseigner les nouvelles valeurs dans `FitnessRepository.java`.
4. Afficher ces infos dans `item_exercise.xml` et `activity_exercise_detail.xml`.

