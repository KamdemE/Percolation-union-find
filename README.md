# Percolation & Union-Find Algorithm

## Description

Ce projet implémente une simulation de **percolation sur grille N×N** en Java, avec estimation du seuil de percolation par la méthode de **Monte Carlo**.

La percolation modélise des phénomènes physiques (écoulement d'un fluide à travers un milieu poreux, conductivité électrique, etc.). Le système *percolate* lorsqu'il existe un chemin de cellules ouvertes reliant la première ligne à la dernière.

La connectivité entre cellules est gérée par la structure de données **Union-Find**, implémentée en trois versions de complexité croissante.

---

## Structure du projet

```
Percolation-union-find/
├── src/
│   ├── Percolation.java      # Simulation de percolation + Monte Carlo
│   ├── UnionFind.java        # Union-Find optimisé (union par rang + compression de chemin)
│   └── UnionFind1.java       # Union-Find naïf (référence de comparaison)
├── .gitignore
├── README.md
└── LICENSE
```

---

## Algorithmes implémentés

### Union-Find — trois versions

| Fichier | Méthode | Find | Union |
|---|---|---|---|
| `UnionFind1.java` | Naïve | O(1) | O(n) |
| `UnionFind.java` | fastFind / fastUnion | O(log n) amorti | O(log n) |
| `UnionFind.java` | logFind / logUnion (union par rang) | O(log n) | O(log n) |

La version active par défaut est **logFind / logUnion** (union par rang avec compression de chemin partielle).

### Détection de percolation — trois méthodes

| Méthode | Principe | Complexité |
|---|---|---|
| `isNaivePercolation` | DFS récursif depuis chaque cellule | O(n²) |
| `isFastPercolation` | Union-Find, sans nœuds virtuels | O(n) |
| `isLogPercolation` | Union-Find + 2 nœuds virtuels top/bottom | O(α(n)) ≈ O(1) |

La méthode active se choisit dans `ispercolation()` dans `Percolation.java`.

---

## Fonctionnement

1. La grille N×N est initialisée avec toutes les cellules **bloquées**.
2. Des cellules sont **ouvertes aléatoirement** une à une.
3. À chaque ouverture, la cellule est **connectée à ses voisines ouvertes** via Union-Find.
4. Le système percolate dès qu'un chemin relie le nœud virtuel **top** au nœud virtuel **bottom**.
5. Le ratio `(cellules ouvertes / total)` au moment de la percolation est le **seuil observé**.
6. La méthode **Monte Carlo** répète cette expérience n fois et retourne la moyenne.

> La valeur théorique du seuil pour une grille infinie est **≈ 0.593**.

---

## Lancer le projet

### Prérequis

- Java JDK 8 ou supérieur
- Eclipse IDE (ou tout autre IDE Java)

### Compilation et exécution

```bash
# Cloner le dépôt
git clone https://github.com/KamdemE/Percolation-union-find.git
cd Percolation-union-find

# Compiler
javac src/*.java -d out/

# Lancer avec 100 simulations Monte Carlo
java -cp out Percolation 100
```

**Exemple de sortie :**
```
Le seuil de Percolation vaut : 0.5927
Temps d'exécution : 342 ms
```

### Avec Eclipse

1. `File > Import > Existing Projects into Workspace`
2. Sélectionner le dossier du projet
3. Clic droit sur `Percolation.java` → `Run As > Run Configurations`
4. Dans l'onglet `Arguments`, saisir le nombre de simulations (ex: `100`)

---

## Auteur

**KAMDEM KOUAM Ezechiel**  
GitHub : https://github.com/KamdemE

---

## Licence

Ce projet est distribué sous licence MIT — voir le fichier [LICENSE](LICENSE) pour plus de détails.
