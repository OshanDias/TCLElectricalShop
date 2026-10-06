package com.tclelectrical.view;

import com.tclelectrical.controller.ReportController;
import com.tclelectrical.dao.ProductDAO;
import com.tclelectrical.dao.SaleDAO;
import com.tclelectrical.model.Employee;
import com.tclelectrical.util.SessionManager;
import com.tclelectrical.util.UITheme;
import java.awt.*;
import javax.swing.*;

public class DashboardView extends JFrame {

    private final ProductDAO productDAO = new ProductDAO();
    private final SaleDAO saleDAO = new SaleDAO();

    public DashboardView() {
        setTitle("TCL Electrical Shop - Dashboard");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_DARK);
        setLayout(new BorderLayout());

        add(buildNavbar(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildNavbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.SURFACE);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 24, 14, 24));

        JLabel brand = new JLabel("\u26A1 TCL ELECTRICAL SHOP");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(UITheme.AMBER);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);

        Employee user = SessionManager.getCurrentUser();
        String who = (user != null) ? user.getFullName() + "  (" + user.getRole() + ")" : "Guest";
        JLabel lblUser = new JLabel(who);
        lblUser.setFont(UITheme.FONT_LABEL);
        lblUser.setForeground(UITheme.TEXT_LIGHT);

        JButton btnLogout = UITheme.secondaryButton("Logout");
        btnLogout.addActionListener(e -> {
            SessionManager.logout();
            dispose();
            new LoginView().setVisible(true);
        });

        right.add(lblUser);
        right.add(btnLogout);

        bar.add(brand, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildContent() {
        JPanel content = new JPanel();
        content.setBackground(UITheme.BG_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        Employee user = SessionManager.getCurrentUser();
        JLabel welcome = new JLabel("Welcome back, " + (user != null ? user.getFirstName() : "") + " !");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setForeground(UITheme.TEXT_LIGHT);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Here's what's happening in your store today");
        subtitle.setFont(UITheme.FONT_LABEL);
        subtitle.setForeground(UITheme.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel cards = new JPanel(new GridLayout(1, 4, 18, 0));
        cards.setOpaque(false);
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        int todaySales = saleDAO.getTodaySalesCount();
        double todayRev = saleDAO.getTodayRevenue();
        int lowStock = productDAO.getLowStock().size();
        int totalProducts = productDAO.countAll();

        cards.add(kpiCard("Today's Sales", String.valueOf(todaySales), UITheme.AMBER));
        cards.add(kpiCard("Today's Revenue", String.format("Rs. %,.2f", todayRev), UITheme.SUCCESS));
        cards.add(kpiCard("Low Stock Items", String.valueOf(lowStock), UITheme.DANGER));
        cards.add(kpiCard("Total Products", String.valueOf(totalProducts), UITheme.TEXT_LIGHT));

        JLabel actionsTitle = new JLabel("Quick Actions");
        actionsTitle.setFont(UITheme.FONT_HEADER);
        actionsTitle.setForeground(UITheme.TEXT_LIGHT);
        actionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel actions = new JPanel(new GridLayout(2, 3, 18, 18));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        actions.add(navButton("New Sale / Billing", "BILLING"));
        actions.add(navButton("Products", "PRODUCTS"));
        actions.add(navButton("Customers", "CUSTOMERS"));
        actions.add(navButton("Employees", "EMPLOYEES"));
        actions.add(navButton("Suppliers", "SUPPLIERS"));
        actions.add(navButton("Reports", "REPORTS"));

        content.add(welcome);
        content.add(Box.createVerticalStrut(4));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(22));
        content.add(cards);
        content.add(Box.createVerticalStrut(30));
        content.add(actionsTitle);
        content.add(Box.createVerticalStrut(14));
        content.add(actions);

        return content;
    }

    private JPanel kpiCard(String label, String value, Color accent) {
        JPanel card = new JPanel();
        card.setBackground(UITheme.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 4, 0, 0, accent),
            BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(accent);
        lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(UITheme.FONT_LABEL);
        lblLabel.setForeground(UITheme.TEXT_MUTED);
        lblLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(lblValue);
        card.add(Box.createVerticalStrut(8));
        card.add(lblLabel);
        return card;
    }

    private JButton navButton(String text, String module) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 15));
        b.setForeground(UITheme.TEXT_LIGHT);
        b.setBackground(UITheme.SURFACE);
        b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.SURFACE_2, 1),
            BorderFactory.createEmptyBorder(26, 20, 26, 20)));
        b.addActionListener(e -> openModule(module));
        return b;
    }

    private void openModule(String module) {
        // We flip these on one by one as we build each screen.
        switch (module) {
               case "BILLING":   new BillingView().setVisible(true); break;
               case "PRODUCTS":  new ProductView().setVisible(true); break;
               case "CUSTOMERS": new CustomerView().setVisible(true); break;
               case "EMPLOYEES": new EmployeeView().setVisible(true); break;
               case "SUPPLIERS": new SupplierView().setVisible(true); break;
               case "REPORTS":   new ReportController().showSalesReport(); break;
            default:
                JOptionPane.showMessageDialog(this,
                    module + " screen coming next!", "Coming Soon",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardView().setVisible(true));
    }
}