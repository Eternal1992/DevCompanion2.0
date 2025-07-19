// File: CodeAnalyzer.java
package com.ethereon.app;

import java.util.ArrayList;
import java.util.List;

public class CodeAnalyzer {

    public static List<String> analyze(String code) {
        List<String> issues = new ArrayList<>();

        if (code == null || code.trim().isEmpty()) {
            issues.add("Code is empty or missing.");
            return issues;
        }

        if (!code.contains("class")) {
            issues.add("No class definition found.");
        }

        if (!code.contains("public static void main") && code.contains("class")) {
            issues.add("No main method found (expected for runnable Java files).");
        }

        if (code.contains("System.out.print") && !code.contains(";")) {
            issues.add("Print statement may be missing a semicolon.");
        }

        if (code.contains("== true") || code.contains("== false")) {
            issues.add("Use simplified boolean check (e.g., `if (x)` instead of `if (x == true)`).");
        }

        if (code.contains("TODO")) {
            issues.add("Contains TODO comments for future development.");
        }

        // Potential LLM-based insight placeholder removed; now ready for dynamic injection
        return issues;
    }

    public static String suggestFormattingFix(String code) {
        if (code == null) return "";

        // Simple auto-indentation (expandable)
        return code.replaceAll("(?m)^", "    ");
    }
}