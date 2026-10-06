package com.tclelectrical.view;

import com.tclelectrical.dao.EmployeeDAO;
import com.tclelectrical.exception.InvalidInputException;
import com.tclelectrical.model.Employee;
import com.tclelectrical.util.UITheme;
import com.tclelectrical.util.Validator;
import java.awt.*;
import javax.swing.*;

public class RegisterView extends JFrame {

    private JTextField txtFirst, txtLast, txtNic, txtPhone, txtEmail, txtUsername;
    private JPasswordField txtPass, txtConfirm;
    private JComboBox<String> cmbRole;
    private JCheckBox chkShow;
    private char defaultEcho;
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    private int row = 0;   // running grid row

    public RegisterView() {
        setTitle("TCL Electrical Shop - Create Account");
        setSize(520, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.SURFACE);
        add(buildForm());
    }

    private JScrollPane buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.SURFACE);
        p.setBorder(BorderFactory.createEmptyBorder(24, 40, 24, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        JLabel title = new JLabel("Create New Account");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.AMBER);
        g.gridy = row++; g.insets = new Insets(0, 8, 18, 8);
        p.add(title, g);

        txtFirst    = new JTextField(); styleField(txtFirst);
        txtLast     = new JTextField(); styleField(txtLast);
        txtNic      = new JTextField(); styleField(txtNic);
        txtPhone    = new JTextField(); styleField(txtPhone);
        txtEmail    = new JTextField(); styleField(txtEmail);
        txtUsername = new JTextField(); styleField(txtUsername);
        txtPass     = new JPasswordField(); styleField(txtPass);
        txtConfirm  = new JPasswordField(); styleField(txtConfirm);
        defaultEcho = txtPass.getEchoChar();

        cmbRole = new JComboBox<>(new String[]{"CASHIER", "MANAGER", "ADMIN"});
        styleCombo(cmbRole);

        addLabeled(p, g, "First Name", txtFirst);
        addLabeled(p, g, "Last Name", txtLast);
        addLabeled(p, g, "NIC", txtNic);
        addLabeled(p, g, "Phone (07XXXXXXXX)", txtPhone);
        addLabeled(p, g, "Email (optional)", txtEmail);
        addLabeled(p, g, "Role", cmbRole);
        addLabeled(p, g, "Username", txtUsername);
        addLabeled(p, g, "Password", txtPass);
        addLabeled(p, g, "Confirm Password", txtConfirm);

        chkShow = new JCheckBox("Show password");
        chkShow.setBackground(UITheme.SURFACE);
        chkShow.setForeground(UITheme.TEXT_MUTED);
        chkShow.setFocusPainted(false);
        chkShow.addActionListener(e -> {
            char c = chkShow.isSelected() ? (char) 0 : defaultEcho;
            txtPass.setEchoChar(c);
            txtConfirm.setEchoChar(c);
        });
        g.gridy = row++; g.insets = new Insets(2, 6, 12, 8);
        p.add(chkShow, g);

        JButton btnCreate = UITheme.primaryButton("CREATE ACCOUNT");
        g.gridy = row++; g.insets = new Insets(6, 8, 6, 8);
        p.add(btnCreate, g);

        JButton btnBack = UITheme.secondaryButton("Back to Login");
        g.gridy = row++; g.insets = new Insets(2, 8, 6, 8);
        p.add(btnBack, g);

        btnCreate.addActionListener(e -> doRegister());
        btnBack.addActionListener(e -> { dispose(); new LoginView().setVisible(true); });

        JScrollPane sp = new JScrollPane(p);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private void addLabeled(JPanel p, GridBagConstraints g, String lblText, JComponent field) {
        JLabel l = new JLabel(lblText);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_LIGHT);
        g.gridy = row++; g.insets = new Insets(8, 8, 2, 8);
        p.add(l, g);
        g.gridy = row++; g.insets = new Insets(2, 8, 6, 8);
        p.add(field, g);
    }

    private void doRegister() {
        try {
            Validator.requireText(txtFirst.getText(), "First name");
            Validator.requireText(txtLast.getText(), "Last name");
            Validator.requireText(txtNic.getText(), "NIC");
            Validator.requireNIC(txtNic.getText());
            Validator.requirePhone(txtPhone.getText());
            Validator.requireEmail(txtEmail.getText());
            Validator.requireText(txtUsername.getText(), "Username");

            String pass = new String(txtPass.getPassword());
            String confirm = new String(txtConfirm.getPassword());
            if (pass.length() < 4)
                throw new InvalidInputException("Password must be at least 4 characters.");
            if (!pass.equals(confirm))
                throw new InvalidInputException("Passwords do not match.");

            Employee emp = new Employee();
            emp.setFirstName(txtFirst.getText().trim());
            emp.setLastName(txtLast.getText().trim());
            emp.setNic(txtNic.getText().trim());
            emp.setPhone(txtPhone.getText().trim());
            emp.setEmail(txtEmail.getText().trim());
            emp.setRole((String) cmbRole.getSelectedItem());
            emp.setUsername(txtUsername.getText().trim());
            emp.setPassword(pass);
            emp.setHireDate(new java.sql.Date(System.currentTimeMillis()));
            emp.setSalary(0);
            emp.setStatus("ACTIVE");

            if (employeeDAO.insert(emp)) {
                JOptionPane.showMessageDialog(this,
                    "Account created for " + emp.getFullName() + " (" + emp.getRole() + ")!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginView().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Could not create account. Username or NIC may already exist.",
                    "Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (InvalidInputException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void styleField(JTextField f) {
        f.setPreferredSize(new Dimension(420, 36));
        f.setBackground(UITheme.BG_DARK);
        f.setForeground(UITheme.TEXT_LIGHT);
        f.setCaretColor(UITheme.AMBER);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.SURFACE_2, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    private void styleCombo(JComboBox<String> c) {
        c.setPreferredSize(new Dimension(420, 36));
        c.setBackground(UITheme.BG_DARK);
        c.setForeground(UITheme.TEXT_LIGHT);
    }
}