package com.tclelectrical.view;

import com.tclelectrical.dao.EmployeeDAO;
import com.tclelectrical.exception.InvalidInputException;
import com.tclelectrical.model.Employee;
import com.tclelectrical.util.UITheme;
import com.tclelectrical.util.Validator;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class EmployeeView extends JFrame {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private JTextField txtFirst, txtLast, txtNic, txtPhone, txtEmail, txtUsername, txtSalary, txtHire, txtSearch;
    private JTextField txtPassword;
    private JComboBox<String> cmbRole, cmbStatus;
    private JTable table;
    private DefaultTableModel model;
    private List<Employee> currentList;
    private int selectedId = -1;

    public EmployeeView() {
        setTitle("TCL Electrical Shop - Employees");
        setSize(1150, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        loadTable(employeeDAO.getAll());
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.SURFACE);
        h.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Employee Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(UITheme.AMBER);
        JButton back = UITheme.secondaryButton("Back");
        back.addActionListener(e -> dispose());
        h.add(title, BorderLayout.WEST); h.add(back, BorderLayout.EAST);
        return h;
    }

    private JPanel buildCenter() {
        JPanel c = new JPanel(new BorderLayout(0, 12));
        c.setBackground(UITheme.BG_DARK);
        c.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));
        c.add(buildForm(), BorderLayout.NORTH);
        c.add(buildTablePanel(), BorderLayout.CENTER);
        return c;
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.SURFACE);
        form.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8); g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1;

        txtFirst = new JTextField(); txtLast = new JTextField(); txtNic = new JTextField();
        txtPhone = new JTextField(); txtEmail = new JTextField(); txtUsername = new JTextField();
        txtPassword = new JTextField(); txtSalary = new JTextField(); txtHire = new JTextField();
        cmbRole = new JComboBox<>(new String[]{"CASHIER", "MANAGER", "ADMIN"});
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        for (JTextField f : new JTextField[]{txtFirst,txtLast,txtNic,txtPhone,txtEmail,txtUsername,txtPassword,txtSalary,txtHire})
            styleField(f);
        cmbRole.setBackground(UITheme.BG_DARK); cmbRole.setForeground(UITheme.TEXT_LIGHT);
        cmbStatus.setBackground(UITheme.BG_DARK); cmbStatus.setForeground(UITheme.TEXT_LIGHT);

        addCell(form, g, 0, 0, "First Name", txtFirst);
        addCell(form, g, 0, 1, "Last Name", txtLast);
        addCell(form, g, 0, 2, "NIC", txtNic);
        addCell(form, g, 1, 0, "Phone", txtPhone);
        addCell(form, g, 1, 1, "Email (optional)", txtEmail);
        addCell(form, g, 1, 2, "Role", cmbRole);
        addCell(form, g, 2, 0, "Username", txtUsername);
        addCell(form, g, 2, 1, "Password", txtPassword);
        addCell(form, g, 2, 2, "Status", cmbStatus);
        addCell(form, g, 3, 0, "Salary", txtSalary);
        addCell(form, g, 3, 1, "Hire Date (yyyy-MM-dd)", txtHire);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setOpaque(false);
        JButton bAdd = UITheme.primaryButton("Add");
        JButton bUpd = UITheme.secondaryButton("Update");
        JButton bDel = UITheme.secondaryButton("Delete");
        JButton bClr = UITheme.secondaryButton("Clear");
        btns.add(bAdd); btns.add(bUpd); btns.add(bDel); btns.add(bClr);
        bAdd.addActionListener(e -> add());
        bUpd.addActionListener(e -> update());
        bDel.addActionListener(e -> delete());
        bClr.addActionListener(e -> clearForm());
        g.gridx = 0; g.gridy = 4; g.gridwidth = 3; g.insets = new Insets(14, 8, 2, 8);
        form.add(btns, g);
        return form;
    }

    private void addCell(JPanel form, GridBagConstraints g, int row, int col, String lbl, JComponent comp) {
        JPanel cell = new JPanel(); cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(lbl); l.setFont(UITheme.FONT_LABEL); l.setForeground(UITheme.TEXT_LIGHT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT); comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cell.add(l); cell.add(Box.createVerticalStrut(4)); cell.add(comp);
        g.gridx = col; g.gridy = row; g.gridwidth = 1; form.add(cell, g);
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8)); p.setOpaque(false);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); top.setOpaque(false);
        JLabel l = new JLabel("Search:"); l.setForeground(UITheme.TEXT_LIGHT); l.setFont(UITheme.FONT_LABEL);
        txtSearch = new JTextField(22); styleField(txtSearch);
        JButton bGo = UITheme.secondaryButton("Go");
        JButton bAll = UITheme.secondaryButton("Show All");
        top.add(l); top.add(txtSearch); top.add(bGo); top.add(bAll);
        bGo.addActionListener(e -> loadTable(employeeDAO.search(txtSearch.getText().trim())));
        bAll.addActionListener(e -> { txtSearch.setText(""); loadTable(employeeDAO.getAll()); });
        txtSearch.addActionListener(e -> loadTable(employeeDAO.search(txtSearch.getText().trim())));

        String[] cols = {"ID", "Name", "Role", "Phone", "Username", "Salary", "Status"};
        model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model); styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting()) fillFromSelection(); });
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(UITheme.SURFACE);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.SURFACE_2));
        p.add(top, BorderLayout.NORTH); p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void loadTable(List<Employee> list) {
        currentList = list; model.setRowCount(0);
        for (Employee e : list)
            model.addRow(new Object[]{e.getEmployeeId(), e.getFullName(), e.getRole(),
                e.getPhone(), e.getUsername(), String.format("%,.2f", e.getSalary()), e.getStatus()});
    }

    private void fillFromSelection() {
        int r = table.getSelectedRow();
        if (r < 0 || currentList == null || r >= currentList.size()) return;
        Employee e = currentList.get(r);
        selectedId = e.getEmployeeId();
        txtFirst.setText(e.getFirstName()); txtLast.setText(e.getLastName());
        txtNic.setText(e.getNic()); txtPhone.setText(e.getPhone()); txtEmail.setText(e.getEmail());
        txtUsername.setText(e.getUsername()); txtPassword.setText(e.getPassword());
        txtSalary.setText(String.valueOf(e.getSalary()));
        txtHire.setText(e.getHireDate() != null ? e.getHireDate().toString() : "");
        cmbRole.setSelectedItem(e.getRole());
        cmbStatus.setSelectedItem(e.getStatus());
    }

    private Employee readForm() throws InvalidInputException {
        Validator.requireText(txtFirst.getText(), "First name");
        Validator.requireText(txtLast.getText(), "Last name");
        Validator.requireText(txtNic.getText(), "NIC");
        Validator.requireNIC(txtNic.getText());
        Validator.requirePhone(txtPhone.getText());
        Validator.requireEmail(txtEmail.getText());
        Validator.requireText(txtUsername.getText(), "Username");
        Validator.requireText(txtPassword.getText(), "Password");
        double salary = txtSalary.getText().trim().isEmpty() ? 0
                : Validator.requirePositiveDouble(txtSalary.getText(), "Salary");

        java.sql.Date hire;
        String hs = txtHire.getText().trim();
        if (hs.isEmpty()) {
            hire = new java.sql.Date(System.currentTimeMillis());
        } else {
            try { hire = java.sql.Date.valueOf(hs); }
            catch (IllegalArgumentException ex) { throw new InvalidInputException("Hire date must be yyyy-MM-dd (e.g. 2025-06-01)."); }
        }

        Employee e = new Employee();
        e.setFirstName(txtFirst.getText().trim()); e.setLastName(txtLast.getText().trim());
        e.setNic(txtNic.getText().trim()); e.setPhone(txtPhone.getText().trim());
        e.setEmail(txtEmail.getText().trim()); e.setRole((String) cmbRole.getSelectedItem());
        e.setUsername(txtUsername.getText().trim()); e.setPassword(txtPassword.getText().trim());
        e.setSalary(salary); e.setHireDate(hire); e.setStatus((String) cmbStatus.getSelectedItem());
        return e;
    }

    private void add() {
        try { Employee e = readForm();
            if (employeeDAO.insert(e)) { msg("Employee added."); refresh(); }
            else err("Could not add (username or NIC may already exist).");
        } catch (InvalidInputException ex) { warn(ex.getMessage()); }
    }
    private void update() {
        if (selectedId < 0) { warn("Select an employee first."); return; }
        try { Employee e = readForm(); e.setEmployeeId(selectedId);
            if (employeeDAO.update(e)) { msg("Employee updated."); refresh(); } else err("Could not update.");
        } catch (InvalidInputException ex) { warn(ex.getMessage()); }
    }
    private void delete() {
        if (selectedId < 0) { warn("Select an employee first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this employee?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (employeeDAO.delete(selectedId)) { msg("Employee deleted."); refresh(); } else err("Could not delete (linked to sales).");
        }
    }
    private void refresh() { clearForm(); loadTable(employeeDAO.getAll()); }
    private void clearForm() {
        selectedId = -1;
        for (JTextField f : new JTextField[]{txtFirst,txtLast,txtNic,txtPhone,txtEmail,txtUsername,txtPassword,txtSalary,txtHire}) f.setText("");
        cmbRole.setSelectedIndex(0); cmbStatus.setSelectedIndex(0); table.clearSelection();
    }

    private void styleField(JTextField f) {
        f.setPreferredSize(new Dimension(180, 32));
        f.setBackground(UITheme.BG_DARK); f.setForeground(UITheme.TEXT_LIGHT); f.setCaretColor(UITheme.AMBER);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.SURFACE_2, 1), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }
    private void styleTable(JTable t) {
        t.setBackground(UITheme.SURFACE); t.setForeground(UITheme.TEXT_LIGHT); t.setGridColor(UITheme.SURFACE_2);
        t.setRowHeight(26); t.setSelectionBackground(UITheme.AMBER_DARK); t.setSelectionForeground(UITheme.BG_DARK);
        t.setFont(UITheme.FONT_LABEL);
        JTableHeader hd = t.getTableHeader(); hd.setBackground(UITheme.SURFACE_2);
        hd.setForeground(UITheme.TEXT_LIGHT); hd.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
    private void msg(String m){ JOptionPane.showMessageDialog(this,m,"Success",JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m){ JOptionPane.showMessageDialog(this,m,"Validation",JOptionPane.WARNING_MESSAGE); }
    private void err(String m){ JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE); }
}