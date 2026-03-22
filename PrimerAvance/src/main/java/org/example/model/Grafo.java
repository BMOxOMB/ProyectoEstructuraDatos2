package org.example.model;

import java.util.*;

public class Grafo {

    // Lista de adyacencia: Eficiente en tiempo y recursos
    private Map<Usuario, List<Usuario>> adjList;

    public Grafo() {
        adjList = new HashMap<>();
    }

    // Requisito 1: Nuevo Usuario
    public void agregarUsuario(Usuario u) {
        if (u != null) {
            adjList.putIfAbsent(u, new ArrayList<>());
        }
    }

    // Requisito 3: Click Izquierdo (Crear relación)
    public void agregarAmistad(Usuario u1, Usuario u2) {
        if (u1 == null || u2 == null || u1.equals(u2)) return;

        // Aseguramos que ambos existan en el grafo antes de relacionarlos
        agregarUsuario(u1);
        agregarUsuario(u2);

        // Evitar duplicados en la lista de amigos
        if (!adjList.get(u1).contains(u2)) {
            adjList.get(u1).add(u2);
        }
        if (!adjList.get(u2).contains(u1)) {
            adjList.get(u2).add(u1);
        }
    }

    // Requisito 3: Click Derecho (Eliminar relación)
    public void eliminarAmistad(Usuario u1, Usuario u2) {
        if (u1 == null || u2 == null) return;

        List<Usuario> amigosU1 = adjList.get(u1);
        List<Usuario> amigosU2 = adjList.get(u2);

        if (amigosU1 != null) amigosU1.remove(u2);
        if (amigosU2 != null) amigosU2.remove(u1);
    }

    // Necesario para el recorrido BFS de sugerencias
    public List<Usuario> getAmigos(Usuario u) {
        return adjList.getOrDefault(u, new ArrayList<>());
    }

    public Map<Usuario, List<Usuario>> getAdjList() {
        return adjList;
    }

    // Método útil para validar si un usuario ya existe por su username
    public Usuario buscarPorUsername(String username) {
        return adjList.keySet().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }
}