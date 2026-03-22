package org.example.view;

import org.example.model.Usuario;
import org.example.model.Grafo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class GrafoView extends JPanel {

    private Grafo grafo;
    private Map<Usuario, Point> posiciones;
    private Usuario seleccionadoParaAmistad = null; // Renombrado para claridad

    public GrafoView(Grafo grafo) {
        this.grafo = grafo;
        this.posiciones = new HashMap<>();
        setBackground(Color.WHITE);

        // EVENTOS DE MOUSE CORREGIDOS SEGÚN CONSIGNA
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Usuario u = getUsuarioEnPosicion(e.getPoint());
                if (u == null) {
                    seleccionadoParaAmistad = null; // Resetear si toca el fondo
                    repaint();
                    return;
                }

                // CLICK IZQUIERDO → Agregar Amistad (Requisito 3)
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (seleccionadoParaAmistad == null) {
                        seleccionadoParaAmistad = u;
                    } else if (!seleccionadoParaAmistad.equals(u)) {
                        grafo.agregarAmistad(seleccionadoParaAmistad, u);
                        seleccionadoParaAmistad = null;
                    }
                }

                // CLICK DERECHO → Eliminar Amistad
                else if (SwingUtilities.isRightMouseButton(e)) {
                    if (seleccionadoParaAmistad != null && !seleccionadoParaAmistad.equals(u)) {
                        grafo.eliminarAmistad(seleccionadoParaAmistad, u);
                        seleccionadoParaAmistad = null;
                    }
                }
                repaint();
            }
        });
    }

    private void asegurarPosiciones() {
        Random rand = new Random();
        for (Usuario u : grafo.getAdjList().keySet()) {
            if (!posiciones.containsKey(u)) {
                // Generar posición dentro de los límites del panel actual
                int x = rand.nextInt(Math.max(getWidth() - 100, 400)) + 50;
                int y = rand.nextInt(Math.max(getHeight() - 100, 300)) + 50;
                posiciones.put(u, new Point(x, y));
            }
        }
    }

    private Usuario getUsuarioEnPosicion(Point p) {
        int radio = 30;
        for (Map.Entry<Usuario, Point> entry : posiciones.entrySet()) {
            if (p.distance(entry.getValue()) < radio) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        asegurarPosiciones(); // Garantiza que los nuevos usuarios aparezcan
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Dibujar líneas de amistad
        g2.setStroke(new BasicStroke(2));
        g2.setColor(new Color(180, 180, 180));
        for (Usuario u : grafo.getAdjList().keySet()) {
            Point p1 = posiciones.get(u);
            for (Usuario amigo : grafo.getAmigos(u)) {
                Point p2 = posiciones.get(amigo);
                if (p1 != null && p2 != null) {
                    g2.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        // 2. Dibujar nodos
        for (Usuario u : posiciones.keySet()) {
            dibujarNodo(g2, u, posiciones.get(u));
        }
    }

    private void dibujarNodo(Graphics2D g2, Usuario u, Point p) {
        int size = 60;
        int x = p.x - size / 2;
        int y = p.y - size / 2;

        // 1. Obtener la imagen
        Image img = null;
        var url = getClass().getResource("/img/" + u.getAvatar());
        if (url != null) {
            img = new ImageIcon(url).getImage();
        }

        // 2. USAR TU CLASE AvatarView para dibujar (Reutilización de código)
        if (img != null) {
            AvatarView.dibujarAvatarCircular(g2, img, x, y, size);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillOval(x, y, size, size);
        }

        // 3. Dibujar el borde con el color del grupo
        g2.setColor(Color.decode(u.getColorGrupo()));
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(x, y, size, size);

        // 4. Nombre
        g2.setColor(Color.BLACK);
        g2.drawString(u.getUsername(), x, y + size + 15);
    }
}