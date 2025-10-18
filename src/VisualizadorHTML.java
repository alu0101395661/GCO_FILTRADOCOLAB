import java.io.*;
import java.awt.Desktop;
import java.util.*;

public class VisualizadorHTML {

    public static void generarHTML(
            UtilityMatrix um, 
            Similitud sim, 
            double[][] matrizPredicha, 
            Prediccion prediccion,
            String metrica,
            int numVecinos,
            String tipoPrediccion) {

        StringBuilder html = new StringBuilder();

        html.append("""
            <html><head>
            <meta charset="UTF-8">
            <title>Resultados - Filtrado Colaborativo</title>
            <style>
                body { 
                    font-family: 'Segoe UI', Arial, sans-serif; 
                    background: #f4f6fa; 
                    color: #222; 
                    padding: 30px;
                    line-height: 1.5;
                }
                h1, h2 {
                    color: #003366;
                    border-bottom: 2px solid #00336633;
                    padding-bottom: 5px;
                }
                table { 
                    border-collapse: collapse; 
                    margin: 20px 0; 
                    width: 100%; 
                    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
                }
                th, td { 
                    border: 1px solid #ccc; 
                    padding: 6px 10px; 
                    text-align: center; 
                }
                th { 
                    background-color: #e9eef5;
                    font-weight: 600;
                }
                td { background-color: #fff; }
                td.prediccion { background-color: #c8f7c5; }
                td.vacio { background-color: #efefef; color: #aaa; }
                td.original { background-color: #ffffff; }

                /* NUEVO: efecto hover en filas */
                tr:hover td {
                    background-color: #dbe9ff;
                    transition: background-color 0.2s ease-in-out;
                }

                .detalle th { background-color: #dceeff; }
                .detalle td:nth-child(3) { background-color: #eaf4ff; }
                .recs th { background-color: #fff2cc; }
                .recs td:nth-child(2) { background-color: #fff8e1; }

                .legend {
                    background: #fff;
                    border: 1px solid #ccc;
                    display: inline-block;
                    padding: 10px;
                    border-radius: 6px;
                    font-size: 0.9em;
                    margin-bottom: 20px;
                }
                .color-box {
                    display: inline-block;
                    width: 14px;
                    height: 14px;
                    margin-right: 5px;
                    vertical-align: middle;
                }
                .resumen {
                    background-color: #ffffff;
                    border: 1px solid #ccc;
                    padding: 15px 20px;
                    border-radius: 6px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.05);
                    margin-bottom: 25px;
                }
                .resumen h3 {
                    margin-top: 0;
                    color: #003366;
                }
                .resumen p {
                    margin: 4px 0;
                }
                .resumen ul {
                    list-style-type: none;
                    padding-left: 10px;
                    margin-top: 5px;
                }
                .resumen ul li {
                    margin: 4px 0;
                }
                footer {
                    font-size: 0.8em; 
                    color: #555; 
                    margin-top: 40px; 
                    text-align: center; 
                }
            </style>
            </head><body>
            <h1>Resultados del Sistema de Recomendación</h1>
        """);

        // Bloque de resumen de ejecución mejorado
        html.append("<div class='resumen'>");
        html.append("<h3>Resumen de ejecución</h3>");
        html.append("<ul>");
        html.append("<li><strong>Métrica utilizada:</strong> ").append(metrica).append("</li>");
        html.append("<li><strong>Número de vecinos (k):</strong> ").append(numVecinos).append("</li>");
        html.append("<li><strong>Tipo de predicción:</strong> ");
        if (tipoPrediccion.equalsIgnoreCase("simple")) {
            html.append("Predicción simple (basada directamente en las valoraciones de los vecinos)</li>");
        } else {
            html.append("Predicción con diferencia de la media (ajustada por la desviación respecto a la media del usuario)</li>");
        }
        html.append("<li><strong>Total de usuarios:</strong> ").append(um.getUserCount()).append("</li>");
        html.append("<li><strong>Total de ítems:</strong> ").append(um.getItemCount()).append("</li>");
        html.append("<li><strong>Predicciones generadas:</strong> ").append(prediccion.getDetalles().size()).append("</li>");
        html.append("</ul>");
        html.append("</div>");

        // MATRICES
        html.append("<h2>Matriz de Utilidad Original</h2>");
        html.append(matrizToHTML(um.getMatrix(), null));

        html.append("<h2>Matriz de Similitud (" + sim.getMetrica() + ")</h2>");
        html.append(matrizToHTML(sim.getMatrizSimilitud(), null));

        html.append("<h2>Matriz de Utilidad con Predicciones</h2>");
        html.append("""
            <div class='legend'>
                <div><span class='color-box' style='background:#ffffff; border:1px solid #ccc;'></span> Valor original</div>
                <div><span class='color-box' style='background:#c8f7c5; border:1px solid #ccc;'></span> Valor predicho</div>
                <div><span class='color-box' style='background:#efefef; border:1px solid #ccc;'></span> Valor desconocido</div>
            </div>
        """);
        html.append(matrizToHTML(matrizPredicha, um.getMatrix()));

        // DETALLES DE PREDICCIONES
        html.append("<h2>Detalle de Predicciones</h2>");
        html.append("""
            <table class='detalle'>
            <tr><th>Usuario</th><th>Ítem</th><th>Predicción</th><th>Vecinos</th></tr>
        """);
        for (String[] fila : prediccion.getDetalles()) {
            html.append("<tr>");
            for (String celda : fila)
                html.append("<td>").append(celda == null ? "" : celda).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");

        // RECOMENDACIONES
        html.append("<h2>Recomendaciones Finales</h2>");
        html.append("""
            <table class='recs'>
            <tr><th>Usuario</th><th>Ítems recomendados (Top-3)</th></tr>
        """);
        for (Map.Entry<Integer, List<Integer>> entry : prediccion.getRecomendacionesFinales().entrySet()) {
            html.append("<tr>");
            html.append("<td>").append(entry.getKey()).append("</td>");
            html.append("<td>").append(entry.getValue().toString().replaceAll("[\\[\\]]", "")).append("</td>");
            html.append("</tr>");
        }
        html.append("</table>");

        html.append("<footer>GCO 2025-2026</footer>");
        html.append("</body></html>");

        // CREACIÓN Y APERTURA DE ARCHIVO
        try {
            File file = new File("resultados.html");
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(html.toString());
            }

            System.out.println("\n Resultados exportados a: " + file.getAbsolutePath());
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(file.toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Convierte matrices a tabla HTML con distinción de predichas/originales
    private static String matrizToHTML(double[][] matriz, double[][] original) {
        StringBuilder sb = new StringBuilder("<table>");
        for (int i = 0; i < matriz.length; i++) {
            sb.append("<tr>");
            for (int j = 0; j < matriz[i].length; j++) {
                double valor = matriz[i][j];
                boolean vacio = Double.isNaN(valor);
                boolean esPrediccion = (original != null && Double.isNaN(original[i][j]));

                if (vacio) {
                    sb.append("<td class='vacio'>-</td>");
                } else {
                    String clase = esPrediccion ? "prediccion" : "original";
                    sb.append("<td class='").append(clase).append("'>");
                    sb.append(String.format("%.3f", valor));
                    sb.append("</td>");
                }
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }
}
