import java.io.*;
import java.util.*;


public class UtilityMatrix {

    private double minRating;
    private double maxRating;
    private double[][] matrix;

   
    public void loadFromFile(String filePath) throws IOException {
        List<double[]> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // 1ª línea: valor mínimo
            minRating = Double.parseDouble(br.readLine().trim());
            // 2ª línea: valor máximo
            maxRating = Double.parseDouble(br.readLine().trim());

            // Resto de líneas: usuarios
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // saltar líneas vacías

                String[] tokens = line.trim().split("\\s+");
                double[] values = new double[tokens.length];

                for (int i = 0; i < tokens.length; i++) {
                    if (tokens[i].equals("-")) {
                        values[i] = Double.NaN; // valor desconocido
                    } else {
                        values[i] = Double.parseDouble(tokens[i]);
                    }
                }
                rows.add(values);
            }
        }

        // Convertir lista en matriz
        matrix = new double[rows.size()][];
        for (int i = 0; i < rows.size(); i++) {
            matrix[i] = rows.get(i);
        }
    }

    /** Devuelve el número de usuarios (filas) */
    public int getUserCount() {
        return matrix.length;
    }

    /** Devuelve el número de ítems (columnas) */
    public int getItemCount() {
        return matrix[0].length;
    }

    /** Obtiene la puntuación de un usuario a un ítem */
    public double getRating(int user, int item) {
        return matrix[user][item];
    }

    /** Devuelve todas las puntuaciones de un usuario */
    public double[] getUserRatings(int user) {
        return matrix[user];
    }

    /** Calcula la media de puntuaciones conocidas de un usuario */
    public double getUserMean(int user) {
        double sum = 0.0;
        int count = 0;

        for (double rating : matrix[user]) {
            if (!Double.isNaN(rating)) {
                sum += rating;
                count++;
            }
        }
        return (count > 0) ? sum / count : 0.0;
    }

    /** Imprime la matriz en consola */
    public void printMatrix() {
        System.out.printf("Valor mínimo: %.1f | Valor máximo: %.1f%n", minRating, maxRating);
        System.out.println("Matriz de utilidad:");

        for (double[] row : matrix) {
            for (double value : row) {
                if (Double.isNaN(value)) System.out.print("  -  ");
                else System.out.printf("%4.1f ", value);
            }
            System.out.println();
        }
    }

    // Getters
    public double[][] getMatrix() {
        return matrix;
    }

    public double getMinRating() {
        return minRating;
    }

    public double getMaxRating() {
        return maxRating;
    }
}
