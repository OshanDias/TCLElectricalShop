package com.tclelectrical.model;

import java.sql.Date;

public class Employee {
    private int employeeId;
    private String firstName, lastName, nic, phone, email, role, username, password, status;
    private Date hireDate;
    private double salary;

    public Employee() {}

    public Employee(int employeeId, String firstName, String lastName, String nic, String phone,
                    String email, String role, String username, String password, Date hireDate,
                    double salary, String status) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nic = nic;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.username = username;
        this.password = password;
        this.hireDate = hireDate;
        this.salary = salary;
        this.status = status;
    }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFullName() { return firstName + " " + lastName; }
    @Override public String toString() { return getFullName(); }
}