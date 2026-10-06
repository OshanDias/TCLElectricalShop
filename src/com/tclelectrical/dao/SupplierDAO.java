package com.tclelectrical.dao;

import com.tclelectrical.model.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    private final Connection con = DBConnection.getInstance().getConnection();

    public List<Supplier> getAll() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers ORDER BY company_name";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Supplier> search(String kw) {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM suppliers WHERE company_name LIKE ? OR city LIKE ? OR phone LIKE ? ORDER BY company_name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String q = "%" + kw + "%";
            ps.setString(1, q); ps.setString(2, q); ps.setString(3, q);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Supplier s) {
        String sql = "INSERT INTO suppliers (company_name, contact_person, phone, email, address, city, status) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, s);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Supplier s) {
        String sql = "UPDATE suppliers SET company_name=?, contact_person=?, phone=?, email=?, address=?, city=?, status=? WHERE supplier_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, s);
            ps.setInt(8, s.getSupplierId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM suppliers WHERE supplier_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private void bind(PreparedStatement ps, Supplier s) throws SQLException {
        ps.setString(1, s.getCompanyName());
        ps.setString(2, s.getContactPerson());
        ps.setString(3, s.getPhone());
        ps.setString(4, s.getEmail());
        ps.setString(5, s.getAddress());
        ps.setString(6, s.getCity());
        ps.setString(7, s.getStatus() == null ? "ACTIVE" : s.getStatus());
    }

    private Supplier map(ResultSet rs) throws SQLException {
        return new Supplier(rs.getInt("supplier_id"), rs.getString("company_name"),
            rs.getString("contact_person"), rs.getString("phone"), rs.getString("email"),
            rs.getString("address"), rs.getString("city"), rs.getString("status"));
    }
}