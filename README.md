# ⚡ TCL Electrical Shop – Management System

A Java Swing desktop application for managing an electrical goods retail shop — point-of-sale billing, inventory, customers, suppliers, employees, and sales reporting. Built for the **Enterprise Application Development (EAD)** module, following strict **MVC** architecture and OOP design patterns.

> **Powering Your Needs**

![Java](https://img.shields.io/badge/Java-17-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Swing](https://img.shields.io/badge/UI-Java%20Swing-green)
![JasperReports](https://img.shields.io/badge/Reports-JasperReports%206.20-red)

---

## ✨ Features

- **Authentication** – secure login + account registration with role selection (Admin / Manager / Cashier)
- **Dashboard** – live KPI cards: today's sales, today's revenue, low-stock items, total products
- **Billing / POS (main transaction)** – searchable product picker, multi-item cart, live subtotal / discount / tax / total / change, automatic **stock deduction**, invoice numbering
- **Inventory (Products)** – full CRUD, category & supplier links, reorder levels, warranty, low-stock tracking
- **Customer Management** – full CRUD with loyalty points
- **Supplier Management** – full CRUD
- **Employee Management** – full CRUD with roles and login accounts
- **Input validation** – Sri Lankan phone/NIC, email, numeric checks
- **Custom exceptions** – `InsufficientStockException`, `InvalidInputException`
- **Jasper Report** – sales report joining **5 tables** (sales, sale_items, products, customers, employees), exportable to PDF

---

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| UI | Java Swing |
| Database | MySQL 8.0 |
| DB Access | JDBC (MySQL Connector/J) |
| Reporting | JasperReports 6.20 + iText |
| IDE | Apache NetBeans |
| Build | Ant (NetBeans) |

---

## 🏗️ Architecture (MVC + DAO + Service)

```
src/com/tclelectrical/
├── Main.java                 # entry point -> LoginView
├── model/                    # POJOs (Supplier, Category, Employee, Product, Customer, Sale, SaleItem)
├── dao/                      # Data access (DBConnection [Singleton] + one DAO per entity)
├── service/                  # Business logic layer
├── controller/               # ReportController (JasperReports)
├── view/                     # Swing UI (Login, Register, Dashboard, Billing, + CRUD screens)
├── exception/                # InsufficientStockException, InvalidInputException
└── util/                     # Validator, UITheme, SessionManager
```

### Design Patterns
- **Singleton** – `DBConnection` (one shared connection)
- **DAO** – one DAO per table, isolates SQL
- **Service Layer** – business rules separated from UI and persistence
- **MVC** – Model / View / Controller separation

---

## 🗄️ Database Schema

Database: `tcl_electrical_shop_db` — 7 tables:

| Table | Purpose |
|-------|---------|
| `suppliers` | Supplier records |
| `categories` | Product categories |
| `employees` | Staff + login accounts |
| `products` | Catalogue + live stock (FK → categories, suppliers) |
| `customers` | Customer registry + loyalty |
| `sales` | Invoice header (FK → customers, employees) |
| `sale_items` | Invoice line items (FK → sales, products) |

Run `database/tcl_electrical_shop_db.sql` in MySQL Workbench to create the schema and seed data.

---

## 🚀 Installation & Setup

1. **Clone the repo**
   ```bash
   git clone <your-repo-url>
   ```
2. **Create the database** – open MySQL Workbench, run `database/tcl_electrical_shop_db.sql`
3. **Set your DB password** – in `src/com/tclelectrical/dao/DBConnection.java`, update the `PASSWORD` constant to your MySQL root password
4. **Open in NetBeans**, add the libraries in `lib/` to the project classpath (MySQL Connector, JasperReports + dependencies)
5. **Run** – right-click `Main.java` → Run File, or run the built jar

### Default Login
| Username | Password | Role |
|----------|----------|------|
| `admin`  | `admin123` | ADMIN |

---

## 📦 Build & Deployment

- **JAR:** `Clean and Build` in NetBeans → `dist/TCLElectricalShop.jar` (keep `dist/lib/` beside it)
- **EXE:** generated from the jar using **Launch4j**
- Run: `java -jar TCLElectricalShop.jar`

---

## 📊 Reports

The Sales Report (Reports button on the dashboard) is generated with JasperReports from a 5-table JOIN query and can be exported to PDF from the viewer toolbar.

---

## 👤 Author

**M.K.O. Dias** — NIBM, School of Computing & Engineering
EAD Coursework (DCSD 25.3)

---

## 📄 License

Academic coursework project — free to use for educational purposes.
