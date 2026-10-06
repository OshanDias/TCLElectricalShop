package com.tclelectrical.dao;

import com.tclelectrical.model.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private final Connection con = DBConnection.getInstance().getConnection();

    /** Login check. Returns the Employee if credentials match an ACTIVE user, else null. */
    public Employee authenticate(String username, String password) {
        String sql = "SELECT * FROM employees WHERE username=? AND password=? AND status='ACTIVE'";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees ORDER BY first_name";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Employee> search(String kw) {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees WHERE first_name LIKE ? OR last_name LIKE ? OR nic LIKE ? OR role LIKE ? ORDER BY first_name";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String q = "%" + kw + "%";
            ps.setString(1,q); ps.setString(2,q); ps.setString(3,q); ps.setString(4,q);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(map(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(Employee e) {
        String sql = "INSERT INTO employees (first_name,last_name,nic,phone,email,role,username,password,hire_date,salary,status) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, e);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public boolean update(Employee e) {
        String sql = "UPDATE employees SET first_name=?,last_name=?,nic=?,phone=?,email=?,role=?,username=?,password=?,hire_date=?,salary=?,status=? WHERE employee_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, e);
            ps.setInt(12, e.getEmployeeId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM employees WHERE employee_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) { ex.printStackTrace(); return false; }
    }

    private void bind(PreparedStatement ps, Employee e) throws SQLException {
        ps.setString(1, e.getFirstName());
        ps.setString(2, e.getLastName());
        ps.setString(3, e.getNic());
        ps.setString(4, e.getPhone());
        ps.setString(5, e.getEmail());
        ps.setString(6, e.getRole());
        ps.setString(7, e.getUsername());
        ps.setString(8, e.getPassword());
        ps.setDate(9, e.getHireDate());
        ps.setDouble(10, e.getSalary());
        ps.setString(11, e.getStatus() == null ? "ACTIVE" : e.getStatus());
    }

    private Employee map(ResultSet rs) throws SQLException {
        return new Employee(rs.getInt("employee_id"), rs.getString("first_name"), rs.getString("last_name"),
            rs.getString("nic"), rs.getString("phone"), rs.getString("email"), rs.getString("role"),
            rs.getString("username"), rs.getString("password"), rs.getDate("hire_date"),
            rs.getDouble("salary"), rs.getString("status"));
    }
}