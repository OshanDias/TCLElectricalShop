package com.tclelectrical.view;

import com.tclelectrical.dao.EmployeeDAO;
import com.tclelectrical.model.Employee;
import com.tclelectrical.util.SessionManager;
import com.tclelectrical.util.UITheme;
import java.awt.*;
import javax.swing.*;

public class LoginView extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkShow;
    private final char defaultEcho;
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public LoginView() {
        setTitle("TCL Electrical Shop - Login");
        setSize(900, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        txtPassword = new JPasswordField();
        defaultEcho = txtPassword.getEchoChar();   // remember the bullet char

        add(buildBrandPanel(), BorderLayout.WEST);
        add(buildFormPanel(), BorderLayout.CENTER);
    }

    private JPanel buildBrandPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setPreferredSize(new Dimension(380, 0));
        p.setBackground(UITheme.BG_DARK);

        JPanel inner = new JPanel();
        inner.setOpaque(false);
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

        JLabel bolt = new JLabel("\u26A1");
        bolt.setFont(new Font("Segoe UI", Font.BOLD, 64));
        bolt.setForeground(UITheme.AMBER);
        bolt.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel name = new JLabel("TCL ELECTRICAL");
        name.setFont(new Font("Segoe UI", Font.BOLD, 28));
        name.setForeground(UITheme.TEXT_LIGHT);
        name.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel shop = new JLabel("SHOP");
        shop.setFont(new Font("Segoe UI", Font.BOLD, 28));
        shop.setForeground(UITheme.AMBER);
        shop.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tag = new JLabel("Powering Your Needs");
        tag.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        tag.setForeground(UITheme.TEXT_MUTED);
        tag.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(bolt);
        inner.add(Box.createVerticalStrut(15));
        inner.add(name);
        inner.add(shop);
        inner.add(Box.createVerticalStrut(10));
        inner.add(tag);

        p.add(inner);
        return p;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.SURFACE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 10, 8, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0;

        JLabel title = new JLabel("Sign In");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_LIGHT);
        g.gridy = 0; form.add(title, g);

        JLabel sub = new JLabel("Enter your credentials to continue");
        sub.setFont(UITheme.FONT_LABEL);
        sub.setForeground(UITheme.TEXT_MUTED);
        g.gridy = 1; form.add(sub, g);

        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(UITheme.FONT_LABEL);
        lblUser.setForeground(UITheme.TEXT_LIGHT);
        g.gridy = 2; g.insets = new Insets(18, 10, 2, 10); form.add(lblUser, g);

        txtUsername = new JTextField();
        styleField(txtUsername);
        g.gridy = 3; g.insets = new Insets(2, 10, 8, 10); form.add(txtUsername, g);

        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(UITheme.FONT_LABEL);
        lblPass.setForeground(UITheme.TEXT_LIGHT);
        g.gridy = 4; g.insets = new Insets(8, 10, 2, 10); form.add(lblPass, g);

        styleField(txtPassword);
        g.gridy = 5; g.insets = new Insets(2, 10, 4, 10); form.add(txtPassword, g);

        chkShow = new JCheckBox("Show password");
        chkShow.setBackground(UITheme.SURFACE);
        chkShow.setForeground(UITheme.TEXT_MUTED);
        chkShow.setFocusPainted(false);
        chkShow.addActionListener(e ->
            txtPassword.setEchoChar(chkShow.isSelected() ? (char) 0 : defaultEcho));
        g.gridy = 6; g.insets = new Insets(0, 8, 8, 10); form.add(chkShow, g);

        JButton btnLogin = UITheme.primaryButton("LOGIN");
        g.gridy = 7; g.insets = new Insets(18, 10, 6, 10); form.add(btnLogin, g);

        JButton btnRegister = UITheme.secondaryButton("Create New Account");
        g.gridy = 8; g.insets = new Insets(4, 10, 6, 10); form.add(btnRegister, g);

        JLabel hint = new JLabel("Default login:  admin / admin123");
        hint.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hint.setForeground(UITheme.TEXT_MUTED);
        g.gridy = 9; g.insets = new Insets(4, 10, 8, 10); form.add(hint, g);

        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> {
            dispose();
            new RegisterView().setVisible(true);
        });

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(UITheme.SURFACE);
        wrap.setBorder(BorderFactory.createEmptyBorder(30, 70, 30, 70));
        wrap.add(form);
        return wrap;
    }

    private void styleField(JTextField f) {
        f.setPreferredSize(new Dimension(280, 38));
        f.setBackground(UITheme.BG_DARK);
        f.setForeground(UITheme.TEXT_LIGHT);
        f.setCaretColor(UITheme.AMBER);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.SURFACE_2, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void doLogin() {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.",
                "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Employee emp = employeeDAO.authenticate(user, pass);
        if (emp != null) {
            SessionManager.setCurrentUser(emp);
            dispose();
            new DashboardView().setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}