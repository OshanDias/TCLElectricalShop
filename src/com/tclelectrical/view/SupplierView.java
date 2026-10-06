package com.tclelectrical.view;

import com.tclelectrical.dao.SupplierDAO;
import com.tclelectrical.exception.InvalidInputException;
import com.tclelectrical.model.Supplier;
import com.tclelectrical.util.UITheme;
import com.tclelectrical.util.Validator;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class SupplierView extends JFrame {

    private final SupplierDAO supplierDAO = new SupplierDAO();
    private JTextField txtCompany, txtContact, txtPhone, txtEmail, txtCity, txtAddress, txtSearch;
    private JComboBox<String> cmbStatus;
    private JTable table;
    private DefaultTableModel model;
    private List<Supplier> currentList;
    private int selectedId = -1;

    public SupplierView() {
        setTitle("TCL Electrical Shop - Suppliers");
        setSize(1100, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        loadTable(supplierDAO.getAll());
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.SURFACE);
        h.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Supplier Management");
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

        txtCompany = new JTextField(); txtContact = new JTextField(); txtPhone = new JTextField();
        txtEmail = new JTextField(); txtCity = new JTextField(); txtAddress = new JTextField();
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        for (JTextField f : new JTextField[]{txtCompany,txtContact,txtPhone,txtEmail,txtCity,txtAddress}) styleField(f);
        cmbStatus.setBackground(UITheme.BG_DARK); cmbStatus.setForeground(UITheme.TEXT_LIGHT);

        addCell(form, g, 0, 0, "Company Name", txtCompany);
        addCell(form, g, 0, 1, "Contact Person", txtContact);
        addCell(form, g, 0, 2, "Phone", txtPhone);
        addCell(form, g, 1, 0, "Email (optional)", txtEmail);
        addCell(form, g, 1, 1, "City", txtCity);
        addCell(form, g, 1, 2, "Status", cmbStatus);
        addCell(form, g, 2, 0, "Address", txtAddress);

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
        bGo.addActionListener(e -> loadTable(supplierDAO.search(txtSearch.getText().trim())));
        bAll.addActionListener(e -> { txtSearch.setText(""); loadTable(supplierDAO.getAll()); });
        txtSearch.addActionListener(e -> loadTable(supplierDAO.search(txtSearch.getText().trim())));

        String[] cols = {"ID", "Company", "Contact", "Phone", "City", "Status"};
        model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model); styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> { if(!e.getValueIsAdjusting()) fillFromSelection(); });
        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(UITheme.SURFACE);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.SURFACE_2));
        p.add(top, BorderLayout.NORTH); p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void loadTable(List<Supplier> list) {
        currentList = list; model.setRowCount(0);
        for (Supplier s : list)
            model.addRow(new Object[]{s.getSupplierId(), s.getCompanyName(), s.getContactPerson(),
                s.getPhone(), s.getCity(), s.getStatus()});
    }

    private void fillFromSelection() {
        int r = table.getSelectedRow();
        if (r < 0 || currentList == null || r >= currentList.size()) return;
        Supplier s = currentList.get(r);
        selectedId = s.getSupplierId();
        txtCompany.setText(s.getCompanyName()); txtContact.setText(s.getContactPerson());
        txtPhone.setText(s.getPhone()); txtEmail.setText(s.getEmail());
        txtCity.setText(s.getCity()); txtAddress.setText(s.getAddress());
        cmbStatus.setSelectedItem(s.getStatus());
    }

    private Supplier readForm() throws InvalidInputException {
        Validator.requireText(txtCompany.getText(), "Company name");
        Validator.requirePhone(txtPhone.getText());
        Validator.requireEmail(txtEmail.getText());
        Supplier s = new Supplier();
        s.setCompanyName(txtCompany.getText().trim()); s.setContactPerson(txtContact.getText().trim());
        s.setPhone(txtPhone.getText().trim()); s.setEmail(txtEmail.getText().trim());
        s.setCity(txtCity.getText().trim()); s.setAddress(txtAddress.getText().trim());
        s.setStatus((String) cmbStatus.getSelectedItem());
        return s;
    }

    private void add() {
        try { Supplier s = readForm();
            if (supplierDAO.insert(s)) { msg("Supplier added."); refresh(); } else err("Could not add supplier.");
        } catch (InvalidInputException ex) { warn(ex.getMessage()); }
    }
    private void update() {
        if (selectedId < 0) { warn("Select a supplier first."); return; }
        try { Supplier s = readForm(); s.setSupplierId(selectedId);
            if (supplierDAO.update(s)) { msg("Supplier updated."); refresh(); } else err("Could not update.");
        } catch (InvalidInputException ex) { warn(ex.getMessage()); }
    }
    private void delete() {
        if (selectedId < 0) { warn("Select a supplier first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this supplier?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (supplierDAO.delete(selectedId)) { msg("Supplier deleted."); refresh(); } else err("Could not delete (linked to products).");
        }
    }
    private void refresh() { clearForm(); loadTable(supplierDAO.getAll()); }
    private void clearForm() {
        selectedId = -1;
        for (JTextField f : new JTextField[]{txtCompany,txtContact,txtPhone,txtEmail,txtCity,txtAddress}) f.setText("");
        cmbStatus.setSelectedIndex(0); table.clearSelection();
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