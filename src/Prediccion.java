import java.util.*;

public class Prediccion {

    private UtilityMatrix um;
    private Similitud sim;
    private int numVecinos; // número de vecinos a considerar

    public Prediccion(UtilityMatrix um, Similitud sim, int numVecinos) {
        this.um = um;
        this.sim = sim;
        this.numVecinos = numVecinos;
    }

    
    public double predecirSimple(int u, int i) {
        double[][] simMatrix = sim.getMatrizSimilitud();
        double[][] ratings = um.getMatrix();

        // Obtener los vecinos más similares
        int[] vecinos = topNVecinos(simMatrix[u], u, numVecinos);

        double num = 0.0;
        double den = 0.0;

        for (int v : vecinos) {
            double simUV = simMatrix[u][v];
            double ratingVI = ratings[v][i];

            if (!Double.isNaN(ratingVI)) {
                num += simUV * ratingVI;
                den += Math.abs(simUV);
            }
        }

        if (den == 0) return Double.NaN; // no hay vecinos válidos
        return num / den;
    }

    
    public double predecirConMedia(int u, int i) {
        double[][] simMatrix = sim.getMatrizSimilitud();
        double[][] ratings = um.getMatrix();

        double mediaU = um.getUserMean(u);
        int[] vecinos = topNVecinos(simMatrix[u], u, numVecinos);

        double num = 0.0;
        double den = 0.0;

        for (int v : vecinos) {
            double simUV = simMatrix[u][v];
            double ratingVI = ratings[v][i];

            if (!Double.isNaN(ratingVI)) {
                double mediaV = um.getUserMean(v);
                num += simUV * (ratingVI - mediaV);
                den += Math.abs(simUV);
            }
        }

        if (den == 0) return Double.NaN;
        return mediaU + (num / den);
    }

    /**
     * Rellena la matriz de utilidad con las predicciones (para todos los NaN).
     * 
     * @param tipoPrediccion "simple" o "media"
     */
    public double[][] generarMatrizPredicha(String tipoPrediccion) {
        double[][] original = um.getMatrix();
        int nUsuarios = um.getUserCount();
        int nItems = um.getItemCount();
        double[][] predicha = new double[nUsuarios][nItems];

        for (int u = 0; u < nUsuarios; u++) {
            for (int i = 0; i < nItems; i++) {
                if (Double.isNaN(original[u][i])) {
                    double valor;
                    if (tipoPrediccion.equalsIgnoreCase("simple"))
                        valor = predecirSimple(u, i);
                    else
                        valor = predecirConMedia(u, i);
                    predicha[u][i] = valor;
                } else {
                    predicha[u][i] = original[u][i];
                }
            }
        }
        return predicha;
    }

    /**
     * Obtiene los índices de los N vecinos más similares a un usuario u.
     */
    private int[] topNVecinos(double[] similitudes, int usuario, int n) {
        // Crear una lista (índice, similitud)
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < similitudes.length; i++) {
            if (i != usuario) indices.add(i);
        }

        // Ordenar por similitud descendente
        indices.sort((a, b) -> Double.compare(similitudes[b], similitudes[a]));

        // Devolver los n primeros
        return indices.stream().limit(n).mapToInt(Integer::intValue).toArray();
    }

    /**
     * Muestra una matriz predicha en consola (formato legible).
     */
    public void imprimirMatriz(double[][] matriz) {
        System.out.println("\nMatriz de utilidad con predicciones:");
        for (double[] fila : matriz) {
            for (double valor : fila) {
                if (Double.isNaN(valor)) System.out.print("  -  ");
                else System.out.printf("%5.2f ", valor);
            }
            System.out.println();
        }
    }
}
