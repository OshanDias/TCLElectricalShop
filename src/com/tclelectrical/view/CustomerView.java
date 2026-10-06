package com.tclelectrical.view;

import com.tclelectrical.dao.CustomerDAO;
import com.tclelectrical.exception.InvalidInputException;
import com.tclelectrical.model.Customer;
import com.tclelectrical.util.UITheme;
import com.tclelectrical.util.Validator;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class CustomerView extends JFrame {

    private final CustomerDAO customerDAO = new CustomerDAO();
    private JTextField txtFirst, txtLast, txtPhone, txtEmail, txtNic, txtCity, txtAddress, txtLoyalty, txtSearch;
    private JTable table;
    private DefaultTableModel model;
    private List<Customer> currentList;
    private int selectedId = -1;

    public CustomerView() {
        setTitle("TCL Electrical Shop - Customers");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        loadTable(customerDAO.getAll());
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.SURFACE);
        h.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Customer Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(UITheme.AMBER);
        JButton back = UITheme.secondaryButton("Back");
        back.addActionListener(e -> dispose());
        h.add(title, BorderLayout.WEST);
        h.add(back, BorderLayout.EAST);
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
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;

        txtFirst = new JTextField(); txtLast = new JTextField(); txtPhone = new JTextField();
        txtEmail = new JTextField(); txtNic = new JTextField(); txtCity = new JTextField();
        txtAddress = new JTextField(); txtLoyalty = new JTextField();
        for (JTextField f : new JTextField[]{txtFirst,txtLast,txtPhone,txtEmail,txtNic,txtCity,txtAddress,txtLoyalty})
            styleField(f);

        addCell(form, g, 0, 0, "First Name", txtFirst);
        addCell(form, g, 0, 1, "Last Name", txtLast);
        addCell(form, g, 0, 2, "Phone", txtPhone);
        addCell(form, g, 1, 0, "Email (optional)", txtEmail);
        addCell(form, g, 1, 1, "NIC (optional)", txtNic);
        addCell(form, g, 1, 2, "City", txtCity);
        addCell(form, g, 2, 0, "Address", txtAddress);
        addCell(form, g, 2, 1, "Loyalty Points", txtLoyalty);

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
        g.gridx = 0; g.gridy = 3; g.gridwidth = 3; g.insets = new Insets(14, 8, 2, 8);
        form.add(btns, g);
        return form;
    }

    private void addCell(JPanel form, GridBagConstraints g, int row, int col, String lbl, JComponent comp) {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(lbl);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_LIGHT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cell.add(l); cell.add(Box.createVerticalStrut(4)); cell.add(comp);
        g.gridx = col; g.gridy = row; g.gridwidth = 1;
        form.add(cell, g);
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        JLabel l = new JLabel("Search:"); l.setForeground(UITheme.TEXT_LIGHT); l.setFont(UITheme.FONT_LABEL);
        txtSearch = new JTextField(22); styleField(txtSearch);
        JButton bGo = UITheme.secondaryButton("Go");
        JButton bAll = UITheme.secondaryButton("Show All");
        top.add(l); top.add(txtSearch); top.add(bGo); top.add(bAll);
        bGo.addActionListener(e -> loadTable(customerDAO.search(txtSearch.getText().trim())));
        bAll.addActionListener(e -> { txtSearch.setText(""); loadTable(customerDAO.getAll()); });
        txtSearch.addActionListener(e -> loadTable(customerDAO.search(txtSearch.getText().trim())));

        String[] cols = {"ID", "Name", "Phone", "Email", "City", "Loyalty"};
        model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model); styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting()) fillFromSelection(); });
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(UITheme.SURFACE);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.SURFACE_2));
        p.add(top, BorderLayout.NORTH); p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void loadTable(List<Customer> list) {
        currentList = list; model.setRowCount(0);
        for (Customer c : list)
            model.addRow(new Object[]{c.getCustomerId(), c.getFullName(), c.getPhone(),
                c.getEmail(), c.getCity(), c.getLoyaltyPoints()});
    }

    private void fillFromSelection() {
        int r = table.getSelectedRow();
        if (r < 0 || currentList == null || r >= currentList.size()) return;
        Customer c = currentList.get(r);
        selectedId = c.getCustomerId();
        txtFirst.setText(c.getFirstName()); txtLast.setText(c.getLastName());
        txtPhone.setText(c.getPhone()); txtEmail.setText(c.getEmail());
        txtNic.setText(c.getNic()); txtCity.setText(c.getCity());
        txtAddress.setText(c.getAddress()); txtLoyalty.setText(String.valueOf(c.getLoyaltyPoints()));
    }

    private Customer readForm() throws InvalidInputException {
        Validator.requireText(txtFirst.getText(), "First name");
        Validator.requireText(txtLast.getText(), "Last name");
        Validator.requirePhone(txtPhone.getText());
        Validator.requireEmail(txtEmail.getText());
        Validator.requireNIC(txtNic.getText());
        int loyalty = txtLoyalty.getText().trim().isEmpty() ? 0
                : Validator.requireNonNegativeInt(txtLoyalty.getText(), "Loyalty points");
        Customer c = new Customer();
        c.setFirstName(txtFirst.getText().trim()); c.setLastName(txtLast.getText().trim());
        c.setPhone(txtPhone.getText().trim()); c.setEmail(txtEmail.getText().trim());
        c.setNic(txtNic.getText().trim()); c.setCity(txtCity.getText().trim());
        c.setAddress(txtAddress.getText().trim()); c.setLoyaltyPoints(loyalty);
        return c;
    }

    private void add() {
        try { Customer c = readForm();
            if (customerDAO.insert(c)) { msg("Customer added."); refresh(); } else err("Could not add customer.");
        } catch (InvalidInputException ex) { warn(ex.getMessage()); }
    }
    private void update() {
        if (selectedId < 0) { warn("Select a customer first."); return; }
        try { Customer c = readForm(); c.setCustomerId(selectedId);
            if (customerDAO.update(c)) { msg("Customer updated."); refresh(); } else err("Could not update.");
        } catch (InvalidInputException ex) { warn(ex.getMessage()); }
    }
    private void delete() {
        if (selectedId < 0) { warn("Select a customer first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this customer?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (customerDAO.delete(selectedId)) { msg("Customer deleted."); refresh(); } else err("Could not delete (linked to sales).");
        }
    }
    private void refresh() { clearForm(); loadTable(customerDAO.getAll()); }
    private void clearForm() {
        selectedId = -1;
        for (JTextField f : new JTextField[]{txtFirst,txtLast,txtPhone,txtEmail,txtNic,txtCity,txtAddress,txtLoyalty}) f.setText("");
        table.clearSelection();
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