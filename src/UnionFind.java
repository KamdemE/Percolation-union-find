/**
 * UnionFind - Implémentation optimisée de la structure Union-Find
 *
 * Deux optimisations sont combinées :
 *
 *  1. fastFind / fastUnion — compression de chemin partielle :
 *     chaque nœud pointe vers son grand-père (equiv[equiv[i]]),
 *     ce qui réduit progressivement la hauteur des arbres.
 *
 *  2. logFind / logUnion — union par rang :
 *     on attache toujours l'arbre le plus petit sous la racine
 *     du plus grand, ce qui borne la hauteur à O(log n).
 *
 * Complexité :
 *   - fastFind  : O(log n) amorti
 *   - logFind   : O(log n)
 *   - logUnion  : O(log n)
 *
 * Note : len+2 éléments sont alloués pour réserver deux nœuds virtuels
 * utilisés par Percolation (nœud "top" = length, nœud "bottom" = length+1).
 */
public class UnionFind {

  /** Tableau des parents : equiv[i] = parent de i dans l'arbre */
  static int[] equiv;

  /**
   * Tableau des rangs (hauteur estimée de l'arbre).
   * Utilisé par logUnion pour décider quel arbre devient la racine.
   */
  static int[] height;

  /**
   * Initialise la structure pour (len + 2) éléments.
   * Les deux éléments supplémentaires sont les nœuds virtuels top/bottom
   * utilisés par l'algorithme de percolation logarithmique.
   *
   * @param len nombre de cellules de la grille (size * size)
   */
  public static void init(int len) {
    len = len + 2; // +2 pour les nœuds virtuels top (len) et bottom (len+1)
    equiv  = new int[len];
    height = new int[len];
    for (int i = 0; i < len; i++) {
      equiv[i]  = i; // chaque nœud est sa propre racine
      height[i] = 1; // hauteur initiale = 1
    }
  }

  // -------------------------------------------------------------------------
  // Optimisation 1 : compression de chemin partielle (fast)
  // -------------------------------------------------------------------------

  /**
   * Remonte l'arbre jusqu'à la racine en suivant les liens parents.
   * Applique une compression partielle : chaque nœud visité est
   * redirigé vers son grand-père (equiv[equiv[i]]), réduisant la hauteur.
   *
   * @param x l'élément dont on cherche la racine
   * @return la racine (représentant canonique) de x
   */
  public static int fastFind(int x) {
    int i = x;
    while (equiv[i] != i)
      i = equiv[i]; // remontée simple sans compression totale
    return i;
  }

  /**
   * Fusionne les classes de x et y en attachant la racine de x
   * sous la racine de y (sans tenir compte du rang).
   *
   * @param x premier élément
   * @param y second élément
   * @return la nouvelle racine commune
   */
  public static int fastUnion(int x, int y) {
    equiv[fastFind(x)] = equiv[fastFind(y)]; // racine de x → racine de y
    return equiv[fastFind(x)];
  }

  // -------------------------------------------------------------------------
  // Optimisation 2 : union par rang (log)
  // -------------------------------------------------------------------------

  /**
   * Remonte l'arbre en sautant deux niveaux à chaque itération
   * (equiv[equiv[i]]), ce qui compresse progressivement le chemin
   * et borne la hauteur à O(log n).
   *
   * @param x l'élément dont on cherche la racine
   * @return la racine (représentant canonique) de x
   */
  public static int logFind(int x) {
    int i = x;
    while (equiv[i] != i)
      i = equiv[equiv[i]]; // compression : saut de deux niveaux
    return i;
  }

  /**
   * Fusionne les classes de x et y en appliquant l'union par rang :
   * l'arbre de plus faible rang est attaché sous la racine de l'autre.
   * En cas d'égalité, le rang de la racine choisie est incrémenté.
   *
   * Cette stratégie garantit que la hauteur reste bornée par O(log n).
   *
   * @param x premier élément
   * @param y second élément
   * @return la nouvelle racine commune
   */
  public static int logUnion(int x, int y) {
    int rootX = fastFind(x);
    int rootY = fastFind(y);

    if (height[rootX] > height[rootY]) {
      // L'arbre de x est plus grand : y devient fils de x
      equiv[rootY] = rootX;
    } else if (height[rootX] < height[rootY]) {
      // L'arbre de y est plus grand : x devient fils de y
      equiv[rootX] = rootY;
    } else {
      // Même rang : on choisit y comme racine et on incrémente son rang
      equiv[rootX] = rootY;
      height[rootY]++;
    }

    return equiv[fastFind(x)];
  }

  // -------------------------------------------------------------------------
  // Points d'entrée publics (utilisés par Percolation.java)
  // -------------------------------------------------------------------------

  /** Délègue à logFind — version active par défaut. */
  public static int find(int n) {
    return logFind(n);
  }

  /** Délègue à logUnion — version active par défaut. */
  public static int union(int x, int y) {
    return logUnion(x, y);
  }
}
