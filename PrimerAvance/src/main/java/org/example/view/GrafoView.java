package org.example.view;

import org.example.model.Grafo;
import org.example.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GrafoView extends JPanel {

    private final Grafo grafo;
    private final Map<String, Point> posiciones;
    private final NavigationManager nav;
    private final int DIAMETRO_NODO = 90;

    private final int ANCHO_VIRTUAL = 1800;
    private final int ALTO_VIRTUAL = 1200;

    public GrafoView(Grafo grafo, NavigationManager nav) {
        this.grafo = grafo;
        this.nav = nav;
        this.posiciones = new HashMap<>();

        setBackground(Color.WHITE);
        setOpaque(true);
        setPreferredSize(new Dimension(ANCHO_VIRTUAL, ALTO_VIRTUAL));

        // Generar posiciones al cargar
        SwingUtilities.invokeLater(this::generarPosicionesArbol);
    }

    private void generarPosicionesArbol() {
        posiciones.clear();
        List<Usuario> todos = grafo.getAdjList().keySet().stream().toList();
        if (todos.isEmpty()) return;

        // 1. DETERMINAR LA RAÍZ: Usuario logueado
        Usuario raiz = nav.getUsuarioLogueado();

        // Si no hay login (por pruebas), usamos el primero de la lista
        if (raiz == null) {
            raiz = todos.get(0);
        }

        // 2. BFS para organizar por niveles desde la raíz
        Map<Integer, List<Usuario>> niveles = new HashMap<>();
        Map<Usuario, Integer> visitadosConNivel = new HashMap<>();
        Queue<Usuario> cola = new LinkedList<>();

        cola.add(raiz);
        visitadosConNivel.put(raiz, 0);

        int maxNivel = 0;
        while (!cola.isEmpty()) {
            Usuario actual = cola.poll();
            int nivelActual = visitadosConNivel.get(actual);
            maxNivel = Math.max(maxNivel, nivelActual);

            niveles.computeIfAbsent(nivelActual, k -> new ArrayList<>()).add(actual);

            for (Usuario amigo : grafo.getAdjList().get(actual)) {
                if (!visitadosConNivel.containsKey(amigo)) {
                    visitadosConNivel.put(amigo, nivelActual + 1);
                    cola.add(amigo);
                }
            }
        }

        // Manejar usuarios "isla" (sin conexión con la raíz)
        for (Usuario u : todos) {
            if (!visitadosConNivel.containsKey(u)) {
                int nivelIsla = maxNivel + 1;
                niveles.computeIfAbsent(nivelIsla, k -> new ArrayList<>()).add(u);
            }
        }

        // 3. ASIGNAR COORDENADAS
        int totalNiveles = niveles.size();
        int espaciadoVertical = ALTO_VIRTUAL / (totalNiveles + 1);

        for (int i = 0; i < totalNiveles; i++) {
            List<Usuario> listaNivel = niveles.get(i);
            if (listaNivel == null) continue;

            int totalEnNivel = listaNivel.size();
            int espaciadoHorizontal = ANCHO_VIRTUAL / (totalEnNivel + 1);
            int y = (i + 1) * espaciadoVertical;

            for (int j = 0; j < totalEnNivel; j++) {
                Usuario u = listaNivel.get(j);
                int x = (j + 1) * espaciadoHorizontal;
                posiciones.put(u.getUsername(), new Point(x, y));
            }
        }

        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (posiciones.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Dibujar aristas
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1.2f));
        for (Usuario u : grafo.getAdjList().keySet()) {
            Point p1 = posiciones.get(u.getUsername());
            for (Usuario amigo : grafo.getAdjList().get(u)) {
                Point p2 = posiciones.get(amigo.getUsername());
                if (p1 != null && p2 != null) {
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        // Dibujar nodos
        for (Usuario u : grafo.getAdjList().keySet()) {
            Point p = posiciones.get(u.getUsername());
            if (p != null) {
                int x = p.x - DIAMETRO_NODO / 2;
                int y = p.y - DIAMETRO_NODO / 2;
                AvatarView.paintAvatar(g2, u, x, y, DIAMETRO_NODO);

                // Nombre
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                String nombre = u.getPrimerNombre();
                int anchoTexto = g2.getFontMetrics().stringWidth(nombre);
                g2.drawString(nombre, p.x - (anchoTexto / 2), y - 10);
            }
        }
    }
}