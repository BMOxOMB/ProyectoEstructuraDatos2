package org.example.view;

import org.example.model.Usuario;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AvatarView extends JPanel {

    private Image image;
    private int size = 80;
    private float scale = 1.0f;

    private static final Map<String, Image> imageCache = new HashMap<>();

    public AvatarView() {
        setPreferredSize(new Dimension(size, size));
        setOpaque(false);
    }

    private static Image obtenerImagen(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) nombreArchivo = "default.png";
        if (imageCache.containsKey(nombreArchivo)) return imageCache.get(nombreArchivo);

        try {
            URL url = AvatarView.class.getResource("/img/" + nombreArchivo);
            if (url != null) {
                Image img = new ImageIcon(url).getImage();
                imageCache.put(nombreArchivo, img);
                return img;
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + nombreArchivo);
        }
        return null;
    }

    //Calcula un tono más claro del color original para el fondo del avatar.
    private static Color aclararColor(Color color, int cantidad) {
        int r = Math.min(255, color.getRed() + cantidad);
        int g = Math.min(255, color.getGreen() + cantidad);
        int b = Math.min(255, color.getBlue() + cantidad);
        return new Color(r, g, b);
    }

    //Dibuja el círculo con un tono claro del color del grupo y el PNG encima.
    public static void paintAvatar(Graphics2D g2, Usuario u, int x, int y, int diameter) {
        Image img = obtenerImagen(u.getAvatar());
        Color colorOriginal;

        try {
            colorOriginal = Color.decode(u.getColorGrupo());
        } catch (Exception e) {
            colorOriginal = new Color(200, 200, 200); // Gris claro por defecto
        }

        // Creamos el tono más claro (ajusta el 60 si quieres que sea más o menos blanco)
        Color colorClaro = aclararColor(colorOriginal, 60);

        Shape oldClip = g2.getClip();
        Stroke oldStroke = g2.getStroke();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Dibujar el fondo circular con el tono CLARO
        g2.setColor(colorClaro);
        g2.fillOval(x, y, diameter, diameter);

        // 2. Dibujar la imagen (PNG sin fondo) con recorte circular
        if (img != null) {
            Ellipse2D circle = new Ellipse2D.Double(x, y, diameter, diameter);
            g2.setClip(circle);

            int imgW = img.getWidth(null);
            int imgH = img.getHeight(null);
            double scaleImg = Math.max((double) diameter / imgW, (double) diameter / imgH);
            int newW = (int) (imgW * scaleImg);
            int newH = (int) (imgH * scaleImg);

            int imgX = x - (newW - diameter) / 2;
            int imgY = y - (newH - diameter) / 2;

            g2.drawImage(img, imgX, imgY, newW, newH, null);
            g2.setClip(oldClip);
        }

        // 3. Dibujar el borde con el color ORIGINAL del grupo (para que resalte)
        // o puedes dejarlo Negro como en tu referencia anterior.
        g2.setColor(colorOriginal);
        g2.setStroke(new BasicStroke(2.5f));
        g2.drawOval(x, y, diameter, diameter);

        g2.setStroke(oldStroke);
    }

    public void setImage(Image img) {
        this.image = img;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int diameter = Math.min(w, h);
        int x = (w - (int)(diameter * scale)) / 2;
        int y = (h - (int)(diameter * scale)) / 2;
        int sDiam = (int)(diameter * scale);

        Shape circle = new Ellipse2D.Double(x, y, sDiam, sDiam);
        g2.setClip(circle);
        g2.drawImage(image, x, y, sDiam, sDiam, null);
        g2.dispose();
    }
}