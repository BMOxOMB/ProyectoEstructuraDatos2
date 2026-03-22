package org.example.view;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class AvatarView extends JPanel {

    private Image image;
    private int size = 80;
    private float scale = 1.0f;

    public AvatarView() {
        setPreferredSize(new Dimension(size, size));
        setOpaque(false);

        // Hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                scale = 1.1f;
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                scale = 1.0f;
                repaint();
            }
        });
    }

    public void setImage(Image img) {
        this.image = img;
        repaint();
    }

    // Método estático para que el Grafo lo use
    public static void dibujarAvatarCircular(Graphics2D g2, Image image, int x, int y, int diameter) {
        if (image == null) return;

        // Guardar el estado original
        Shape oldClip = g2.getClip();

        // Crear máscara circular
        Ellipse2D circle = new Ellipse2D.Double(x, y, diameter, diameter);
        g2.setClip(circle);

        // Lógica de "Cover"
        int imgW = image.getWidth(null);
        int imgH = image.getHeight(null);
        double scaleImg = Math.max((double) diameter / imgW, (double) diameter / imgH);
        int newW = (int) (imgW * scaleImg);
        int newH = (int) (imgH * scaleImg);

        int imgX = x - (newW - diameter) / 2;
        int imgY = y - (newH - diameter) / 2;

        g2.drawImage(image, imgX, imgY, newW, newH, null);

        // Restaurar clip original
        g2.setClip(oldClip);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (image == null) return;

        Graphics2D g2 = (Graphics2D) g.create();

        // suavizado (calidad alta)
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int diameter = Math.min(w, h);

        int scaledDiameter = (int) (diameter * scale);

        int x = (w - scaledDiameter) / 2;
        int y = (h - scaledDiameter) / 2;

        // máscara circular
        Shape circle = new Ellipse2D.Double(x, y, scaledDiameter, scaledDiameter);
        g2.setClip(circle);

        // mantener proporción (cover tipo Instagram)
        int imgW = image.getWidth(null);
        int imgH = image.getHeight(null);

        double scaleImg = Math.max(
                (double) scaledDiameter / imgW,
                (double) scaledDiameter / imgH
        );

        int newW = (int) (imgW * scaleImg);
        int newH = (int) (imgH * scaleImg);

        int imgX = x - (newW - scaledDiameter) / 2;
        int imgY = y - (newH - scaledDiameter) / 2;

        g2.drawImage(image, imgX, imgY, newW, newH, null);

        g2.dispose();
    }
}