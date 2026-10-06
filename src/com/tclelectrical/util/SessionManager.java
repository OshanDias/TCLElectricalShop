package com.tclelectrical.util;

import com.tclelectrical.model.Employee;

public class SessionManager {
    private static Employee currentUser;

    public static void setCurrentUser(Employee e) { currentUser = e; }
    public static Employee getCurrentUser() { return currentUser; }
    public static void logout() { currentUser = null; }
}