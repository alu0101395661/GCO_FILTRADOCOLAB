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
        int[] vecinos = topNVecinos(simMatrix[u], ratings, u, i, numVecinos);


        double num = 0.0;
        double den = 0.0;

        String vecs = "";

        for (int v : vecinos) {
            vecs += v + ",";

            double simUV = simMatrix[u][v];
            System.out.println("Similitud de " + u + " con " + v + ": " + simUV);

            double ratingVI = ratings[v][i];
            System.out.println("Valoración de " + v + " sobre " + i  + ": " + ratingVI);

            if (!Double.isNaN(ratingVI)) {
                num += simUV * ratingVI;
                den += Math.abs(simUV);
            }
        }

        System.out.println("Predicción de " + u + " sobre " + i + ": " + num/den);

        System.out.println("Vecinos de " + u + " con el item " + i + ": ");
        System.out.println(vecs);
        System.out.println();

        if (den == 0) return Double.NaN; // no hay vecinos válidos
        return num / den;
    }

    
    public double predecirConMedia(int u, int i) {
        double[][] simMatrix = sim.getMatrizSimilitud();
        double[][] ratings = um.getMatrix();

        double mediaU = um.getUserMean(u);
        int[] vecinos = topNVecinos(simMatrix[u], ratings, u, i, numVecinos);

        System.out.println("Media de " + u + ": " + mediaU);

        double num = 0.0;
        double den = 0.0;

        String vecs = "";

        for (int v : vecinos) {
            vecs += v + ",";

            double simUV = simMatrix[u][v];
            System.out.println("Similitud de " + u + " con " + v + ": " + simUV);

            double ratingVI = ratings[v][i];
            System.out.println("Valoración de " + v + " sobre " + i + ": " + ratingVI);

            if (!Double.isNaN(ratingVI)) {
                double mediaV = um.getUserMean(v);
                System.out.println("Media del vecino " + v + ": " + mediaV);
                num += simUV * (ratingVI - mediaV);
                den += Math.abs(simUV);
            }
        }

        System.out.println("Predicción de " + u + " sobre " + i + ": " + num/den);

        System.out.println("Vecinos de " + u + " con el item " + i + ": ");
        System.out.println(vecs);
        System.out.println();

        if (den == 0) return Double.NaN;
        return mediaU + (num / den);
    }

    /**
     * Rellena la matriz de utilidad con las predicciones (para todos los NaN).
     * 
     * @param tipoPrediccion "simple" o "media"
     */
    public double[][] generarMatrizPredicha(String tipoPrediccion, double valorMedio) {
        double[][] original = um.getMatrix();
        int nUsuarios = um.getUserCount();
        int nItems = um.getItemCount();
        double[][] predicha = new double[nUsuarios][nItems];
        double[][] recomendaciones = new double[nUsuarios][nItems];
        int[] num_predicciones = new int[nUsuarios];

        for (int u = 0; u < nUsuarios; u++) {
            num_predicciones[u] = 0;
            for (int i = 0; i < nItems; i++) {
                if (Double.isNaN(original[u][i])) {
                    double valor;
                    if (tipoPrediccion.equalsIgnoreCase("simple"))
                        valor = predecirSimple(u, i);
                    else
                        valor = predecirConMedia(u, i);
                    predicha[u][i] = valor;
                    recomendaciones[u][i] = valor;
                    num_predicciones[u] += 1; 
                } else {
                    predicha[u][i] = original[u][i];
                }
            }
        }

        

        for (int i = 0; i <= nUsuarios; i++) {
            if (num_predicciones[i] != 0) {
                Map<Integer, Double> recs = new HashMap<Integer, Double>();

                for (int j = 0; j <= nItems; j++) {
                    if (recomendaciones[i][j] == Double.NaN) {
                        continue;
                    } else if (recomendaciones[i][j] < valorMedio) {
                        continue;
                    } else {
                        recs.put(j, recomendaciones[i][j]);
                    }
                }

                if (!recs.isEmpty()) {
                    List<Integer> items = recs.entriSet().stream().sorted(Map.Entry.<Integer,Double>comparingByValue().reversed()).limit(3).map(Map.Entry::getKey).collect(Collectors.toList());
                    System.out.println("Recomendación al usuario " + i + ": " );
                    String top = "";
                    
                    for (int it : items) {
                        top += it + ",";
                    }

                    System.out.println(top);
                }
            }
        }

        return predicha;
    }

    /**
     * Obtiene los índices de los N vecinos más similares a un usuario u.
     */
    private int[] topNVecinos(double[] similitudes, double[][] ratings, int usuario, int item, int n) {
        // Crear una lista (índice, similitud)
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < similitudes.length; i++) {
            if (i != usuario && !Double.isNaN(ratings[i][item])) indices.add(i);
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
