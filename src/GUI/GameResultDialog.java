package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameResultDialog extends JDialog {

    public GameResultDialog(MainFrame parentFrame, String title, String message, int matchPoints, int totalPoints) {
        super(parentFrame, "Game Summary", true); // Modal = true (blocheaza spatele)
        setSize(450, 350);
        setLocationRelativeTo(parentFrame);
        setLayout(new BorderLayout());
        setUndecorated(true);

        // Determin culoarea in functie de titlu (Victorie/Infrangere)
        Color headerColor;
        if (title.toLowerCase().contains("won") || title.toLowerCase().contains("victo")) {
            headerColor = new Color(40, 167, 69); // Verde
        } else if (title.toLowerCase().contains("draw")) {
            headerColor = new Color(108, 117, 125); // Gri
        } else {
            headerColor = new Color(220, 53, 69); // Roșu
        }

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(headerColor);
        headerPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        JLabel titleLabel = new JLabel(title.toUpperCase(), SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(30, 20, 30, 20));

        JLabel msgLabel = new JLabel(message);
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        msgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Puncte meci
        JLabel pointsLabel = new JLabel("Points for this match: " + matchPoints);
        pointsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        pointsLabel.setForeground(new Color(0, 102, 204));
        pointsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Separator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(300, 10));

        // Scor total
        JLabel totalLabel = new JLabel("New Total Score: " + totalPoints);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 22));
        totalLabel.setForeground(new Color(50, 50, 50));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bodyPanel.add(msgLabel);
        bodyPanel.add(Box.createVerticalStrut(20));
        bodyPanel.add(pointsLabel);
        bodyPanel.add(Box.createVerticalStrut(20));
        bodyPanel.add(sep);
        bodyPanel.add(Box.createVerticalStrut(20));
        bodyPanel.add(totalLabel);

        add(bodyPanel, BorderLayout.CENTER);

        // Panou pentru cele 2 butoane
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton menuBtn = new JButton("Back to Main Menu");
        menuBtn.setPreferredSize(new Dimension(160, 40));
        menuBtn.setBackground(new Color(52, 58, 64));
        menuBtn.setForeground(Color.WHITE);
        menuBtn.setFocusPainted(false);
        menuBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Inchide dialogul
                parentFrame.showMainMenu();
            }
        });

        JButton exitBtn = new JButton("Exit Application");
        exitBtn.setPreferredSize(new Dimension(160, 40));
        exitBtn.setBackground(new Color(220, 50, 70));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Inchide aplicatia
            }
        });

        buttonPanel.add(menuBtn);
        buttonPanel.add(exitBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }
}