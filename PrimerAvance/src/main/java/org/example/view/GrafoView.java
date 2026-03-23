package org.example.view;

import org.example.controller.UsuarioController;
import org.example.model.Grafo;
import org.example.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class GrafoView extends JPanel {

    private final Grafo grafo;
    private final Map<String, Point> posiciones;
    private final NavigationManager nav;
    private final UsuarioController userController;
    private final boolean esInteractivo;

    private final int DIAMETRO_NODO = 90;
    private final int ANCHO_VIRTUAL = 1800;
    private final int ALTO_VIRTUAL = 1200;

    public GrafoView(Grafo grafo, NavigationManager nav, boolean esInteractivo) {
        this.grafo = grafo;
        this.nav = nav;
        this.userController = nav.getUsuarioController();
        this.esInteractivo = esInteractivo;
        this.posiciones = new HashMap<>();

        // FIX 1: Forzar opacidad y color de fondo sólido
        setBackground(Color.WHITE);
        setOpaque(true);

        // Definimos el tamaño del "lienzo" para que el scroll funcione bien
        setPreferredSize(new Dimension(ANCHO_VIRTUAL, ALTO_VIRTUAL));

        if (this.esInteractivo) {
            configurarEventosMouse();
        }

        SwingUtilities.invokeLater(this::generarPosicionesArbol);
    }

    private void configurarEventosMouse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!esInteractivo) return;

                Usuario clicado = obtenerUsuarioEnPosicion(e.getPoint());
                if (clicado == null) return;

                if (SwingUtilities.isLeftMouseButton(e)) {
                    manejarClickIzquierdo(clicado);
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    manejarClickDerecho(clicado);
                }
            }
        });
    }

    private void manejarClickIzquierdo(Usuario clicado) {
        Usuario logueado = nav.getUsuarioLogueado();
        if (logueado == null || clicado.equals(logueado)) return;

        userController.agregarAmistad(logueado, clicado);
        generarPosicionesArbol();

        JOptionPane.showMessageDialog(this, "¡Conexión establecida con " + clicado.getPrimerNombre() + "!");

        this.revalidate();
        this.repaint();
    }

    private void manejarClickDerecho(Usuario clicado) {
        Usuario logueado = nav.getUsuarioLogueado();
        if (logueado == null || clicado.equals(logueado)) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + clicado.getPrimerNombre() + " de tus amistades?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            userController.eliminarAmistad(logueado, clicado);
            generarPosicionesArbol();
            this.revalidate();
            this.repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        // FIX 2: Obligatorio llamar a super para limpiar rastros de scroll
        super.paintComponent(g);

        // FIX 3: Limpieza manual extra por si el scroll es muy rápido
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        if (posiciones.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // --- 1. DIBUJAR ARISTAS ---
        g2.setColor(new Color(200, 200, 200));
        g2.setStroke(new BasicStroke(1.5f));

        for (Usuario u : grafo.getAdjList().keySet()) {
            Point p1 = posiciones.get(u.getUsername());
            if (p1 == null) continue;
            for (Usuario amigo : grafo.getAdjList().get(u)) {
                Point p2 = posiciones.get(amigo.getUsername());
                if (p2 != null) g2.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        // --- 2. DIBUJAR NODOS ---
        Usuario logueado = nav.getUsuarioLogueado();

        for (Usuario u : grafo.getAdjList().keySet()) {
            Point p = posiciones.get(u.getUsername());
            if (p != null) {
                int x = p.x - DIAMETRO_NODO / 2;
                int y = p.y - DIAMETRO_NODO / 2;

                if (logueado != null && u.getUsername().equals(logueado.getUsername())) {
                    g2.setColor(new Color(255, 215, 0, 200));
                    g2.setStroke(new BasicStroke(5));
                    g2.drawOval(x - 4, y - 4, DIAMETRO_NODO + 8, DIAMETRO_NODO + 8);
                }

                AvatarView.paintAvatar(g2, u, x, y, DIAMETRO_NODO);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Arial", Font.BOLD, 13));
                String nombre = u.getPrimerNombre();
                int anchoT = g2.getFontMetrics().stringWidth(nombre);
                g2.drawString(nombre, p.x - (anchoT / 2), y + DIAMETRO_NODO + 15);

                g2.setFont(new Font("Arial", Font.PLAIN, 11));
                g2.setColor(Color.GRAY);
                String tag = "@" + u.getUsername();
                int anchoTag = g2.getFontMetrics().stringWidth(tag);
                g2.drawString(tag, p.x - (anchoTag / 2), y + DIAMETRO_NODO + 28);
            }
        }
    }

    private Usuario obtenerUsuarioEnPosicion(Point p) {
        for (Map.Entry<String, Point> entry : posiciones.entrySet()) {
            Point centroNodo = entry.getValue();
            double distancia = Math.sqrt(Math.pow(p.x - centroNodo.x, 2) + Math.pow(p.y - centroNodo.y, 2));
            if (distancia <= DIAMETRO_NODO / 2.0) {
                return grafo.getAdjList().keySet().stream()
                        .filter(u -> u.getUsername().equals(entry.getKey()))
                        .findFirst().orElse(null);
            }
        }
        return null;
    }

    private void generarPosicionesArbol() {
        posiciones.clear();
        List<Usuario> todos = grafo.getAdjList().keySet().stream().toList();
        if (todos.isEmpty()) return;

        Usuario raiz = nav.getUsuarioLogueado();
        if (raiz == null) raiz = todos.get(0);

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

        for (Usuario u : todos) {
            if (!visitadosConNivel.containsKey(u)) {
                niveles.computeIfAbsent(maxNivel + 1, k -> new ArrayList<>()).add(u);
            }
        }

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
}