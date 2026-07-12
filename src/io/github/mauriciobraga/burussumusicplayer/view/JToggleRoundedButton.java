package io.github.mauriciobraga.burussumusicplayer.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JToggleRoundedButton extends JToggleButton {

    private Color normalColor = Color.WHITE;
    private Color selectedColor = Color.GREEN;
    // private Color hoverColor = new Color(255, 0, 0); // Hover mais claro
    private Color hoverColor = Color.YELLOW;
    private boolean isHovering = false;

    public JToggleRoundedButton(String text) {
        setText(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setFont(new Font("SansSerif", Font.BOLD, 16));
        setForeground(normalColor);
        setMargin(new Insets(10, 20, 10, 20));

        // Efeito hover
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovering = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovering = false;
                repaint();
            }
        });

        // Atualiza o botão quando clicado
        addActionListener(e -> repaint());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define cor com base no estado
        Color currentColor;
        if (isSelected()) {
            currentColor = selectedColor;
        } else if (isHovering) {
            currentColor = hoverColor;
        } else {
            currentColor = normalColor;
        }

        // Desenha borda arredondada
        g2.setColor(currentColor);
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);

        // Desenha texto centralizado
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getAscent();
        g2.setColor(currentColor);
        g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 4);

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Nenhuma borda padrão
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(150, 40);
    }
}
