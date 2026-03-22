package org.example.service;

import org.example.model.Grafo;
import org.example.model.Usuario;

import java.util.*;

public class RedSocialService {

    private final Grafo grafo;

    public RedSocialService(Grafo grafo) {
        this.grafo = grafo;
    }

    // Nuevo Usuario
    public void agregarUsuario(Usuario usuario) {
        if (usuario != null) {
            grafo.agregarUsuario(usuario);
        }
    }

    // Click Izquierdo
    public void agregarAmistad(Usuario u1, Usuario u2) {
        if (u1 != null && u2 != null && !u1.equals(u2)) {
            grafo.agregarAmistad(u1, u2);
        }
    }

    // Click Derecho
    public void eliminarAmistad(Usuario u1, Usuario u2) {
        if (u1 != null && u2 != null) {
            grafo.eliminarAmistad(u1, u2);
        }
    }

    // BFS → Sugerencias de amigos (Amigos de mis amigos)
    public List<Usuario> sugerencias(Usuario usuario) {
        if (usuario == null) return new ArrayList<>();

        List<Usuario> sugeridos = new ArrayList<>();
        Set<Usuario> visitados = new HashSet<>();
        Queue<Usuario> cola = new LinkedList<>();

        cola.add(usuario);
        visitados.add(usuario);

        int nivel = 0;
        // El BFS explora nivel por nivel (Nivel 1: Amigos directos, Nivel 2: Amigos de amigos)
        while (!cola.isEmpty() && nivel < 2) {
            int size = cola.size();

            for (int i = 0; i < size; i++) {
                Usuario actual = cola.poll();

                // grafo.getAmigos debe devolver la lista de adyacencia del nodo
                for (Usuario vecino : grafo.getAmigos(actual)) {
                    if (!visitados.contains(vecino)) {
                        visitados.add(vecino);
                        cola.add(vecino);

                        // Si no es mi amigo directo y no soy yo mismo, es una sugerencia [cite: 74]
                        if (!grafo.getAmigos(usuario).contains(vecino) && !vecino.equals(usuario)) {
                            sugeridos.add(vecino);
                        }
                    }
                }
            }
            nivel++;
        }
        return sugeridos;
    }

    // Recomendar por intereses
    public List<Usuario> recomendarPorIntereses(Usuario usuario) {
        if (usuario == null || usuario.getIntereses().isEmpty()) return new ArrayList<>();

        List<Usuario> base = sugerencias(usuario);
        List<Usuario> resultado = new ArrayList<>();

        for (Usuario u : base) {
            // Si el usuario sugerido comparte al menos un interés, se recomienda
            boolean comparteInteres = u.getIntereses().stream()
                    .anyMatch(interes -> usuario.getIntereses().contains(interes));

            if (comparteInteres) {
                resultado.add(u);
            }
        }
        return resultado;
    }

    // Método para Grupos
    public void asignarGrupo(Usuario usuario, String nombreGrupo, String color) {
        if (usuario != null) {
            usuario.setGrupo(nombreGrupo, color);
        }
    }
}