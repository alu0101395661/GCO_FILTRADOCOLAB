# Sistema de Recomendación — Filtrado Colaborativo

Sistema de recomendación basado en **filtrado colaborativo de usuarios**, que permite calcular similitudes y realizar predicciones de valoraciones con distintas métricas.

---

## Descripción

Carga una **matriz de utilidad** (usuarios × ítems) desde un archivo de texto y permite:

- Calcular la **matriz de similitud** entre usuarios según una métrica elegida.
- Generar predicciones para las valoraciones desconocidas (`NaN`).
- Obtener una lista de **ítems recomendados** para cada usuario.
- Visualizar los resultados en un archivo html.

---

## Métricas disponibles


| `pearson` | | `coseno` | | `euclidea` |

---

## Compilación

Desde la raíz del proyecto:

```bash
javac -d src src/*.java
```

---

## Ejecución

Para ejercutar:

java -cp src Main  **ruta al archivo de la matriz** **métrica** a usar:

```bash
java -cp src Main data/matriz.txt pearson
```

Luego, el programa pedirá por consola:

1. El número de vecinos a considerar.  
2. El tipo de predicción:
   - `1` -> Simple  
   - `2` -> Con diferencia de la media  

Mostrará el resultado por consola y generará un archivo html con resultado incluyendo:

- La matriz original  
- La matriz de similitud  
- La matriz con predicciones  
- Detalle de predicciones y recomendaciones finales  

---

## Ejemplo de uso

```bash
java -cp src Main data/matriz.txt coseno
```

Salida esperada (fragmento):
```
Introduce el número de vecinos a considerar: 3
Elige el tipo de predicción:
1. Simple
2. Con diferencia de la media
Opción (1-2): 1

Resultados exportados en: /.../resultados.html
```