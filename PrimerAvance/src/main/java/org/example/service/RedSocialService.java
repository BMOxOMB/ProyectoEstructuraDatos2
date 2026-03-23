package org.example.service;

import org.example.model.Grafo;
import org.example.model.Usuario;
import java.util.*;
import java.util.stream.Collectors;

public class RedSocialService {

    private final Grafo grafo;

    public RedSocialService(Grafo grafo) {
        this.grafo = grafo;
    }

    public void agregarUsuario(Usuario usuario) {
        if (usuario != null) {
            grafo.agregarUsuario(usuario);
        }
    }

    public void agregarAmistad(Usuario u1, Usuario u2) {
        if (u1 != null && u2 != null && !u1.equals(u2)) {
            grafo.agregarAmistad(u1, u2);
        }
    }

    public void eliminarAmistad(Usuario u1, Usuario u2) {
        if (u1 != null && u2 != null) {
            grafo.eliminarAmistad(u1, u2);
        }
    }

    /**
     * Establece la conexión simétrica en el grafo (Memoria)
     */
    public void conectarUsuarios(Usuario u1, Usuario u2) {
        if (u1 != null && u2 != null && !u1.equals(u2)) {
            grafo.agregarAmistad(u1, u2);
            System.out.println("DEBUG: Nodo " + u1.getUsername() + " conectado con " + u2.getUsername());
        }
    }

    /**
     * Rompe la conexión simétrica en el grafo (Memoria)
     */
    public void desconectarUsuarios(Usuario u1, Usuario u2) {
        if (u1 != null && u2 != null) {
            grafo.eliminarAmistad(u1, u2);
            System.out.println("DEBUG: Arista eliminada entre " + u1.getUsername() + " e " + u2.getUsername());
        }
    }

    /**
     * BFS Clásico para encontrar amigos de amigos (Nivel 2).
     */
    public List<Usuario> sugerencias(Usuario usuario) {
        if (usuario == null) return new ArrayList<>();

        List<Usuario> sugeridos = new ArrayList<>();
        Set<Usuario> visitados = new HashSet<>();
        Queue<Usuario> cola = new LinkedList<>();

        cola.add(usuario);
        visitados.add(usuario);

        List<Usuario> amigosDirectos = grafo.getAmigos(usuario);
        int nivel = 0;

        while (!cola.isEmpty() && nivel < 2) {
            int size = cola.size();
            for (int i = 0; i < size; i++) {
                Usuario actual = cola.poll();
                for (Usuario vecino : grafo.getAmigos(actual)) {
                    if (!visitados.contains(vecino)) {
                        visitados.add(vecino);
                        cola.add(vecino);

                        // Es sugerencia si: no soy yo y no es mi amigo directo
                        if (!vecino.equals(usuario) && !amigosDirectos.contains(vecino)) {
                            sugeridos.add(vecino);
                        }
                    }
                }
            }
            nivel++;
        }
        return sugeridos;
    }

    /**
     * Refinado: Recomendar personas que compartan intereses.
     * Combina la cercanía del BFS con la afinidad de intereses.
     */
    public List<Usuario> recomendarPorIntereses(Usuario usuario) {
        if (usuario == null || usuario.getIntereses() == null || usuario.getIntereses().isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Obtenemos candidatos potenciales del BFS (amigos de amigos)
        List<Usuario> candidatosCercanos = sugerencias(usuario);

        // 2. Si no hay amigos de amigos, buscamos en todo el grafo (nodos globales)
        // para no dejar la lista vacía si el usuario es nuevo.
        List<Usuario> todosLosNodos = (candidatosCercanos.isEmpty())
                ? grafo.getTodosLosUsuarios()
                : candidatosCercanos;

        List<Usuario> amigosDirectos = grafo.getAmigos(usuario);

        return todosLosNodos.stream()
                .filter(u -> !u.equals(usuario)) // No sugerirse a sí mismo
                .filter(u -> !amigosDirectos.contains(u)) // No sugerir amigos actuales
                .filter(u -> tieneInteresesEnComun(usuario, u)) // Compartir gustos
                .distinct() // Evitar duplicados
                .limit(5) // Limitar a las mejores 5 sugerencias
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar para comparar intereses (Case-insensitive)
     */
    private boolean tieneInteresesEnComun(Usuario principal, Usuario candidato) {
        if (candidato.getIntereses() == null) return false;

        return principal.getIntereses().stream()
                .anyMatch(interes -> candidato.getIntereses().stream()
                        .anyMatch(i -> i.equalsIgnoreCase(interes)));
    }

    public void asignarGrupo(Usuario usuario, String nombreGrupo, String color) {
        if (usuario != null) {
            usuario.setGrupo(nombreGrupo, color);
        }
    }
}