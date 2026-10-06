package com.tclelectrical.controller;

import com.tclelectrical.dao.DBConnection;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

/**
 * Generates the Sales Report using JasperReports.
 * The .jrxml design is embedded below and compiled at runtime,
 * then filled directly from the MySQL connection (5-table JOIN).
 */
public class ReportController {

    public void showSalesReport() {
        try {
            JasperReport report = JasperCompileManager.compileReport(
                new ByteArrayInputStream(JRXML.getBytes(StandardCharsets.UTF_8)));

            JasperPrint print = JasperFillManager.fillReport(
                report, null, DBConnection.getInstance().getConnection());

            if (print.getPages().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                    "No sales data to show yet. Make a sale first.",
                    "Sales Report", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            JasperViewer viewer = new JasperViewer(print, false); // false = don't exit app on close
            viewer.setTitle("TCL Electrical Shop - Sales Report");
            viewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Report error: " + e.getMessage(),
                "Jasper Report", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final String JRXML = """
<?xml version="1.0" encoding="UTF-8"?>
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://jasperreports.sourceforge.net/jasperreports http://jasperreports.sourceforge.net/xsd/jasperreport.xsd"
    name="sales_report" pageWidth="842" pageHeight="595" orientation="Landscape"
    columnWidth="802" leftMargin="20" rightMargin="20" topMargin="20" bottomMargin="20">

    <queryString>
        <![CDATA[SELECT s.invoice_no, s.sale_date,
            CONCAT(c.first_name,' ',c.last_name) AS customer_name,
            CONCAT(e.first_name,' ',e.last_name) AS cashier_name,
            p.product_name, p.brand,
            si.quantity, si.unit_price, si.line_total
        FROM sale_items si
        JOIN sales s ON si.sale_id = s.sale_id
        JOIN products p ON si.product_id = p.product_id
        LEFT JOIN customers c ON s.customer_id = c.customer_id
        JOIN employees e ON s.employee_id = e.employee_id
        ORDER BY s.sale_date, s.invoice_no]]>
    </queryString>

    <field name="invoice_no" class="java.lang.String"/>
    <field name="sale_date" class="java.sql.Timestamp"/>
    <field name="customer_name" class="java.lang.String"/>
    <field name="cashier_name" class="java.lang.String"/>
    <field name="product_name" class="java.lang.String"/>
    <field name="brand" class="java.lang.String"/>
    <field name="quantity" class="java.lang.Integer"/>
    <field name="unit_price" class="java.math.BigDecimal"/>
    <field name="line_total" class="java.math.BigDecimal"/>

    <variable name="grandTotal" class="java.math.BigDecimal" calculation="Sum">
        <variableExpression><![CDATA[$F{line_total}]]></variableExpression>
    </variable>

    <title>
        <band height="55">
            <staticText>
                <reportElement x="0" y="0" width="802" height="28"/>
                <textElement textAlignment="Center"><font size="18" isBold="true"/></textElement>
                <text><![CDATA[TCL Electrical Shop - Sales Report]]></text>
            </staticText>
            <staticText>
                <reportElement x="0" y="30" width="802" height="16"/>
                <textElement textAlignment="Center"><font size="10"/></textElement>
                <text><![CDATA[Powering Your Needs]]></text>
            </staticText>
        </band>
    </title>

    <columnHeader>
        <band height="22">
            <staticText><reportElement x="0" y="0" width="110" height="20"/><textElement><font isBold="true"/></textElement><text><![CDATA[Invoice No]]></text></staticText>
            <staticText><reportElement x="110" y="0" width="90" height="20"/><textElement><font isBold="true"/></textElement><text><![CDATA[Date]]></text></staticText>
            <staticText><reportElement x="200" y="0" width="110" height="20"/><textElement><font isBold="true"/></textElement><text><![CDATA[Customer]]></text></staticText>
            <staticText><reportElement x="310" y="0" width="100" height="20"/><textElement><font isBold="true"/></textElement><text><![CDATA[Cashier]]></text></staticText>
            <staticText><reportElement x="410" y="0" width="150" height="20"/><textElement><font isBold="true"/></textElement><text><![CDATA[Product]]></text></staticText>
            <staticText><reportElement x="560" y="0" width="80" height="20"/><textElement><font isBold="true"/></textElement><text><![CDATA[Brand]]></text></staticText>
            <staticText><reportElement x="640" y="0" width="40" height="20"/><textElement textAlignment="Right"><font isBold="true"/></textElement><text><![CDATA[Qty]]></text></staticText>
            <staticText><reportElement x="680" y="0" width="60" height="20"/><textElement textAlignment="Right"><font isBold="true"/></textElement><text><![CDATA[Unit Price]]></text></staticText>
            <staticText><reportElement x="740" y="0" width="62" height="20"/><textElement textAlignment="Right"><font isBold="true"/></textElement><text><![CDATA[Line Total]]></text></staticText>
        </band>
    </columnHeader>

    <detail>
        <band height="18">
            <textField><reportElement x="0" y="0" width="110" height="16"/><textFieldExpression><![CDATA[$F{invoice_no}]]></textFieldExpression></textField>
            <textField pattern="yyyy-MM-dd"><reportElement x="110" y="0" width="90" height="16"/><textFieldExpression><![CDATA[$F{sale_date}]]></textFieldExpression></textField>
            <textField><reportElement x="200" y="0" width="110" height="16"/><textFieldExpression><![CDATA[$F{customer_name}]]></textFieldExpression></textField>
            <textField><reportElement x="310" y="0" width="100" height="16"/><textFieldExpression><![CDATA[$F{cashier_name}]]></textFieldExpression></textField>
            <textField><reportElement x="410" y="0" width="150" height="16"/><textFieldExpression><![CDATA[$F{product_name}]]></textFieldExpression></textField>
            <textField><reportElement x="560" y="0" width="80" height="16"/><textFieldExpression><![CDATA[$F{brand}]]></textFieldExpression></textField>
            <textField><reportElement x="640" y="0" width="40" height="16"/><textElement textAlignment="Right"/><textFieldExpression><![CDATA[$F{quantity}]]></textFieldExpression></textField>
            <textField pattern="#,##0.00"><reportElement x="680" y="0" width="60" height="16"/><textElement textAlignment="Right"/><textFieldExpression><![CDATA[$F{unit_price}]]></textFieldExpression></textField>
            <textField pattern="#,##0.00"><reportElement x="740" y="0" width="62" height="16"/><textElement textAlignment="Right"/><textFieldExpression><![CDATA[$F{line_total}]]></textFieldExpression></textField>
        </band>
    </detail>

    <summary>
        <band height="30">
            <staticText><reportElement x="560" y="6" width="180" height="20"/><textElement textAlignment="Right"><font isBold="true" size="12"/></textElement><text><![CDATA[Grand Total (Rs.):]]></text></staticText>
            <textField pattern="#,##0.00"><reportElement x="740" y="6" width="62" height="20"/><textElement textAlignment="Right"><font isBold="true" size="12"/></textElement><textFieldExpression><![CDATA[$V{grandTotal}]]></textFieldExpression></textField>
        </band>
    </summary>
</jasperReport>
""";
}