/**
 * Percolation - Simulation du seuil de percolation par méthode de Monte Carlo
 *
 * Une grille N×N de cellules est initialisée avec toutes les cellules bloquées.
 * Des cellules sont ouvertes aléatoirement une à une jusqu'à ce que le système
 * "percolate" (il existe un chemin ouvert du haut vers le bas).
 *
 * Trois méthodes de détection sont implémentées :
 *   1. isNaivePercolation  — parcours récursif DFS depuis une cellule
 *   2. isFastPercolation   — vérification via Union-Find sans nœuds virtuels
 *   3. isLogPercolation    — vérification via deux nœuds virtuels top/bottom (O(α(n)))
 *
 * La simulation de Monte Carlo répète l'expérience n fois et retourne
 * la moyenne des seuils observés, qui converge vers ~0.593 pour une grille infinie.
 *
 * Usage : java Percolation <nombre_de_simulations>
 */
public class Percolation {

  /** Taille d'un côté de la grille (N) */
  static final int size = 10;

  /** Nombre total de cellules (N²) */
  static final int length = size * size;

  /**
   * Grille de percolation : grid[i] = true si la cellule i est ouverte.
   * Les cellules sont indexées ligne par ligne : cellule (row, col) → row*size + col.
   */
  static boolean[] grid = new boolean[length];

  // -------------------------------------------------------------------------
  // Initialisation
  // -------------------------------------------------------------------------

  /**
   * Réinitialise la grille (toutes les cellules bloquées)
   * et réinitialise la structure Union-Find.
   * Alloue length+2 slots pour les deux nœuds virtuels top (length) et bottom (length+1).
   */
  public static void init() {
    for (int i = 0; i < length; i++)
      grid[i] = false;
    UnionFind.init(length); // length+2 éléments alloués dans UnionFind.init
  }

  // -------------------------------------------------------------------------
  // Affichage
  // -------------------------------------------------------------------------

  /**
   * Affiche la grille dans la console.
   * '*' = cellule ouverte, '-' = cellule bloquée.
   */
  public static void print() {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        System.out.print(grid[i * size + j] ? "*" : "-");
      }
      System.out.println();
    }
  }

  // -------------------------------------------------------------------------
  // Méthode 1 : Détection naïve par parcours récursif (DFS)
  // -------------------------------------------------------------------------

  /**
   * Parcours récursif en profondeur (DFS) depuis la cellule n.
   * Explore les cellules ouvertes voisines non encore visitées.
   *
   * La direction 'up' guide la recherche :
   *   - up=true  : on cherche à atteindre la première ligne (percolation vers le haut)
   *   - up=false : on cherche à atteindre la dernière ligne (percolation vers le bas)
   *
   * @param seen tableau des cellules déjà visitées (évite les boucles infinies)
   * @param n    indice de la cellule courante
   * @param up   direction de recherche
   * @return true si une cellule de la ligne cible est atteinte
   */
  public static boolean detectPath(boolean[] seen, int n, boolean up) {
    seen[n] = true;

    if (up) {
      // Condition d'arrêt : on est sur la première ligne
      if (n >= 0 && n <= size - 1)
        return grid[n];

      // Explorer le voisin du haut
      if (grid[n - size] && !seen[n - size])
        return detectPath(seen, n - size, true) || detectPath(seen, n - size, false);

      // Explorer le voisin gauche (si on n'est pas au bord gauche)
      if (n % size != 0 && grid[n - 1] && !seen[n - 1])
        return detectPath(seen, n - 1, true) || detectPath(seen, n - 1, false);

      // Explorer le voisin droit (si on n'est pas au bord droit)
      if (n % size != size - 1 && grid[n + 1] && !seen[n + 1])
        return detectPath(seen, n + 1, true) || detectPath(seen, n + 1, false);

    } else {
      // Condition d'arrêt : on est sur la dernière ligne
      if (n >= (size - 1) * size && n <= length - 1)
        return grid[n];

      // Explorer le voisin du bas
      if (grid[n + size] && !seen[n + size])
        return detectPath(seen, n + size, false) || detectPath(seen, n + size, true);

      // Explorer le voisin gauche
      if (n % size != 0 && grid[n - 1] && !seen[n - 1])
        return detectPath(seen, n - 1, false) || detectPath(seen, n - 1, true);

      // Explorer le voisin droit
      if (n % size != size - 1 && grid[n + 1] && !seen[n + 1])
        return detectPath(seen, n + 1, false) || detectPath(seen, n + 1, true);
    }

    return false;
  }

  /**
   * Vérifie si la cellule n appartient à un chemin percolant (naïf).
   * Lance deux DFS : un vers le haut et un vers le bas depuis n.
   *
   * @param n indice de la cellule à tester
   * @return true si n est sur un chemin qui relie le haut et le bas
   */
  public static boolean isNaivePercolation(int n) {
    boolean[] seen = new boolean[length];

    if (!grid[n]) return false; // cellule bloquée : impossible de percoler

    return detectPath(seen, n, true) && detectPath(seen, n, false);
  }

  // -------------------------------------------------------------------------
  // Méthode 2 : Détection rapide par Union-Find (sans nœuds virtuels)
  // -------------------------------------------------------------------------

  /**
   * Vérifie si la cellule n appartient à un chemin percolant via Union-Find.
   * On teste si n est dans la même composante qu'une cellule de la première
   * ligne ET d'une cellule de la dernière ligne.
   *
   * Complexité : O(size²) — parcourt les deux lignes.
   *
   * @param n indice de la cellule à tester
   * @return true si n est sur un chemin percolant
   */
  public static boolean isFastPercolation(int n) {
    // Cherche si n est connecté à une cellule de la première ligne
    for (int i = 0; i < size; i++) {
      if (UnionFind.find(i) == UnionFind.find(n)) {
        // Puis vérifie si cette composante atteint la dernière ligne
        for (int j = 0; j < size; j++) {
          if (UnionFind.find((size - 1) * size + j) == UnionFind.find(n))
            return true;
        }
      }
    }
    return false;
  }

  // -------------------------------------------------------------------------
  // Méthode 3 : Détection logarithmique via nœuds virtuels top/bottom
  // -------------------------------------------------------------------------

  /**
   * Vérifie la percolation globale via deux nœuds virtuels.
   * - Nœud virtuel "top"    : index length   (connecté à toute la première ligne)
   * - Nœud virtuel "bottom" : index length+1 (connecté à toute la dernière ligne)
   *
   * Si top et bottom sont dans la même composante Union-Find, le système percolate.
   * Complexité : O(α(n)) ≈ O(1).
   *
   * @return true si le système percolate
   */
  public static boolean isLogPercolation() {
    return UnionFind.find(length) == UnionFind.find(length + 1);
  }

  // -------------------------------------------------------------------------
  // Sélecteur de méthode
  // -------------------------------------------------------------------------

  /**
   * Point d'entrée pour tester la percolation d'une cellule n.
   * Modifie le corps de cette méthode pour changer d'algorithme.
   *
   * @param n indice de la cellule à tester
   * @return true si n appartient à un chemin percolant
   */
  public static boolean ispercolation(int n) {
    return isNaivePercolation(n);
    // Alternatives disponibles :
    // return isFastPercolation(n);
    // return isLogPercolation();  // ne prend pas de paramètre
  }

  // -------------------------------------------------------------------------
  // Propagation Union-Find lors de l'ouverture d'une cellule
  // -------------------------------------------------------------------------

  /**
   * Lorsqu'une cellule n est ouverte, la connecte à ses voisins ouverts via Union-Find.
   * Gère également la connexion aux nœuds virtuels top et bottom :
   *   - Première ligne → union avec le nœud virtuel "top" (index length)
   *   - Dernière ligne → union avec le nœud virtuel "bottom" (index length+1)
   *
   * @param n indice de la cellule nouvellement ouverte
   */
  public static void propagateUnion(int n) {
    // Voisin du haut
    if (n > size - 1 && grid[n - size])
      UnionFind.union(n, n - size);

    // Voisin du bas
    if (n < (size - 1) * size && grid[n + size])
      UnionFind.union(n, n + size);

    // Voisin gauche (pas au bord gauche)
    if (n % size != 0 && grid[n - 1])
      UnionFind.union(n, n - 1);

    // Voisin droit (pas au bord droit)
    if (n % size != size - 1 && grid[n + 1])
      UnionFind.union(n, n + 1);

    // Première ligne : connecter au nœud virtuel "top" (length)
    if (n >= 0 && n < size && grid[n]) {
      UnionFind.union(n, length);
      for (int i = 0; i < size; i++) {
        if (i != n) UnionFind.union(n, i); // connecte toutes les cellules de la 1ère ligne entre elles
      }
    }

    // Dernière ligne : connecter au nœud virtuel "bottom" (length+1)
    if (n >= (size - 1) * size && n < length && grid[n]) {
      UnionFind.union(n, length + 1);
      for (int i = 0; i < size; i++) {
        if (i != n) UnionFind.union(n, (size - 1) * size + i); // connecte toutes les cellules de la dernière ligne
      }
    }
  }

  // -------------------------------------------------------------------------
  // Ouverture aléatoire de cellules
  // -------------------------------------------------------------------------

  /** Tableau de suivi des cellules déjà ouvertes (évite les doublons). */
  static boolean[] seens = new boolean[length];

  /**
   * Ouvre une cellule aléatoire non encore ouverte.
   * Tire un indice au hasard ; si la cellule est déjà ouverte, recommence récursivement.
   *
   * Note : pour de grandes grilles, une approche itérative (Fisher-Yates shuffle)
   * serait plus efficace.
   *
   * @return l'indice de la cellule nouvellement ouverte
   */
  public static int randomShadow() {
    int select = (int) (length * Math.random());
    grid[select] = true;

    if (!seens[select]) {
      seens[select] = true;
      propagateUnion(select); // met à jour Union-Find
      return select;
    }
    return randomShadow(); // cellule déjà ouverte : on retente
  }

  // -------------------------------------------------------------------------
  // Simulation unique
  // -------------------------------------------------------------------------

  /**
   * Simule une expérience de percolation :
   * ouvre des cellules une à une jusqu'à ce que le système percolate.
   *
   * @return le ratio (cellules ouvertes / total) au moment de la percolation
   */
  public static double percolation() {
    double ouvertes = 0;
    boolean reached = false;

    // Réinitialise le tableau de suivi
    for (int k = 0; k < length; k++)
      seens[k] = false;

    while (!reached) {
      int j = randomShadow(); // ouvre une nouvelle cellule
      ouvertes++;

      // Teste toutes les cellules pour savoir si le système percolate
      for (int k = 0; k < length; k++) {
        if (ispercolation(k)) {
          reached = true;
          break;
        }
      }
    }

    return ouvertes / length; // seuil estimé pour cette expérience
  }

  // -------------------------------------------------------------------------
  // Monte Carlo
  // -------------------------------------------------------------------------

  /**
   * Estime le seuil de percolation par la méthode de Monte Carlo.
   * Répète l'expérience n fois et retourne la moyenne des seuils obtenus.
   *
   * La valeur théorique pour une grille infinie est ≈ 0.593.
   *
   * @param n nombre de simulations
   * @return estimation du seuil de percolation
   */
  public static double monteCarlo(int n) {
    double sum = 0.0;
    for (int i = 0; i < n; i++) {
      init();         // réinitialise grille et Union-Find
      sum += percolation(); // lance une simulation
    }
    return sum / n; // moyenne des seuils
  }

  // -------------------------------------------------------------------------
  // Point d'entrée principal
  // -------------------------------------------------------------------------

  /**
   * Lance la simulation Monte Carlo et affiche le seuil estimé et le temps d'exécution.
   *
   * @param args args[0] = nombre de simulations (entier positif)
   */
  public static void main(String[] args) {
    int n = Integer.parseInt(args[0]);
    long start = System.currentTimeMillis();
    System.out.println("Le seuil de Percolation vaut : " + monteCarlo(n));
    long end = System.currentTimeMillis();
    System.out.println("Temps d'exécution : " + (end - start) + " ms");
  }
}
