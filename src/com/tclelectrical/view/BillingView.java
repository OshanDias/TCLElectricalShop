package com.tclelectrical.view;

import com.tclelectrical.dao.CustomerDAO;
import com.tclelectrical.dao.ProductDAO;
import com.tclelectrical.dao.SaleDAO;
import com.tclelectrical.exception.InsufficientStockException;
import com.tclelectrical.model.Customer;
import com.tclelectrical.model.Product;
import com.tclelectrical.model.Sale;
import com.tclelectrical.model.SaleItem;
import com.tclelectrical.util.SessionManager;
import com.tclelectrical.util.UITheme;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class BillingView extends JFrame {

    private final ProductDAO productDAO = new ProductDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final SaleDAO saleDAO = new SaleDAO();

    private JComboBox<Customer> cmbCustomer;
    private JComboBox<Product> cmbProduct;
    private JComboBox<String> cmbPayment;
    private JTextField txtQty, txtDiscount, txtTax, txtPaid;
    private JTextField txtProductSearch;
    private List<Product> allActiveProducts = new ArrayList<>();
    private JLabel lblInvoice, lblSubtotal, lblTotal, lblChange;
    private JTable table;
    private DefaultTableModel model;

    private final List<SaleItem> cart = new ArrayList<>();

    public BillingView() {
        setTitle("TCL Electrical Shop - Billing / POS");
        setSize(1150, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildControls(), BorderLayout.WEST);
        add(buildCart(), BorderLayout.CENTER);
        add(buildTotals(), BorderLayout.SOUTH);

        loadCombos();
        newInvoiceNo();
    }

    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(UITheme.SURFACE);
        h.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("Billing / Point of Sale");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(UITheme.AMBER);

        lblInvoice = new JLabel("Invoice: -");
        lblInvoice.setFont(UITheme.FONT_LABEL);
        lblInvoice.setForeground(UITheme.TEXT_LIGHT);

        JButton back = UITheme.secondaryButton("Back");
        back.addActionListener(e -> dispose());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        right.setOpaque(false);
        right.add(lblInvoice);
        right.add(back);

        h.add(title, BorderLayout.WEST);
        h.add(right, BorderLayout.EAST);
        return h;
    }

    private JPanel buildControls() {
        JPanel p = new JPanel();
        p.setBackground(UITheme.SURFACE);
        p.setPreferredSize(new Dimension(320, 0));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(sectionLabel("Customer"));
        cmbCustomer = new JComboBox<>(); styleCombo(cmbCustomer);
        p.add(cmbCustomer);
        p.add(Box.createVerticalStrut(16));

        p.add(sectionLabel("Product"));
        txtProductSearch = new JTextField(); styleField(txtProductSearch);
        txtProductSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        txtProductSearch.getDocument().addDocumentListener(new DocumentListener() {
         public void insertUpdate(DocumentEvent e){ filterProducts(); }
         public void removeUpdate(DocumentEvent e){ filterProducts(); }
         public void changedUpdate(DocumentEvent e){ filterProducts(); }
         
        });
        p.add(txtProductSearch);
        p.add(Box.createVerticalStrut(6));
        cmbProduct = new JComboBox<>(); styleCombo(cmbProduct);
        p.add(cmbProduct);
        p.add(Box.createVerticalStrut(10));

        p.add(sectionLabel("Quantity"));
        txtQty = new JTextField("1"); styleField(txtQty);
        txtQty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        p.add(txtQty);
        p.add(Box.createVerticalStrut(14));

        JButton bAdd = UITheme.primaryButton("Add to Cart");
        bAdd.setAlignmentX(Component.LEFT_ALIGNMENT);
        bAdd.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        bAdd.addActionListener(e -> addToCart());
        p.add(bAdd);
        p.add(Box.createVerticalStrut(8));

        JButton bRemove = UITheme.secondaryButton("Remove Selected");
        bRemove.setAlignmentX(Component.LEFT_ALIGNMENT);
        bRemove.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        bRemove.addActionListener(e -> removeSelected());
        p.add(bRemove);
        p.add(Box.createVerticalStrut(20));

        p.add(sectionLabel("Payment Method"));
        cmbPayment = new JComboBox<>(new String[]{"CASH", "CARD", "MOBILE"});
        styleCombo(cmbPayment);
        p.add(cmbPayment);

        return p;
    }

    private JScrollPane buildCart() {
        String[] cols = {"Product", "Unit Price", "Qty", "Line Total"};
        model = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(model);
        table.setBackground(UITheme.SURFACE); table.setForeground(UITheme.TEXT_LIGHT);
        table.setGridColor(UITheme.SURFACE_2); table.setRowHeight(28);
        table.setSelectionBackground(UITheme.AMBER_DARK); table.setSelectionForeground(UITheme.BG_DARK);
        table.setFont(UITheme.FONT_LABEL);
        JTableHeader hd = table.getTableHeader();
        hd.setBackground(UITheme.SURFACE_2); hd.setForeground(UITheme.TEXT_LIGHT);
        hd.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JScrollPane sp = new JScrollPane(table);
        sp.getViewport().setBackground(UITheme.SURFACE);
        sp.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        return sp;
    }

    private JPanel buildTotals() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.SURFACE);
        p.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 10, 5, 10);
        g.anchor = GridBagConstraints.WEST;

        lblSubtotal = valueLabel("Rs. 0.00");
        lblTotal = valueLabel("Rs. 0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(UITheme.AMBER);
        lblChange = valueLabel("Rs. 0.00");

        txtDiscount = new JTextField("0"); styleField(txtDiscount); txtDiscount.setColumns(8);
        txtTax = new JTextField("0"); styleField(txtTax); txtTax.setColumns(8);
        txtPaid = new JTextField("0"); styleField(txtPaid); txtPaid.setColumns(8);
        DocumentListener dl = new DocumentListener() {
            public void insertUpdate(DocumentEvent e){ recalc(); }
            public void removeUpdate(DocumentEvent e){ recalc(); }
            public void changedUpdate(DocumentEvent e){ recalc(); }
        };
        txtDiscount.getDocument().addDocumentListener(dl);
        txtTax.getDocument().addDocumentListener(dl);
        txtPaid.getDocument().addDocumentListener(dl);

        // row 0
        g.gridy = 0;
        g.gridx = 0; p.add(keyLabel("Subtotal:"), g);
        g.gridx = 1; p.add(lblSubtotal, g);
        g.gridx = 2; p.add(keyLabel("Discount (Rs.):"), g);
        g.gridx = 3; p.add(txtDiscount, g);
        g.gridx = 4; p.add(keyLabel("Tax (Rs.):"), g);
        g.gridx = 5; p.add(txtTax, g);

        // row 1
        g.gridy = 1;
        g.gridx = 0; p.add(keyLabel("TOTAL:"), g);
        g.gridx = 1; p.add(lblTotal, g);
        g.gridx = 2; p.add(keyLabel("Amount Paid:"), g);
        g.gridx = 3; p.add(txtPaid, g);
        g.gridx = 4; p.add(keyLabel("Change:"), g);
        g.gridx = 5; p.add(lblChange, g);

        // buttons
        JButton bComplete = UITheme.primaryButton("Complete Sale");
        bComplete.addActionListener(e -> completeSale());
        JButton bNew = UITheme.secondaryButton("New Sale");
        bNew.addActionListener(e -> resetSale());

        g.gridy = 2; g.insets = new Insets(14, 10, 2, 10);
        g.gridx = 0; g.gridwidth = 3; p.add(bNew, g);
        g.gridx = 3; g.gridwidth = 3; p.add(bComplete, g);

        return p;
    }

    // ---- data ----
       private void loadCombos() {
        cmbCustomer.removeAllItems();
        for (Customer c : customerDAO.getAll()) cmbCustomer.addItem(c);
        allActiveProducts = productDAO.getActive();
        filterProducts();
    }
       
           private void filterProducts() {
        String kw = txtProductSearch.getText().trim().toLowerCase();
        cmbProduct.removeAllItems();
        for (Product p : allActiveProducts) {
            if (kw.isEmpty()
                || p.getProductName().toLowerCase().contains(kw)
                || (p.getBrand() != null && p.getBrand().toLowerCase().contains(kw))
                || (p.getSku() != null && p.getSku().toLowerCase().contains(kw))) {
                cmbProduct.addItem(p);
            }
        }
    }

    private void newInvoiceNo() {
        lblInvoice.setText("Invoice: " + saleDAO.generateInvoiceNo());
    }

    private void addToCart() {
        Product p = (Product) cmbProduct.getSelectedItem();
        if (p == null) { warn("No product selected."); return; }
        int qty;
        try { qty = Integer.parseInt(txtQty.getText().trim()); }
        catch (NumberFormatException e) { warn("Quantity must be a whole number."); return; }
        if (qty <= 0) { warn("Quantity must be at least 1."); return; }
        if (qty > p.getQtyInStock()) { warn("Only " + p.getQtyInStock() + " in stock for " + p.getProductName() + "."); return; }

        // if already in cart, add to its quantity
        for (SaleItem it : cart) {
            if (it.getProductId() == p.getProductId()) {
                int newQty = it.getQuantity() + qty;
                if (newQty > p.getQtyInStock()) { warn("Not enough stock. Available: " + p.getQtyInStock()); return; }
                it.setQuantity(newQty);
                it.setLineTotal(newQty * it.getUnitPrice());
                refreshCart(); return;
            }
        }
        cart.add(new SaleItem(p.getProductId(), p.getProductName(), qty, p.getSellingPrice()));
        refreshCart();
    }

    private void removeSelected() {
        int r = table.getSelectedRow();
        if (r < 0) { warn("Select a cart row to remove."); return; }
        cart.remove(r);
        refreshCart();
    }

    private void refreshCart() {
        model.setRowCount(0);
        for (SaleItem it : cart)
            model.addRow(new Object[]{ it.getProductName(),
                String.format("%,.2f", it.getUnitPrice()), it.getQuantity(),
                String.format("%,.2f", it.getLineTotal()) });
        recalc();
    }

    private void recalc() {
        double subtotal = 0;
        for (SaleItem it : cart) subtotal += it.getLineTotal();
        double discount = parseOrZero(txtDiscount.getText());
        double tax = parseOrZero(txtTax.getText());
        double total = subtotal - discount + tax;
        if (total < 0) total = 0;
        double paid = parseOrZero(txtPaid.getText());
        double change = paid - total;

        lblSubtotal.setText(String.format("Rs. %,.2f", subtotal));
        lblTotal.setText(String.format("Rs. %,.2f", total));
        lblChange.setText(String.format("Rs. %,.2f", change));
    }

    private void completeSale() {
        if (cart.isEmpty()) { warn("Cart is empty. Add at least one product."); return; }

        double subtotal = 0;
        for (SaleItem it : cart) subtotal += it.getLineTotal();
        double discount = parseOrZero(txtDiscount.getText());
        double tax = parseOrZero(txtTax.getText());
        double total = subtotal - discount + tax;
        if (total < 0) total = 0;
        double paid = parseOrZero(txtPaid.getText());
        if (paid < total) { warn("Amount paid is less than the total."); return; }

        Customer cust = (Customer) cmbCustomer.getSelectedItem();

        Sale sale = new Sale();
        sale.setInvoiceNo(lblInvoice.getText().replace("Invoice: ", "").trim());
        sale.setCustomerId(cust != null ? cust.getCustomerId() : 0);
        sale.setEmployeeId(SessionManager.getCurrentUser().getEmployeeId());
        sale.setSubtotal(subtotal);
        sale.setDiscount(discount);
        sale.setTax(tax);
        sale.setTotalAmount(total);
        sale.setPaymentMethod((String) cmbPayment.getSelectedItem());
        sale.setAmountPaid(paid);
        sale.setChangeDue(paid - total);
        sale.setItems(cart);

        try {
            int saleId = saleDAO.createSale(sale);
            JOptionPane.showMessageDialog(this,
                "Sale completed!\n\nInvoice: " + sale.getInvoiceNo()
                + "\nItems: " + cart.size()
                + "\nTotal: Rs. " + String.format("%,.2f", total)
                + "\nPaid: Rs. " + String.format("%,.2f", paid)
                + "\nChange: Rs. " + String.format("%,.2f", (paid - total)),
                "Success (Sale #" + saleId + ")", JOptionPane.INFORMATION_MESSAGE);
            resetSale();
        } catch (InsufficientStockException ex) {   // <-- custom exception caught
            warn(ex.getMessage());
        } catch (SQLException ex) {
            err("Database error: " + ex.getMessage());
        }
    }

    private void resetSale() {
        cart.clear();
        refreshCart();
        txtDiscount.setText("0"); txtTax.setText("0"); txtPaid.setText("0");
        txtQty.setText("1");
        loadCombos();        // reload products so stock numbers are fresh
        newInvoiceNo();
    }

    // ---- helpers ----
    private double parseOrZero(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; }
    }

    private JLabel sectionLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return l;
    }
    private JLabel keyLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(UITheme.FONT_LABEL);
        l.setForeground(UITheme.TEXT_LIGHT);
        return l;
    }
    private JLabel valueLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setForeground(UITheme.TEXT_LIGHT);
        return l;
    }
    private void styleField(JTextField f) {
        f.setBackground(UITheme.BG_DARK); f.setForeground(UITheme.TEXT_LIGHT); f.setCaretColor(UITheme.AMBER);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.SURFACE_2, 1), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }
    private void styleCombo(JComboBox<?> c) {
        c.setBackground(UITheme.BG_DARK); c.setForeground(UITheme.TEXT_LIGHT);
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
    }
    private void warn(String m){ JOptionPane.showMessageDialog(this,m,"Notice",JOptionPane.WARNING_MESSAGE); }
    private void err(String m){ JOptionPane.showMessageDialog(this,m,"Error",JOptionPane.ERROR_MESSAGE); }
}