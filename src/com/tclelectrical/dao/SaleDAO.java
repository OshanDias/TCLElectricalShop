package com.tclelectrical.dao;

import com.tclelectrical.exception.InsufficientStockException;
import com.tclelectrical.model.Sale;
import com.tclelectrical.model.SaleItem;
import java.sql.*;

public class SaleDAO {
    private final Connection con = DBConnection.getInstance().getConnection();

    /**
     * Saves a complete sale (header + items) in ONE transaction and deducts stock.
     * @return the generated sale_id
     * @throws InsufficientStockException if any item exceeds available stock (custom exception)
     */
    public int createSale(Sale sale) throws InsufficientStockException, SQLException {
        try {
            con.setAutoCommit(false);

            // 1) verify stock for every item FIRST (atomic check)
            for (SaleItem it : sale.getItems()) {
                int available = getStock(it.getProductId());
                if (it.getQuantity() > available) {
                    con.rollback();
                    throw new InsufficientStockException(it.getProductName(), available, it.getQuantity());
                }
            }

            // 2) insert sale header, grab generated id
            String sh = "INSERT INTO sales (invoice_no,customer_id,employee_id,subtotal,discount,tax,total_amount,payment_method,amount_paid,change_due) VALUES (?,?,?,?,?,?,?,?,?,?)";
            int saleId;
            try (PreparedStatement ps = con.prepareStatement(sh, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, sale.getInvoiceNo());
                if (sale.getCustomerId() > 0) ps.setInt(2, sale.getCustomerId());
                else ps.setNull(2, Types.INTEGER);
                ps.setInt(3, sale.getEmployeeId());
                ps.setDouble(4, sale.getSubtotal());
                ps.setDouble(5, sale.getDiscount());
                ps.setDouble(6, sale.getTax());
                ps.setDouble(7, sale.getTotalAmount());
                ps.setString(8, sale.getPaymentMethod());
                ps.setDouble(9, sale.getAmountPaid());
                ps.setDouble(10, sale.getChangeDue());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    saleId = keys.getInt(1);
                }
            }

            // 3) insert each line item + deduct its stock
            String si = "INSERT INTO sale_items (sale_id,product_id,quantity,unit_price,line_total) VALUES (?,?,?,?,?)";
            String us = "UPDATE products SET qty_in_stock = qty_in_stock - ? WHERE product_id = ?";
            try (PreparedStatement psItem = con.prepareStatement(si);
                 PreparedStatement psStock = con.prepareStatement(us)) {
                for (SaleItem it : sale.getItems()) {
                    psItem.setInt(1, saleId);
                    psItem.setInt(2, it.getProductId());
                    psItem.setInt(3, it.getQuantity());
                    psItem.setDouble(4, it.getUnitPrice());
                    psItem.setDouble(5, it.getLineTotal());
                    psItem.addBatch();

                    psStock.setInt(1, it.getQuantity());
                    psStock.setInt(2, it.getProductId());
                    psStock.addBatch();
                }
                psItem.executeBatch();
                psStock.executeBatch();
            }

            con.commit();
            return saleId;

        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
        }
    }

    private int getStock(int productId) throws SQLException {
        String sql = "SELECT qty_in_stock FROM products WHERE product_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    /** Builds next invoice number e.g. INV-20261006-0006 */
    public String generateInvoiceNo() {
        String prefix = "INV-" + new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + "-";
        int n = 1;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*)+1 AS n FROM sales")) {
            if (rs.next()) n = rs.getInt("n");
        } catch (SQLException e) { e.printStackTrace(); }
        return prefix + String.format("%04d", n);
    }

    // ---- dashboard metrics ----
    public double getTodayRevenue() {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(total_amount),0) FROM sales WHERE DATE(sale_date)=CURDATE()")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int getTodaySalesCount() {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM sales WHERE DATE(sale_date)=CURDATE()")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public double getTotalRevenue() {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COALESCE(SUM(total_amount),0) FROM sales")) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}