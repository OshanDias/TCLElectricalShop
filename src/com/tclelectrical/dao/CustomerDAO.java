package com.tclelectrical.dao;

import com.tclelectrical.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private final Connection con = DBConnection.getInstance().getConnection();

    public List<Customer> getAll() {
        List<Customer> list = new ArrayList<>();
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM customers ORDER BY first_name")) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Customer> search(String kw) {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE first_name LIKE ? OR last_name LIKE ? OR phone LIKE ? OR nic LIKE ? ORDER BY first_name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String q = "%" + kw + "%";
            ps.setString(1,q); ps.setString(2,q); ps.setString(3,q); ps.setString(4,q);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Customer c) {
        String sql = "INSERT INTO customers (first_name,last_name,phone,email,nic,address,city,loyalty_points,registered_date) VALUES (?,?,?,?,?,?,?,?,CURDATE())";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, c);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean update(Customer c) {
        String sql = "UPDATE customers SET first_name=?,last_name=?,phone=?,email=?,nic=?,address=?,city=?,loyalty_points=? WHERE customer_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, c);
            ps.setInt(9, c.getCustomerId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM customers WHERE customer_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public int countAll() {
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM customers")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private void bind(PreparedStatement ps, Customer c) throws SQLException {
        ps.setString(1, c.getFirstName());
        ps.setString(2, c.getLastName());
        ps.setString(3, c.getPhone());
        ps.setString(4, c.getEmail());
        ps.setString(5, c.getNic());
        ps.setString(6, c.getAddress());
        ps.setString(7, c.getCity());
        ps.setInt(8, c.getLoyaltyPoints());
    }

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setCustomerId(rs.getInt("customer_id"));
        c.setFirstName(rs.getString("first_name"));
        c.setLastName(rs.getString("last_name"));
        c.setPhone(rs.getString("phone"));
        c.setEmail(rs.getString("email"));
        c.setNic(rs.getString("nic"));
        c.setAddress(rs.getString("address"));
        c.setCity(rs.getString("city"));
        c.setLoyaltyPoints(rs.getInt("loyalty_points"));
        c.setRegisteredDate(rs.getDate("registered_date"));
        return c;
    }
}