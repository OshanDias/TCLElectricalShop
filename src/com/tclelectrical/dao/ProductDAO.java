package com.tclelectrical.dao;

import com.tclelectrical.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final Connection con = DBConnection.getInstance().getConnection();

    private static final String SELECT_JOIN =
        "SELECT p.*, c.category_name, s.company_name AS supplier_name " +
        "FROM products p " +
        "JOIN categories c ON p.category_id = c.category_id " +
        "JOIN suppliers  s ON p.supplier_id = s.supplier_id ";

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT_JOIN + "ORDER BY p.product_name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Product> search(String kw) {
        List<Product> list = new ArrayList<>();
        String sql = SELECT_JOIN + "WHERE p.product_name LIKE ? OR p.brand LIKE ? OR p.sku LIKE ? ORDER BY p.product_name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String q = "%" + kw + "%";
            ps.setString(1,q); ps.setString(2,q); ps.setString(3,q);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Active products for the billing dropdown. */
    public List<Product> getActive() {
        List<Product> list = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT_JOIN + "WHERE p.status='ACTIVE' ORDER BY p.product_name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** At or below reorder level - dashboard low-stock card. */
    public List<Product> getLowStock() {
        List<Product> list = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(SELECT_JOIN + "WHERE p.qty_in_stock <= p.reorder_level ORDER BY p.qty_in_stock")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Product p) {
        String sql = "INSERT INTO products (product_name,category_id,supplier_id,brand,model,sku,cost_price,selling_price,qty_in_stock,reorder_level,warranty_months,status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, p);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Product p) {
        String sql = "UPDATE products SET product_name=?,category_id=?,supplier_id=?,brand=?,model=?,sku=?,cost_price=?,selling_price=?,qty_in_stock=?,reorder_level=?,warranty_months=?,status=? WHERE product_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, p);
            ps.setInt(13, p.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE product_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int countAll() {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private void bind(PreparedStatement ps, Product p) throws SQLException {
        ps.setString(1, p.getProductName());
        ps.setInt(2, p.getCategoryId());
        ps.setInt(3, p.getSupplierId());
        ps.setString(4, p.getBrand());
        ps.setString(5, p.getModel());
        ps.setString(6, p.getSku());
        ps.setDouble(7, p.getCostPrice());
        ps.setDouble(8, p.getSellingPrice());
        ps.setInt(9, p.getQtyInStock());
        ps.setInt(10, p.getReorderLevel());
        ps.setInt(11, p.getWarrantyMonths());
        ps.setString(12, p.getStatus() == null ? "ACTIVE" : p.getStatus());
    }

    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setProductName(rs.getString("product_name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setSupplierId(rs.getInt("supplier_id"));
        p.setBrand(rs.getString("brand"));
        p.setModel(rs.getString("model"));
        p.setSku(rs.getString("sku"));
        p.setCostPrice(rs.getDouble("cost_price"));
        p.setSellingPrice(rs.getDouble("selling_price"));
        p.setQtyInStock(rs.getInt("qty_in_stock"));
        p.setReorderLevel(rs.getInt("reorder_level"));
        p.setWarrantyMonths(rs.getInt("warranty_months"));
        p.setStatus(rs.getString("status"));
        p.setCategoryName(rs.getString("category_name"));
        p.setSupplierName(rs.getString("supplier_name"));
        return p;
    }
}