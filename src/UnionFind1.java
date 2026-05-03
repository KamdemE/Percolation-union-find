/**
 * UnionFind1 - Implémentation naïve de la structure Union-Find
 *
 * Principe : chaque élément pointe directement vers le représentant
 * canonique de sa classe d'équivalence (tableau "à plat").
 *
 * Complexité :
 *   - naiveFind : O(1)  — lecture directe dans le tableau
 *   - naiveUnion : O(n) — il faut parcourir tout le tableau pour
 *                         mettre à jour tous les membres de la classe
 */
public class UnionFind1 {

  /** Tableau des représentants canoniques : equiv[i] = représentant de i */
  static int[] equiv;

  /**
   * Initialise la structure pour 'len' éléments.
   * Au départ, chaque élément est son propre représentant.
   *
   * @param len nombre d'éléments
   */
  public static void init(int len) {
    equiv = new int[len];
    for (int i = 0; i < len; i++)
      equiv[i] = i; // chaque élément est sa propre classe
  }

  /**
   * Trouve le représentant canonique de x.
   * Dans cette version naïve, il est stocké directement dans equiv[x].
   *
   * @param x l'élément dont on cherche le représentant
   * @return le représentant canonique de x
   */
  public static int naiveFind(int x) {
    return equiv[x];
  }

  /**
   * Fusionne les classes d'équivalence de x et y.
   * Tous les éléments ayant le même représentant que x sont redirigés
   * vers le représentant de y — d'où le coût O(n).
   *
   * @param x premier élément
   * @param y second élément
   * @return le nouveau représentant canonique commun
   */
  public static int naiveUnion(int x, int y) {
    int repX = naiveFind(x);
    int repY = naiveFind(y);

    // Parcours complet pour mettre à jour tous les membres de la classe de x
    for (int i = 0; i < equiv.length; i++) {
      if (naiveFind(i) == repX)
        equiv[i] = repY;
    }
    return naiveFind(y);
  }

  /** Délègue à naiveFind — point d'entrée uniforme. */
  public static int find(int n) {
    return naiveFind(n);
  }

  /** Délègue à naiveUnion — point d'entrée uniforme. */
  public static int union(int x, int y) {
    return naiveUnion(x, y);
  }
}
