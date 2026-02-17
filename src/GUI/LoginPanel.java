package GUI;

import Exceptions.InvalidCredentialsException;
import MainManagement.Main;
import MainManagement.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

public class LoginPanel extends JPanel {
    private MainFrame parentFrame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private Image backgroundImage;

    public LoginPanel(MainFrame frame) {
        this.parentFrame = frame;

        // Aliniez pe verticala toate componentele
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(30, 20, 20, 20));

        try {
            URL imgUrl = getClass().getResource("/loginBkg.jpg");
            if (imgUrl != null) {
                backgroundImage = ImageIO.read(imgUrl);
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        JLabel titleLabel = new JLabel("Chess Game");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        addCentered(titleLabel);

        JLabel subtitleLabel = new JLabel("Sign in to start");
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        addCentered(subtitleLabel);
        addSpacer(20);

        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setForeground(Color.WHITE);
        addCentered(emailLabel);
        addSpacer(5);
        // Camp text pt email
        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(200, 30));
        addCentered(emailField);
        addSpacer(15);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(Color.WHITE);
        addCentered(passLabel);
        addSpacer(5);
        // Camp text pt parola
        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(200, 30));
        addCentered(passwordField);
        addSpacer(25);

        // Butoane
        JButton loginButton = new JButton("Sign In");
        loginButton.setBackground(new Color(60, 180, 115)); // Verde
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setMaximumSize(new Dimension(150, 30));
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        addCentered(loginButton);
        addSpacer(15);

        // Un separator orizontal mic
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(200, 5));
        addCentered(sep);
        addSpacer(10);

        JButton registerButton = new JButton("Create Account");
        registerButton.setBackground(Color.ORANGE);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setMaximumSize(new Dimension(150, 30));
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegister();
            }
        });
        addCentered(registerButton);

    }

    // Adaug o componenta si o centrez automat
    private void addCentered(JComponent comp) {
        comp.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(comp);
    }

    // Adaug spatiu gol vertical
    private void addSpacer(int height) {
        add(Box.createRigidArea(new Dimension(0, height)));
    }

    // Logica butoanelor
    private void performLogin() {
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();
        // Daca unul dintre campuri e gol, mesaj de eroare
        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty.");
            return;
        }
        // Altfel se incearca logarea cu credentialele date
        try {
            Main.getInstance().validateEmailFormat(email);
            User user = Main.getInstance().login(email, pass);
            if (user != null) {
                emailField.setText("");
                passwordField.setText("");
                parentFrame.showMainMenu();
            } else {
                throw new InvalidCredentialsException("Invalid credentials.");
            }
        } catch (InvalidCredentialsException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void performRegister() {
        String email = emailField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill in email and password to create a new account.");
            return;
        }

        try {
            Main.getInstance().newAccount(email, pass);
            JOptionPane.showMessageDialog(this, "Account created!");
            emailField.setText("");
            passwordField.setText("");
            parentFrame.showMainMenu();
        } catch (InvalidCredentialsException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Pentru afisarea imaginii de bg
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}