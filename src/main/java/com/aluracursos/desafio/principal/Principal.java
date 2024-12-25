package com.aluracursos.desafio.principal;

import com.aluracursos.desafio.models.Datos;
import com.aluracursos.desafio.models.DatosLibros;
import com.aluracursos.desafio.service.ConsumoAPI;
import com.aluracursos.desafio.service.ConvierteDatos;
import com.aluracursos.desafio.util.Utils;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    private Scanner scanner = new Scanner(System.in);


    public void muestraMenu(){
        var json = consumoApi.obtenerDatos(Utils.API_URL_BOOKS);
        var datos = convierteDatos.obtenerDatos(json, Datos.class);

        System.out.println("Top 10 libros mas descargados");
        datos.libros().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDescargas).reversed())
                .map(l -> l.titulo().toUpperCase())
                .limit(10).forEach(System.out::println);

        //buscar libro por nombre

        System.out.println("Digite el nombre del libro:");

        var titulo = scanner.nextLine();
        json = consumoApi.obtenerDatos(Utils.API_URL_BOOKS+"?search="+titulo.replace(" ","+"));
        var datosBucados = convierteDatos.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libro = datosBucados.libros().stream().filter(l -> l.titulo().toUpperCase().contains(titulo.toUpperCase())).findFirst();

        if (libro.isPresent()){
            System.out.println("Libro buscado: ");
            System.out.println(libro.get());
        }else {
            System.out.println("libro no encontrado");
        }

        //estadisticas

        System.out.println("Manejando estatisticas");

        DoubleSummaryStatistics est = datos.libros().stream()
                .filter(d -> d.numeroDescargas() >0).collect(Collectors.summarizingDouble(DatosLibros::numeroDescargas));
        System.out.println("Cantidad maxima de descargas: "+est.getMax());
        System.out.println("Promedio de descargas: "+est.getAverage());
        System.out.println("Cantidad minima de descargas: "+est.getMin());





    }
}
