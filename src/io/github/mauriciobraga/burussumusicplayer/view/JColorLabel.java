package io.github.mauriciobraga.burussumusicplayer.view;

import javax.swing.*;
import java.awt.*;

public class JColorLabel extends JLabel {

    private int padding = 10;

    private Color corGradiente;
    public JColorLabel(String text, Color c) {
        super(text);
        corGradiente = c;
        setFont(new Font("SansSerif", Font.BOLD, 30));
        setForeground(Color.LIGHT_GRAY); // Apenas como fallback
        setHorizontalAlignment(SwingConstants.LEFT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Habilita antialiasing
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Cria gradiente prateado: branco → cor → branco
        GradientPaint gradient = new GradientPaint(
                0, 0, Color.WHITE,
                0, getHeight(), corGradiente, true
        );

        g2.setPaint(gradient);
        FontMetrics fm = g2.getFontMetrics();
        // int x = (getWidth() - fm.stringWidth(getText())) / 2;
        
        int x = padding; // Alinhado à esquerda com margem
        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

        g2.setFont(getFont());
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}
