import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Uso: java Main <ruta_matriz.txt> <metrica>");
            System.out.println("Métricas disponibles: pearson | coseno | euclidea");
            return;
        }

        String ruta = args[0];
        String metrica = args[1].toLowerCase();

        try {
            // Cargar matriz
            UtilityMatrix um = new UtilityMatrix();
            um.loadFromFile(ruta);
            um.printMatrix();

            double val_max = um.getMaxRating();
            double val_min = um.getMinRating();
            double val_medio = (val_max + val_min) / 2;

            // Calcular similitudes
            Similitud sim = new Similitud(metrica);
            sim.calcularTodo(um);
            sim.imprimirMatriz();

            Scanner sc = new Scanner(System.in);

            System.out.print("\nIntroduce el número de vecinos a considerar: ");
            int k = sc.nextInt();

            System.out.println("\nElige el tipo de predicción:");
            System.out.println("1. Simple");
            System.out.println("2. Con diferencia de la media");
            System.out.print("Opción (1-2): ");
            int tipo = sc.nextInt();

            String tipoPrediccion = (tipo == 1) ? "simple" : "media";

            Prediccion prediccion = new Prediccion(um, sim, k);
            double[][] matrizPredicha = prediccion.generarMatrizPredicha(tipoPrediccion, val_medio);
            prediccion.imprimirMatriz(matrizPredicha);
            VisualizadorHTML.generarHTML(um, sim, matrizPredicha, prediccion, metrica, k, tipoPrediccion);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


