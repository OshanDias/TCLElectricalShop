package com.tclelectrical.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * Central theme (TCL Electrical Shop logo colours).
 * Dark navy + amber. Use these constants in every view for consistency.
 */
public class UITheme {

    // ---- Colours (from the logo) ----
    public static final Color BG_DARK   = new Color(0x0E1621);  // main background
    public static final Color SURFACE   = new Color(0x1A2433);  // panels / cards / tables
    public static final Color SURFACE_2 = new Color(0x22304A);  // hover / header rows
    public static final Color AMBER      = new Color(0xF5A623); // primary accent
    public static final Color AMBER_DARK = new Color(0xD48806); // button hover
    public static final Color TEXT_LIGHT = new Color(0xFFFFFF);
    public static final Color TEXT_MUTED = new Color(0x8A94A6);
    public static final Color SUCCESS    = new Color(0x2ECC71);
    public static final Color DANGER     = new Color(0xE74C3C);

    // ---- Fonts ----
    public static final Font FONT_TITLE  = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_LABEL  = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 14);

    /** Amber primary button. */
    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(AMBER);
        b.setForeground(BG_DARK);
        b.setFont(FONT_BUTTON);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Dark secondary button (outline style). */
    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(SURFACE_2);
        b.setForeground(TEXT_LIGHT);
        b.setFont(FONT_BUTTON);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(AMBER, 1));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}