package com.tclelectrical.view;

import com.tclelectrical.dao.CategoryDAO;
import com.tclelectrical.dao.ProductDAO;
import com.tclelectrical.dao.SupplierDAO;
import com.tclelectrical.exception.InvalidInputException;
import com.tclelectrical.model.Category;
import com.tclelectrical.model.Product;
import com.tclelectrical.model.Supplier;
import com.tclelectrical.util.UITheme;
import com.tclelectrical.util.Validator;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ProductView extends JFrame {

    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final SupplierDAO supplierDAO = new SupplierDAO();

    private JTextField txtName, txtBrand, txtModel, txtSku, txtCost, txtPrice, txtQty, txtReorder, txtWarranty, txtSearch;
    private JComboBox<Category> cmbCategory;
    private JComboBox<Supplier> cmbSupplier;
    private JComboBox<String> cmbStatus;
    private JTable table;
    private DefaultTableModel model;
    private List<Product> currentList;
    private int selectedId = -1;

    public ProductView() {
        setTitle("TCL Electrical Shop - Products");
        setSize(1150, 730);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);

        loadCombos();
        loadTable(productDAO.getAll());
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.SURFACE);
        h.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Product Management");
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

        txtName = new JTextField(); txtBrand = new JTextField(); txtModel = new JTextField();
        txtSku = new JTextField(); txtCost = new JTextField(); txtPrice = new JTextField();
        txtQty = new JTextField(); txtReorder = new JTextField(); txtWarranty = new JTextField();
        cmbCategory = new JComboBox<>(); cmbSupplier = new JComboBox<>();
        cmbStatus = new JComboBox<>(new String[]{"ACTIVE", "DISCONTINUED"});
        for (JTextField f : new JTextField[]{txtName,txtBrand,txtModel,txtSku,txtCost,txtPrice,txtQty,txtReorder,txtWarranty})
            styleField(f);
        styleCombo(cmbCategory); styleCombo(cmbSupplier); styleCombo(cmbStatus);

        addCell(form, g, 0, 0, "Product Name", txtName);
        addCell(form, g, 0, 1, "Brand", txtBrand);
        addCell(form, g, 0, 2, "Model", txtModel);
        addCell(form, g, 1, 0, "Category", cmbCategory);
        addCell(form, g, 1, 1, "Supplier", cmbSupplier);
        addCell(form, g, 1, 2, "SKU", txtSku);
        addCell(form, g, 2, 0, "Cost Price", txtCost);
        addCell(form, g, 2, 1, "Selling Price", txtPrice);
        addCell(form, g, 2, 2, "Qty in Stock", txtQty);
        addCell(form, g, 3, 0, "Reorder Level", txtReorder);
        addCell(form, g, 3, 1, "Warranty (months)", txtWarranty);
        addCell(form, g, 3, 2, "Status", cmbStatus);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btns.setOpaque(false);
        JButton bAdd = UITheme.primaryButton("Add");
        JButton bUpd = UITheme.secondaryButton("Update");
        JButton bDel = UITheme.secondaryButton("Delete");
        JButton bClr = UITheme.secondaryButton("Clear");
        btns.add(bAdd); btns.add(bUpd); btns.add(bDel); btns.add(bClr);
        bAdd.addActionListener(e -> addProduct());
        bUpd.addActionListener(e -> updateProduct());
        bDel.addActionListener(e -> deleteProduct());
        bClr.addActionListener(e -> clearForm());

        g.gridx = 0; g.gridy = 4; g.gridwidth = 3; g.insets = new Insets(14, 8, 2, 8);
        form.add(btns, g);
        return form;
    }

    private void addCell(JPanel form, GridBagConstraints g, int rowIdx, int col, String lbl, JComponent comp) {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(lbl);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_LIGHT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        cell.add(l);
        cell.add(Box.createVerticalStrut(4));
        cell.add(comp);
        g.gridx = col; g.gridy = rowIdx; g.gridwidth = 1;
        form.add(cell, g);
    }

    private JPanel buildTablePanel() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);
        JLabel l = new JLabel("Search:");
        l.setForeground(UITheme.TEXT_LIGHT);
        l.setFont(UITheme.FONT_LABEL);
        txtSearch = new JTextField(22);
        styleField(txtSearch);
        JButton bSearch = UITheme.secondaryButton("Go");
        JButton bAll = UITheme.secondaryButton("Show All");
        top.add(l); top.add(txtSearch); top.add(bSearch); top.add(bAll);
        bSearch.addActionListener(e -> loadTable(productDAO.search(txtSearch.getText().trim())));
        bAll.addActionListener(e -> { txtSearch.setText(""); loadTable(productDAO.getAll()); });
        txtSearch.addActionListener(e -> loadTable(productDAO.search(txtSearch.getText().trim())));

        String[] cols = {"ID", "Name", "Category", "Brand", "Price (Rs.)", "Stock", "Supplier", "Status"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFromSelection();
        });

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(UITheme.SURFACE);
        sp.setBorder(BorderFactory.createLineBorder(UITheme.SURFACE_2));

        p.add(top, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void loadCombos() {
        cmbCategory.removeAllItems();
        for (Category c : categoryDAO.getAll()) cmbCategory.addItem(c);
        cmbSupplier.removeAllItems();
        for (Supplier s : supplierDAO.getAll()) cmbSupplier.addItem(s);
    }

    private void loadTable(List<Product> list) {
        currentList = list;
        model.setRowCount(0);
        for (Product p : list) {
            model.addRow(new Object[]{
                p.getProductId(), p.getProductName(), p.getCategoryName(), p.getBrand(),
                String.format("%,.2f", p.getSellingPrice()), p.getQtyInStock(),
                p.getSupplierName(), p.getStatus()
            });
        }
    }

    private void fillFromSelection() {
        int r = table.getSelectedRow();
        if (r < 0 || currentList == null || r >= currentList.size()) return;
        Product p = currentList.get(r);
        selectedId = p.getProductId();
        txtName.setText(p.getProductName());
        txtBrand.setText(p.getBrand());
        txtModel.setText(p.getModel());
        txtSku.setText(p.getSku());
        txtCost.setText(String.valueOf(p.getCostPrice()));
        txtPrice.setText(String.valueOf(p.getSellingPrice()));
        txtQty.setText(String.valueOf(p.getQtyInStock()));
        txtReorder.setText(String.valueOf(p.getReorderLevel()));
        txtWarranty.setText(String.valueOf(p.getWarrantyMonths()));
        selectCategory(p.getCategoryId());
        selectSupplier(p.getSupplierId());
        cmbStatus.setSelectedItem(p.getStatus());
    }

    private void selectCategory(int id) {
        for (int i = 0; i < cmbCategory.getItemCount(); i++)
            if (cmbCategory.getItemAt(i).getCategoryId() == id) { cmbCategory.setSelectedIndex(i); return; }
    }

    private void selectSupplier(int id) {
        for (int i = 0; i < cmbSupplier.getItemCount(); i++)
            if (cmbSupplier.getItemAt(i).getSupplierId() == id) { cmbSupplier.setSelectedIndex(i); return; }
    }

    private Product readForm() throws InvalidInputException {
        Validator.requireText(txtName.getText(), "Product name");
        if (cmbCategory.getSelectedItem() == null) throw new InvalidInputException("Select a category.");
        if (cmbSupplier.getSelectedItem() == null) throw new InvalidInputException("Select a supplier.");
        double cost = Validator.requirePositiveDouble(txtCost.getText(), "Cost price");
        double price = Validator.requirePositiveDouble(txtPrice.getText(), "Selling price");
        int qty = Validator.requireNonNegativeInt(txtQty.getText(), "Qty in stock");
        int reorder = Validator.requireNonNegativeInt(txtReorder.getText(), "Reorder level");
        int warranty = Validator.requireNonNegativeInt(txtWarranty.getText(), "Warranty months");

        Product p = new Product();
        p.setProductName(txtName.getText().trim());
        p.setBrand(txtBrand.getText().trim());
        p.setModel(txtModel.getText().trim());
        p.setSku(txtSku.getText().trim());
        p.setCategoryId(((Category) cmbCategory.getSelectedItem()).getCategoryId());
        p.setSupplierId(((Supplier) cmbSupplier.getSelectedItem()).getSupplierId());
        p.setCostPrice(cost);
        p.setSellingPrice(price);
        p.setQtyInStock(qty);
        p.setReorderLevel(reorder);
        p.setWarrantyMonths(warranty);
        p.setStatus((String) cmbStatus.getSelectedItem());
        return p;
    }

    private void addProduct() {
        try {
            Product p = readForm();
            if (productDAO.insert(p)) { msg("Product added."); refresh(); }
            else err("Could not add product (SKU may already exist).");
        } catch (InvalidInputException ex) { warn(ex.getMessage()); }
    }

    private void updateProduct() {
        if (selectedId < 0) { warn("Select a product from the table first."); return; }
        try {
            Product p = readForm();
            p.setProductId(selectedId);
            if (productDAO.update(p)) { msg("Product updated."); refresh(); }
            else err("Could not update product.");
        } catch (InvalidInputException ex) { warn(ex.getMessage()); }
    }

    private void deleteProduct() {
        if (selectedId < 0) { warn("Select a product from the table first."); return; }
        int c = JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm",
                JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            if (productDAO.delete(selectedId)) { msg("Product deleted."); refresh(); }
            else err("Could not delete (it may be linked to past sales).");
        }
    }

    private void refresh() { clearForm(); loadTable(productDAO.getAll()); }

    private void clearForm() {
        selectedId = -1;
        for (JTextField f : new JTextField[]{txtName,txtBrand,txtModel,txtSku,txtCost,txtPrice,txtQty,txtReorder,txtWarranty})
            f.setText("");
        if (cmbCategory.getItemCount() > 0) cmbCategory.setSelectedIndex(0);
        if (cmbSupplier.getItemCount() > 0) cmbSupplier.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        table.clearSelection();
    }

    // ---- styling + helpers ----
    private void styleField(JTextField f) {
        f.setPreferredSize(new Dimension(180, 32));
        f.setBackground(UITheme.BG_DARK);
        f.setForeground(UITheme.TEXT_LIGHT);
        f.setCaretColor(UITheme.AMBER);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.SURFACE_2, 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    private void styleCombo(JComboBox<?> c) {
        c.setBackground(UITheme.BG_DARK);
        c.setForeground(UITheme.TEXT_LIGHT);
    }

    private void styleTable(JTable t) {
        t.setBackground(UITheme.SURFACE);
        t.setForeground(UITheme.TEXT_LIGHT);
        t.setGridColor(UITheme.SURFACE_2);
        t.setRowHeight(26);
        t.setSelectionBackground(UITheme.AMBER_DARK);
        t.setSelectionForeground(UITheme.BG_DARK);
        t.setFont(UITheme.FONT_LABEL);
        JTableHeader hd = t.getTableHeader();
        hd.setBackground(UITheme.SURFACE_2);
        hd.setForeground(UITheme.TEXT_LIGHT);
        hd.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private void msg(String m)  { JOptionPane.showMessageDialog(this, m, "Success", JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m) { JOptionPane.showMessageDialog(this, m, "Validation", JOptionPane.WARNING_MESSAGE); }
    private void err(String m)  { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}