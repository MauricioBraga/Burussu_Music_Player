package io.github.mauriciobraga.burussumusicplayer.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import io.github.mauriciobraga.burussumusicplayer.model.Sound;

public class BurussuView extends JFrame {

    public static final String OPEN = "Selecionar música";
    public static final String PAUSE = "Pausa";
    public static final String KARAOKE = "Karaoke";
    public static final String ACAPELLA = "Acappella";
    public static final String PLAY_SOUND = "Tocar música";
    public static final String EXIT = "Encerrar";

    private AbstractButton playButton;
    private JPanel painelCentralplayer;
    private JPanel painelCentralJFrame;
    private JCheckBox[] checkboxes;
    private JColorLabel lblNome_Musica = new JColorLabel("", Color.YELLOW);
    private ActionListener actionListener;

    public BurussuView(String title) {
        super(title);
        setUndecorated(true);

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setOpaque(false);
        titleBar.setPreferredSize(new Dimension(getWidth(), 40));
        titleBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY));

        JColorLabel titleLabel = new JColorLabel("                                     " + title + "                                                            ", Color.gray);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleBar.add(titleLabel, BorderLayout.WEST);

        JButton closeButton = new JButton("X");
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(new Color(140, 7, 0));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Dialog", Font.BOLD, 16));
        closeButton.setPreferredSize(new Dimension(45, 40));
        closeButton.addActionListener(e -> dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING)));
        titleBar.add(closeButton, BorderLayout.EAST);

        final Point[] clickPoint = {null};
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                clickPoint[0] = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point currentScreenLocation = e.getLocationOnScreen();
                setLocation(currentScreenLocation.x - clickPoint[0].x,
                        currentScreenLocation.y - clickPoint[0].y);
            }
        });

        JLabel background = new JLabel();
        background.setLayout(new BorderLayout());
        background.setBackground(new Color(60, 0, 0));
        background.setOpaque(true);

        java.net.URL backgroundUrl = getClass().getResource("/background_vermelho.png");
        if (backgroundUrl != null) {
            background.setIcon(new ImageIcon(backgroundUrl));
        }
        setContentPane(background);

        painelCentralplayer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelCentralplayer.setPreferredSize(new Dimension(350, 175));
        JPanel painelControlesplayer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelControlesplayer.setPreferredSize(new Dimension(350, 50));

        painelCentralJFrame = new JPanel(new BorderLayout());
        painelCentralJFrame.setPreferredSize(new Dimension(painelCentralplayer.getWidth() + 30, painelCentralplayer.getHeight() + 30));

        JPanel painelPaddingVertical = new JPanel();
        painelPaddingVertical.setPreferredSize(new Dimension(painelCentralJFrame.getWidth(), 60));
        painelPaddingVertical.setOpaque(false);
        painelPaddingVertical.add(lblNome_Musica);

        JPanel painelPaddingHorizontal = new JPanel();
        painelPaddingHorizontal.setPreferredSize(new Dimension(20, 60));
        painelPaddingHorizontal.setOpaque(false);

        painelCentralJFrame.add(painelPaddingVertical, BorderLayout.NORTH);
        painelCentralJFrame.add(painelPaddingHorizontal, BorderLayout.WEST);
        painelCentralJFrame.add(painelCentralplayer, BorderLayout.CENTER);
        painelCentralJFrame.setOpaque(false);
        painelCentralplayer.setOpaque(false);
        painelControlesplayer.setOpaque(false);

        playButton = createButton(PLAY_SOUND, true);
        playButton.setEnabled(false);

        painelControlesplayer.setLayout(new FlowLayout(FlowLayout.LEFT));
        painelControlesplayer.add(createButton(OPEN, false));
        painelControlesplayer.add(playButton);
        painelControlesplayer.add(createButton(PAUSE, true));
        painelControlesplayer.add(createButton(KARAOKE, true));
        painelControlesplayer.add(createButton(ACAPELLA, true));
        painelControlesplayer.add(createButton(EXIT, false));

        getContentPane().add(titleBar, BorderLayout.NORTH);
        getContentPane().add(painelCentralJFrame, BorderLayout.CENTER);
        getContentPane().add(painelControlesplayer, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 680);
        setLocationRelativeTo(null);
        validate();
        setVisible(true);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public AbstractButton createButton(String name, boolean canToggle) {
        AbstractButton button = canToggle ? new JToggleRoundedButton(name) : new JRoundedButton(name);
        button.addActionListener(e -> {
            if (actionListener != null) {
                actionListener.actionPerformed(e);
            }
        });
        return button;
    }

    public File[] selectAudioFiles() {
        personalizaCoresJFileChooser();

        JFileChooser chooser = new JFileChooser();
        chooser.setOpaque(false);
        try {
            chooser.setCurrentDirectory(new File((new File(".").getCanonicalPath())));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("WAVE FILES", "wav", "wave");
            chooser.setFileFilter(filter);
        } catch (Exception ex) {
            // ignore
        }
        chooser.setMultiSelectionEnabled(true);
        chooser.showOpenDialog(this);

        File[] files = chooser.getSelectedFiles();
        if (files == null || files.length == 0) {
            return new File[0];
        }

        if (files.length > 0) {
            String nome = obterNomeDaPastaMae(files[0]);
            setMusicName(nome);
        }

        return files;
    }

    public void clearPlayerPanel() {
        painelCentralplayer.removeAll();
        painelCentralplayer.revalidate();
        painelCentralplayer.repaint();
    }

    public void renderChannels(Sound[] canais, ActionListener channelSelectionListener) {
        clearPlayerPanel();
        checkboxes = new JCheckBox[canais.length];
        for (int i = 0; i < canais.length; i++) {
            PanelLevelMeter meter = new PanelLevelMeter(null);
            meter.setLayout(new FlowLayout(FlowLayout.LEFT));
            meter.setPreferredSize(new Dimension(650, 20));
            meter.setSound(canais[i]);

            if (i < 9) {
                checkboxes[i] = new JCheckBox("Canal 0" + (i + 1), true);
            } else {
                checkboxes[i] = new JCheckBox("Canal " + (i + 1), true);
            }
            checkboxes[i].setForeground(Color.WHITE);
            checkboxes[i].setOpaque(false);
            checkboxes[i].addActionListener(channelSelectionListener);

            painelCentralplayer.add(checkboxes[i]);
            painelCentralplayer.add(meter);
        }
        painelCentralplayer.revalidate();
        painelCentralplayer.repaint();
    }

    public void enablePlayButton(boolean enabled) {
        playButton.setEnabled(enabled);
        playButton.setSelected(false);
    }

    public void setMusicName(String nome) {
        lblNome_Musica.setText((nome == null ? "" : nome) + "  ");
    }

    public JCheckBox[] getCheckboxes() {
        return checkboxes;
    }

    public String obterNomeDaPastaMae(File arquivo) {
        File pastaPai = arquivo.getParentFile();
        if (pastaPai != null) {
            return pastaPai.getName();
        }
        return "";
    }

    public void personalizaCoresJFileChooser() {
        Color corFundo = new Color(99, 6, 0);
        Color corFrente = Color.WHITE;

        UIManager.put("ComboBox.buttonHighlight", Color.green);
        UIManager.put("activeCaption", new javax.swing.plaf.ColorUIResource(Color.blue));
        UIManager.put("activeCaptionText", new javax.swing.plaf.ColorUIResource(Color.green));
        UIManager.put("Panel.background", corFundo);
        UIManager.put("Label.foreground", corFrente);
        UIManager.put("Button.background", corFundo);
        UIManager.put("Button.foreground", corFrente);
        UIManager.put("ComboBox.background", corFundo);
        UIManager.put("ComboBox.foreground", corFrente);
        UIManager.put("TextField.background", corFundo);
        UIManager.put("TextField.foreground", corFrente);
        UIManager.put("ToolBar.background", corFundo);
        UIManager.put("Viewport.background", corFundo);
        UIManager.put("Viewport.foreground", corFrente);
        UIManager.put("ScrollPane.background", corFundo);
        UIManager.put("List.background", corFundo);
        UIManager.put("List.foreground", corFrente);

        SwingUtilities.updateComponentTreeUI(this);
    }
}


