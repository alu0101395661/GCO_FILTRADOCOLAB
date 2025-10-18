
public class Similitud {

    private double[][] matrizSimilitud;
    private String metrica;  // "pearson", "coseno" o "euclidea"

    /** Constructor  */
    public Similitud(String metrica) {
        this.metrica = metrica.toLowerCase();
    }

     
    public void calcularTodo(UtilityMatrix um) {
        int nUsuarios = um.getUserCount();
        matrizSimilitud = new double[nUsuarios][nUsuarios];

        for (int i = 0; i < nUsuarios; i++) {
            for (int j = i; j < nUsuarios; j++) {
                double sim;
                if (i == j) sim = 1.0;
                else {
                    double[] u = um.getUserRatings(i);
                    double[] v = um.getUserRatings(j);
                    sim = calcular(u, v);
                }
                matrizSimilitud[i][j] = sim;
                matrizSimilitud[j][i] = sim;
            }
        }
    }

    /** Calcula la similitud entre dos usuarios según la métrica seleccionada */
    public double calcular(double[] u, double[] v) {
        switch (metrica) {
            case "pearson": return pearson(u, v);
            case "coseno": return coseno(u, v);
            case "euclidea": return euclidea(u, v);
            default:
                throw new IllegalArgumentException("Métrica no reconocida: " + metrica);
        }
    }

 
    /** Similitud de Pearson */
    private double pearson(double[] a, double[] b) {
        double mediaA = media(a);
        double mediaB = media(b);
        double num = 0.0, denA = 0.0, denB = 0.0;

        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i]) && !Double.isNaN(b[i])) {
                double da = a[i] - mediaA;
                double db = b[i] - mediaB;
                num += da * db;
                denA += da * da;
                denB += db * db;
            }
        }
        if (denA == 0 || denB == 0) return 0.0;
        return num / Math.sqrt(denA * denB);
    }

    /** Similitud del coseno */
    private double coseno(double[] a, double[] b) {
        double num = 0.0, denA = 0.0, denB = 0.0;

        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i]) && !Double.isNaN(b[i])) {
                num += a[i] * b[i];
                denA += a[i] * a[i];
                denB += b[i] * b[i];
            }
        }
        if (denA == 0 || denB == 0) return 0.0;
        return num / (Math.sqrt(denA) * Math.sqrt(denB));
    }

    
    private double euclidea(double[] a, double[] b) {
        double suma = 0.0;

        for (int i = 0; i < a.length; i++) {
            if (!Double.isNaN(a[i]) && !Double.isNaN(b[i])) {
                double diff = a[i] - b[i];
                suma += diff * diff;
            }
        }

        double dist = Math.sqrt(suma);
        return 1.0 / (1.0 + dist);
    }


    private double media(double[] v) {
        double suma = 0;
        int count = 0;
        for (double x : v) {
            if (!Double.isNaN(x)) {
                suma += x;
                count++;
            }
        }
        return (count > 0) ? suma / count : 0.0;
    }

    /** Devuelve la matriz de similitud */
    public double[][] getMatrizSimilitud() {
        return matrizSimilitud;
    }

    /** Imprime la matriz de similitud con formato */
    public void imprimirMatriz() {
        System.out.println("\nMatriz de similitud (" + metrica + "):");
        for (double[] fila : matrizSimilitud) {
            for (double valor : fila) {
                System.out.printf("%7.3f ", valor);
            }
            System.out.println();
        }
    }
}
