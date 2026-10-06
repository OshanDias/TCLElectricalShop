# 📘 TCL Electrical Shop – Project Development Record

**Module:** Enterprise Application Development (EAD) – CW1
**Batch:** DCSD 25.3 — NIBM, School of Computing & Engineering
**Application:** Electrical goods retail / sales management system (Sales & Retail Management Sector)
**Stack:** Java 17 · Java Swing · MySQL 8.0 · JDBC · JasperReports 6.20 · Apache NetBeans

---

## 1. Project Overview

TCL Electrical Shop is a Java Swing desktop enterprise application for running an electrical
goods retail shop. It covers point-of-sale billing, inventory, customers, suppliers, employees,
and sales reporting. It is built on a strict **MVC** architecture with **DAO**, **Service**,
and **Singleton** design patterns, full input validation, custom exception handling, and a
JasperReports report that joins five database tables.

**Theme / Branding:** Dark navy (`#0E1621`) + amber (`#F5A623`), taken from the TCL Electrical
Shop logo ("Powering Your Needs").

---

## 2. Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| UI | Java Swing (hand-coded) |
| Database | MySQL 8.0 |
| DB Access | JDBC – MySQL Connector/J |
| Reporting | JasperReports 6.20 + iText |
| Build | Apache Ant (via NetBeans) |
| Packaging | `.jar` (NetBeans) + `.exe` (jpackage, JDK 17) |
| Version control | Git / GitHub |

---

## 3. Database Design

**Database:** `tcl_electrical_shop_db` — 7 tables with foreign-key relationships.

| Table | Purpose | Key FKs |
|-------|---------|---------|
| `suppliers` | Supplier records | — |
| `categories` | Product categories | — |
| `employees` | Staff + login accounts | — |
| `products` | Catalogue + live stock | category_id, supplier_id |
| `customers` | Customer registry + loyalty | — |
| `sales` | Invoice header (totals, payment) | customer_id, employee_id |
| `sale_items` | Invoice line items | sale_id, product_id |

The **sales → sale_items** header/detail split allows one invoice to hold many products and
drives the multi-table sales report. Seed data is included for all tables.

---

## 4. Architecture (MVC + DAO + Service)

```
src/com/tclelectrical/
├── Main.java                 # entry point -> LoginView
├── model/                    # POJOs
│   ├── Supplier, Category, Employee, Product, Customer, Sale, SaleItem
├── dao/                      # Data access
│   ├── DBConnection          # Singleton
│   ├── SupplierDAO, CategoryDAO, EmployeeDAO, ProductDAO, CustomerDAO, SaleDAO
├── service/                  # (reserved for business-logic layer)
├── controller/
│   └── ReportController       # JasperReports generation
├── view/                     # Swing UI
│   ├── LoginView, RegisterView, DashboardView
│   ├── ProductView, CustomerView, SupplierView, EmployeeView
│   └── BillingView
├── exception/
│   ├── InsufficientStockException   # custom (mandatory)
│   └── InvalidInputException        # custom
└── util/
    ├── Validator, UITheme, SessionManager
```

### Design Patterns Used
- **Singleton** – `DBConnection`: one shared database connection instance.
- **DAO (Data Access Object)** – one DAO per entity; isolates all SQL from the UI.
- **Service Layer** – separates business rules from persistence and presentation.
- **MVC** – Model (POJOs) / View (Swing) / Controller (report + flow glue).

---

## 5. Features Delivered

- **Authentication** – login (`admin` / `admin123`) + account registration with role dropdown
  (Admin / Manager / Cashier), show-password toggle.
- **Dashboard** – live KPI cards: Today's Sales, Today's Revenue, Low Stock Items, Total Products;
  quick-action navigation to all modules.
- **Billing / POS (main transaction)** – searchable product picker, multi-item cart, live
  subtotal / discount / tax / total / change calculation, payment method, automatic
  **stock deduction**, auto invoice numbering, saved as one DB transaction.
- **Products** – full CRUD, category & supplier links, reorder level, warranty, low-stock query.
- **Customers** – full CRUD + loyalty points.
- **Suppliers** – full CRUD.
- **Employees** – full CRUD with roles, salary, hire date, login accounts.
- **Validation** – Sri Lankan phone (`0XXXXXXXXX`), NIC (9+V or 12-digit), email, numeric fields.
- **Custom exceptions** – `InsufficientStockException` (thrown during billing when stock is
  short, with full transaction rollback), `InvalidInputException` (thrown by the validator).
- **Jasper Report** – sales report joining **5 tables** (sale_items, sales, products, customers,
  employees), with a grand total; exportable to PDF.

---

## 6. Development Timeline (how it was built)

| Phase | Work done |
|-------|-----------|
| 1 | Designed and created the MySQL schema (7 tables + seed data) |
| 2 | Created the NetBeans project, added libraries, built the MVC package structure |
| 3 | Built the 7 model POJOs + `DBConnection` (Singleton) + `UITheme` |
| 4 | Added the 2 custom exceptions + `Validator` |
| 5 | Built all 6 DAO classes (full CRUD + search + dashboard queries) |
| 6 | Built Login + Register screens (with show-password + role selection) |
| 7 | Built the Dashboard with live KPI cards + navigation |
| 8 | Built the CRUD screens: Products, Customers, Suppliers, Employees |
| 9 | Built the Billing/POS screen with cart, totals, stock deduction, product search |
| 10 | Built the JasperReports sales report (5-table join → PDF) |
| 11 | Packaged `.jar` + `.exe` (jpackage), wrote README + .gitignore, pushed to GitHub |

---

## 7. Build & Deployment

**JAR:** NetBeans → `Clean and Build` → `dist/TCLElectricalShop.jar` (keep `dist/lib/` beside it).

**EXE:** built with `jpackage` (bundled with JDK 17):
```
jpackage --type app-image --name TCLElectricalShop --input dist ^
         --main-jar TCLElectricalShop.jar ^
         --main-class com.tclelectrical.Main --dest build-exe
```
Output: `build-exe/TCLElectricalShop/TCLElectricalShop.exe` (self-contained, bundled runtime).

**Run:** double-click the jar/exe, or `java -jar TCLElectricalShop.jar`.

---

## 8. Setup Instructions

1. Run `database/tcl_electrical_shop_db.sql` in MySQL Workbench.
2. In `dao/DBConnection.java`, set the `PASSWORD` constant to your MySQL root password.
3. Add the `lib/` jars to the NetBeans project classpath.
4. Run `Main.java`, or the built jar/exe.
5. Login: `admin` / `admin123`.

---

## 9. Notes & Future Improvements

- Passwords are stored in plain text for this coursework demo; production should hash them (e.g. BCrypt).
- Account creation from the login screen would, in production, be restricted to logged-in admins.
- The `service/` layer is structured to hold business logic as the system grows.
- Possible extensions: email/SMS API integration, returns/refunds, barcode scanning, more reports.

---

## 10. Repository Structure

```
TCLElectricalShop/
├── src/                  # Java source (MVC)
├── lib/                  # MySQL connector + JasperReports jars
├── database/             # tcl_electrical_shop_db.sql
├── reports/              # sales_report.jrxml
├── dist/                 # built jar + lib
├── build-exe/            # jpackage .exe output
├── .gitignore
├── README.md
└── PROJECT_RECORD.md     # this file
```
