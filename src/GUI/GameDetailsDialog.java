package GUI;

import GameState.Game;
import GameState.Move;
import GameState.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameDetailsDialog extends JDialog {

    public GameDetailsDialog(JFrame parent, Game game) {
        super(parent, "Game Information", true); // true = modal (blochează fereastra din spate)
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Game ID
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(60, 63, 65)); // Dark header
        JLabel titleLabel = new JLabel("Game #" + game.getGameId());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(Color.WHITE);

        JLabel playersTitle = new JLabel("Players");
        playersTitle.setFont(new Font("Arial", Font.BOLD, 14));
        playersTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(playersTitle);
        contentPanel.add(Box.createVerticalStrut(10));

        JPanel playersListPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        playersListPanel.setBackground(new Color(245, 245, 245)); // Gri deschis
        playersListPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        playersListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (Player p : game.getPlayers()) {
            JPanel pRow = new JPanel(new BorderLayout());
            pRow.setBackground(new Color(245, 245, 245));
            pRow.setBorder(new EmptyBorder(5, 10, 5, 10));

            JLabel nameLbl = new JLabel("\u25CF " + p.getName() + " (" + p.getColor() + ")");
            nameLbl.setFont(new Font("Arial", Font.PLAIN, 13));

            JLabel pointsLbl = new JLabel("Points: " + p.getPoints());
            pointsLbl.setFont(new Font("Arial", Font.BOLD, 13));
            pointsLbl.setForeground(new Color(40, 167, 69));

            pRow.add(nameLbl, BorderLayout.WEST);
            pRow.add(pointsLbl, BorderLayout.EAST);
            playersListPanel.add(pRow);
        }

        playersListPanel.setMaximumSize(new Dimension(1000, 70));
        contentPanel.add(playersListPanel);

        contentPanel.add(Box.createVerticalStrut(20));

        JLabel historyTitle = new JLabel("Move History (" + game.getMoves().size() + " moves)");
        historyTitle.setFont(new Font("Arial", Font.BOLD, 14));
        historyTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(historyTitle);
        contentPanel.add(Box.createVerticalStrut(10));

        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        historyArea.setBackground(new Color(250, 250, 250));

        // Construiesc textul mutarilor
        StringBuilder history = new StringBuilder();
        int moveNum = 1;
        for (Move m : game.getMoves()) {
            history.append(GamePanel.formatMoveLog(m, moveNum));
            moveNum++;
        }
        historyArea.setText(history.toString());

        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        contentPanel.add(scrollPane);

        add(contentPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(5, 10, 10, 10));

        JButton closeBtn = new JButton("Close");
        closeBtn.setBackground(new Color(52, 58, 64));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        footerPanel.add(closeBtn);
        add(footerPanel, BorderLayout.SOUTH);
    }
}