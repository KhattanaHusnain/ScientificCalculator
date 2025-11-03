package com.scientific.calculator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.*;
import java.util.regex.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView displayText;
    private TextView expressionText;
    private String currentExpression = "";
    private boolean isNewCalculation = false;
    private Map<String, Double> variableValues = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        displayText = findViewById(R.id.display);
        expressionText = findViewById(R.id.expression);
        setupButtonListeners();
    }

    private void setupButtonListeners() {
        int[] numberIds = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9};
        for (int id : numberIds) findViewById(id).setOnClickListener(this);

        int[] operatorIds = {R.id.btn_add, R.id.btn_subtract, R.id.btn_multiply, R.id.btn_divide};
        for (int id : operatorIds) findViewById(id).setOnClickListener(this);

        int[] functionIds = {R.id.btn_x, R.id.btn_y, R.id.btn_z, R.id.btn_power,
                R.id.btn_sqrt, R.id.btn_square, R.id.btn_open_paren, R.id.btn_close_paren,
                R.id.btn_abs, R.id.btn_equals_sign, R.id.btn_decimal, R.id.btn_negate};
        for (int id : functionIds) findViewById(id).setOnClickListener(this);

        findViewById(R.id.btn_enter).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_simplify).setOnClickListener(this);
        findViewById(R.id.btn_expand).setOnClickListener(this);
        findViewById(R.id.btn_factor).setOnClickListener(this);
        findViewById(R.id.btn_solve).setOnClickListener(this);
        findViewById(R.id.btn_evaluate).setOnClickListener(this);
        findViewById(R.id.btn_derivative).setOnClickListener(this);
        findViewById(R.id.btn_integration).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (isNewCalculation && !isOperator(id) && id != R.id.btn_enter) {
            if (id != R.id.btn_simplify && id != R.id.btn_expand &&
                    id != R.id.btn_factor && id != R.id.btn_solve &&
                    id != R.id.btn_evaluate && id != R.id.btn_derivative &&
                    id != R.id.btn_integration) {
                currentExpression = "";
                isNewCalculation = false;
            }
        }

        if (id == R.id.btn_0) appendToExpression("0");
        else if (id == R.id.btn_1) appendToExpression("1");
        else if (id == R.id.btn_2) appendToExpression("2");
        else if (id == R.id.btn_3) appendToExpression("3");
        else if (id == R.id.btn_4) appendToExpression("4");
        else if (id == R.id.btn_5) appendToExpression("5");
        else if (id == R.id.btn_6) appendToExpression("6");
        else if (id == R.id.btn_7) appendToExpression("7");
        else if (id == R.id.btn_8) appendToExpression("8");
        else if (id == R.id.btn_9) appendToExpression("9");
        else if (id == R.id.btn_decimal) appendToExpression(".");
        else if (id == R.id.btn_add) appendToExpression("+");
        else if (id == R.id.btn_subtract) appendToExpression("-");
        else if (id == R.id.btn_multiply) appendToExpression("*");
        else if (id == R.id.btn_divide) appendToExpression("/");
        else if (id == R.id.btn_x) appendToExpression("x");
        else if (id == R.id.btn_y) appendToExpression("y");
        else if (id == R.id.btn_z) appendToExpression("z");
        else if (id == R.id.btn_open_paren) appendToExpression("(");
        else if (id == R.id.btn_close_paren) appendToExpression(")");
        else if (id == R.id.btn_equals_sign) appendToExpression("=");
        else if (id == R.id.btn_square) appendToExpression("^2");
        else if (id == R.id.btn_sqrt) appendToExpression("sqrt(");
        else if (id == R.id.btn_abs) appendToExpression("abs(");
        else if (id == R.id.btn_power) appendToExpression("^");
        else if (id == R.id.btn_negate) toggleNegative();
        else if (id == R.id.btn_delete) deleteLastChar();
        else if (id == R.id.btn_clear) clearAll();
        else if (id == R.id.btn_enter) processEnter();
        else if (id == R.id.btn_simplify) performSimplify();
        else if (id == R.id.btn_expand) performExpand();
        else if (id == R.id.btn_factor) performFactor();
        else if (id == R.id.btn_solve) performSolve();
        else if (id == R.id.btn_evaluate) performEvaluate();
        else if (id == R.id.btn_derivative) performDerivative();
        else if (id == R.id.btn_integration) performIntegration();

        updateDisplay();
    }

    private boolean isOperator(int id) {
        return id == R.id.btn_add || id == R.id.btn_subtract ||
                id == R.id.btn_multiply || id == R.id.btn_divide;
    }

    private void appendToExpression(String text) {
        currentExpression += text;
    }

    private void toggleNegative() {
        if (currentExpression.isEmpty()) {
            currentExpression = "-";
        } else {
            currentExpression = "(-" + currentExpression + ")";
        }
    }

    private void deleteLastChar() {
        if (!currentExpression.isEmpty()) {
            currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
        }
    }

    private void clearAll() {
        currentExpression = "";
        displayText.setText("0");
        expressionText.setText("");
        isNewCalculation = false;
    }

    private void updateDisplay() {
        if (currentExpression.isEmpty()) {
            displayText.setText("0");
            expressionText.setText("");
        } else {
            displayText.setText(formatWithSuperscript(currentExpression));
            expressionText.setText(formatWithSuperscript(currentExpression));
        }
    }

    private void processEnter() {
        if (currentExpression.isEmpty()) return;

        try {
            String result = evaluateExpression(currentExpression);
            expressionText.setText(formatWithSuperscript(currentExpression));
            displayText.setText(formatWithSuperscript(result));
            currentExpression = result;
            isNewCalculation = true;
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void performSimplify() {
        if (currentExpression.isEmpty()) return;

        try {
            AlgebraEngine engine = new AlgebraEngine();
            String result = engine.simplify(currentExpression);
            expressionText.setText(formatWithSuperscript("Simplify: " + currentExpression));
            displayText.setText(formatWithSuperscript(result));
            currentExpression = result;
            isNewCalculation = true;
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void performExpand() {
        if (currentExpression.isEmpty()) return;

        try {
            AlgebraEngine engine = new AlgebraEngine();
            String result = engine.expand(currentExpression);
            expressionText.setText(formatWithSuperscript("Expand: " + currentExpression));
            displayText.setText(formatWithSuperscript(result));
            currentExpression = result;
            isNewCalculation = true;
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void performFactor() {
        if (currentExpression.isEmpty()) return;

        try {
            AlgebraEngine engine = new AlgebraEngine();
            String result = engine.factor(currentExpression);
            expressionText.setText(formatWithSuperscript("Factor: " + currentExpression));
            displayText.setText(formatWithSuperscript(result));
            currentExpression = result;
            isNewCalculation = true;
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void performSolve() {
        if (currentExpression.isEmpty()) return;

        try {
            AlgebraEngine engine = new AlgebraEngine();
            String result = engine.solve(currentExpression);
            expressionText.setText(formatWithSuperscript("Solve: " + currentExpression));
            displayText.setText(formatWithSuperscript(result));
            currentExpression = result;
            isNewCalculation = true;
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void performEvaluate() {
        if (currentExpression.isEmpty()) return;

        Set<String> variables = extractVariables(currentExpression);
        if (variables.isEmpty()) {
            Toast.makeText(this, "No variables found to evaluate", Toast.LENGTH_SHORT).show();
            return;
        }

        promptForVariableValues(variables);
    }

    private void performDerivative() {
        if (currentExpression.isEmpty()) return;

        Set<String> variables = extractVariables(currentExpression);
        if (variables.isEmpty()) {
            Toast.makeText(this, "No variables found in expression", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prompt user to select which variable to differentiate with respect to
        String[] varArray = variables.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Differentiate with respect to:");
        builder.setItems(varArray, (dialog, which) -> {
            String variable = varArray[which];
            try {
                AlgebraEngine engine = new AlgebraEngine();
                String result = engine.derivative(currentExpression, variable);
                expressionText.setText(formatWithSuperscript("d/d" + variable + ": " + currentExpression));
                displayText.setText(formatWithSuperscript(result));
                currentExpression = result;
                isNewCalculation = true;
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    private void performIntegration() {
        if (currentExpression.isEmpty()) return;

        Set<String> variables = extractVariables(currentExpression);
        if (variables.isEmpty()) {
            Toast.makeText(this, "No variables found in expression", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prompt user to select which variable to integrate with respect to
        String[] varArray = variables.toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Integrate with respect to:");
        builder.setItems(varArray, (dialog, which) -> {
            String variable = varArray[which];
            try {
                AlgebraEngine engine = new AlgebraEngine();
                String result = engine.integrate(currentExpression, variable);
                expressionText.setText(formatWithSuperscript("∫" + currentExpression + " d" + variable));
                displayText.setText(formatWithSuperscript(result + "+C"));
                currentExpression = result + "+C";
                isNewCalculation = true;
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    private void promptForVariableValues(Set<String> variables) {
        List<String> varList = new ArrayList<>(variables);
        promptForNextVariable(varList, 0, new HashMap<>());
    }

    private void promptForNextVariable(List<String> variables, int index, Map<String, Double> values) {
        if (index >= variables.size()) {
            try {
                AlgebraEngine engine = new AlgebraEngine();
                String result = engine.evaluate(currentExpression, values);
                expressionText.setText(formatWithSuperscript("Evaluate: " + currentExpression));
                displayText.setText(formatWithSuperscript(result));
                currentExpression = result;
                isNewCalculation = true;
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
            return;
        }

        String var = variables.get(index);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter value for " + var);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            try {
                double value = Double.parseDouble(input.getText().toString());
                values.put(var, value);
                promptForNextVariable(variables, index + 1, values);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
                promptForNextVariable(variables, index, values);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private Set<String> extractVariables(String expr) {
        Set<String> vars = new HashSet<>();
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(expr);
        while (matcher.find()) {
            String var = matcher.group();
            if (!var.equals("sqrt") && !var.equals("abs")) {
                vars.add(var);
            }
        }
        return vars;
    }

    private String evaluateExpression(String expr) {
        if (containsVariables(expr)) {
            AlgebraEngine engine = new AlgebraEngine();
            return engine.simplify(expr);
        } else {
            return evaluateNumeric(expr);
        }
    }

    private boolean containsVariables(String expr) {
        return expr.matches(".*[a-zA-Z].*");
    }

    private String evaluateNumeric(String expr) {
        try {
            expr = expr.replace("×", "*").replace("÷", "/");
            expr = processMathFunctions(expr);
            double result = eval(expr);

            if (result == (long) result) {
                return String.valueOf((long) result);
            } else {
                return String.format("%.10f", result).replaceAll("0*$", "").replaceAll("\\.$", "");
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot evaluate: " + e.getMessage());
        }
    }

    private String processMathFunctions(String expr) {
        while (expr.contains("sqrt(")) {
            int start = expr.indexOf("sqrt(");
            int end = findMatchingParen(expr, start + 4);
            String inner = expr.substring(start + 5, end);
            double value = eval(inner);
            expr = expr.substring(0, start) + Math.sqrt(value) + expr.substring(end + 1);
        }

        while (expr.contains("abs(")) {
            int start = expr.indexOf("abs(");
            int end = findMatchingParen(expr, start + 3);
            String inner = expr.substring(start + 4, end);
            double value = eval(inner);
            expr = expr.substring(0, start) + Math.abs(value) + expr.substring(end + 1);
        }

        return expr;
    }

    private int findMatchingParen(String expr, int start) {
        int count = 1;
        for (int i = start + 1; i < expr.length(); i++) {
            if (expr.charAt(i) == '(') count++;
            if (expr.charAt(i) == ')') count--;
            if (count == 0) return i;
        }
        return expr.length() - 1;
    }

    private double eval(String expr) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expr.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }

    private SpannableStringBuilder formatWithSuperscript(String text) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        Pattern pattern = Pattern.compile("\\^\\(([^)]+)\\)|\\^(-?\\d+)|\\^([a-zA-Z])");
        Matcher matcher = pattern.matcher(text);

        int offset = 0;
        while (matcher.find()) {
            String exponent;
            int start = matcher.start() - offset;
            int end = matcher.end() - offset;

            if (matcher.group(1) != null) exponent = matcher.group(1);
            else if (matcher.group(2) != null) exponent = matcher.group(2);
            else exponent = matcher.group(3);

            builder.replace(start, end, exponent);
            builder.setSpan(new SuperscriptSpan(), start, start + exponent.length(),
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            builder.setSpan(new RelativeSizeSpan(0.7f), start, start + exponent.length(),
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);

            offset += (end - start) - exponent.length();
        }

        return builder;
    }

    // ======================== ADVANCED ALGEBRA ENGINE ========================
    class AlgebraEngine {

        public String simplify(String expr) {
            expr = normalizeExpression(expr);

            if (expr.contains("=")) {
                String[] parts = expr.split("=");
                if (parts.length == 2) {
                    return simplify(parts[0].trim()) + "=" + simplify(parts[1].trim());
                }
            }

            List<Term> terms = parseTerms(expr);
            Map<String, Double> combined = combineTerms(terms);
            return buildExpression(combined);
        }

        public String expand(String expr) {
            expr = normalizeExpression(expr);

            // Handle (a+b)(c+d)
            Pattern pattern = Pattern.compile("\\(([^)]+)\\)\\*?\\(([^)]+)\\)");
            Matcher matcher = pattern.matcher(expr);

            if (matcher.find()) {
                String first = matcher.group(1);
                String second = matcher.group(2);

                List<Term> firstTerms = parseTerms(first);
                List<Term> secondTerms = parseTerms(second);

                List<Term> result = new ArrayList<>();
                for (Term t1 : firstTerms) {
                    for (Term t2 : secondTerms) {
                        result.add(multiplyTerms(t1, t2));
                    }
                }

                Map<String, Double> combined = combineTerms(result);
                return buildExpression(combined);
            }

            // Handle x(a+b)
            pattern = Pattern.compile("([^(]+)\\(([^)]+)\\)");
            matcher = pattern.matcher(expr);

            if (matcher.find()) {
                String multiplier = matcher.group(1).trim();
                String inside = matcher.group(2);

                Term mult = parseSingleTerm(multiplier);
                List<Term> insideTerms = parseTerms(inside);

                List<Term> result = new ArrayList<>();
                for (Term t : insideTerms) {
                    result.add(multiplyTerms(mult, t));
                }

                Map<String, Double> combined = combineTerms(result);
                return buildExpression(combined);
            }

            return simplify(expr);
        }

        public String factor(String expr) {
            expr = normalizeExpression(expr);

            // Try different factoring strategies
            String result;

            // Strategy 1: Common factor extraction (GCF)
            result = factorCommonTerms(expr);
            if (!result.equals(expr)) {
                return result;
            }

            // Strategy 2: Quadratic factoring (trinomials)
            result = factorQuadratic(expr);
            if (result != null) {
                return result;
            }

            // Strategy 3: Difference of squares: a^2 - b^2 = (a+b)(a-b)
            result = factorDifferenceOfSquares(expr);
            if (result != null) {
                return result;
            }

            // Strategy 4: Perfect square trinomial: a^2 + 2ab + b^2 = (a+b)^2
            result = factorPerfectSquare(expr);
            if (result != null) {
                return result;
            }

            // Strategy 5: Difference/Sum of cubes
            result = factorCubes(expr);
            if (result != null) {
                return result;
            }

            // Strategy 6: Grouping method
            result = factorByGrouping(expr);
            if (result != null) {
                return result;
            }

            return expr; // Cannot factor further
        }

        // Strategy 1: Factor out common terms (GCF)
        private String factorCommonTerms(String expr) {
            List<Term> terms = parseTerms(expr);

            if (terms.size() < 2) return expr;

            // Find GCD of coefficients
            long gcd = findGCD(terms);

            // Find common variables with minimum powers
            Map<String, Integer> commonVars = findCommonVariables(terms);

            // If nothing common, return original
            if (gcd == 1 && commonVars.isEmpty()) {
                return expr;
            }

            // Build the common factor
            StringBuilder commonFactor = new StringBuilder();

            if (gcd != 1) {
                commonFactor.append(gcd);
            }

            List<String> sortedVars = new ArrayList<>(commonVars.keySet());
            Collections.sort(sortedVars);

            for (String var : sortedVars) {
                int power = commonVars.get(var);
                commonFactor.append(var);
                if (power > 1) {
                    commonFactor.append("^").append(power);
                }
            }

            // Divide each term by the common factor
            List<Term> factoredTerms = new ArrayList<>();
            for (Term t : terms) {
                Term newTerm = new Term();
                newTerm.coefficient = t.coefficient / gcd;
                newTerm.variables = new HashMap<>(t.variables);

                for (Map.Entry<String, Integer> entry : commonVars.entrySet()) {
                    String var = entry.getKey();
                    int power = newTerm.variables.getOrDefault(var, 0) - entry.getValue();

                    if (power > 0) {
                        newTerm.variables.put(var, power);
                    } else {
                        newTerm.variables.remove(var);
                    }
                }

                factoredTerms.add(newTerm);
            }

            Map<String, Double> combined = combineTerms(factoredTerms);
            String innerExpr = buildExpression(combined);

            // If inner expression is 1, just return the factor
            if (innerExpr.equals("1")) {
                return commonFactor.toString();
            }

            return commonFactor.toString() + "(" + innerExpr + ")";
        }

        // Strategy 2: Factor quadratics ax^2 + bx + c
        private String factorQuadratic(String expr) {
            List<Term> terms = parseTerms(expr);
            Map<String, Double> combined = combineTerms(terms);

            if (combined.size() < 2 || combined.size() > 3) {
                return null;
            }

            // Extract coefficients for ax^2 + bx + c
            String variable = null;
            double a = 0, b = 0, c = 0;

            for (Map.Entry<String, Double> entry : combined.entrySet()) {
                String key = entry.getKey();
                double coeff = entry.getValue();

                if (key.contains("^2")) {
                    // Extract variable name
                    Pattern p = Pattern.compile("([a-zA-Z])");
                    Matcher m = p.matcher(key);
                    if (m.find()) {
                        variable = m.group(1);
                        a = coeff;
                    }
                } else if (!key.isEmpty() && key.matches(".*[a-zA-Z].*") && !key.contains("^")) {
                    Pattern p = Pattern.compile("([a-zA-Z])");
                    Matcher m = p.matcher(key);
                    if (m.find()) {
                        if (variable == null) variable = m.group(1);
                        b = coeff;
                    }
                } else if (key.isEmpty()) {
                    c = coeff;
                }
            }

            if (variable == null || a == 0) {
                return null;
            }

            // Convert to integers for factoring
            long aInt = Math.round(a);
            long bInt = Math.round(b);
            long cInt = Math.round(c);

            if (Math.abs(a - aInt) > 0.001 || Math.abs(b - bInt) > 0.001 || Math.abs(c - cInt) > 0.001) {
                return null; // Non-integer coefficients
            }

            // Case 1: a = 1, factor x^2 + bx + c
            if (aInt == 1) {
                return factorSimpleQuadratic(variable, bInt, cInt);
            }

            // Case 2: a != 1, use AC method
            return factorComplexQuadratic(variable, aInt, bInt, cInt);
        }

        private String factorSimpleQuadratic(String var, long b, long c) {
            // Find two numbers that multiply to c and add to b
            for (long i = -Math.abs(c); i <= Math.abs(c); i++) {
                if (i == 0) continue;
                if (c % i == 0) {
                    long j = c / i;
                    if (i + j == b) {
                        return formatBinomial(var, 1, i) + formatBinomial(var, 1, j);
                    }
                }
            }
            return null;
        }

        private String factorComplexQuadratic(String var, long a, long b, long c) {
            // AC method: find two numbers that multiply to a*c and add to b
            long ac = a * c;

            for (long i = -Math.abs(ac); i <= Math.abs(ac); i++) {
                if (i == 0) continue;
                if (ac % i == 0) {
                    long j = ac / i;
                    if (i + j == b) {
                        // Try simpler approach: check if it factors nicely
                        for (long p = -Math.abs(a); p <= Math.abs(a); p++) {
                            if (p == 0) continue;
                            if (a % p == 0) {
                                long q = a / p;
                                for (long r = -Math.abs(c); r <= Math.abs(c); r++) {
                                    if (r == 0) continue;
                                    if (c % r == 0) {
                                        long s = c / r;
                                        if (p * s + q * r == b) {
                                            return formatBinomial(var, p, r) + formatBinomial(var, q, s);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        private String formatBinomial(String var, long coeff, long constant) {
            StringBuilder sb = new StringBuilder("(");

            if (coeff == 1) {
                sb.append(var);
            } else if (coeff == -1) {
                sb.append("-").append(var);
            } else {
                sb.append(coeff).append(var);
            }

            if (constant > 0) {
                sb.append("+").append(constant);
            } else if (constant < 0) {
                sb.append(constant);
            }

            sb.append(")");
            return sb.toString();
        }

        // Strategy 3: Difference of squares a^2 - b^2 = (a+b)(a-b)
        private String factorDifferenceOfSquares(String expr) {
            List<Term> terms = parseTerms(expr);
            Map<String, Double> combined = combineTerms(terms);

            if (combined.size() != 2) return null;

            // Look for pattern: a^2 - b^2
            List<Map.Entry<String, Double>> entries = new ArrayList<>(combined.entrySet());

            String key1 = entries.get(0).getKey();
            double coeff1 = entries.get(0).getValue();
            String key2 = entries.get(1).getKey();
            double coeff2 = entries.get(1).getValue();

            // Check if both are perfect squares and opposite signs
            if (coeff1 * coeff2 >= 0) return null; // Same signs, not difference

            // Determine which is positive
            String posKey = coeff1 > 0 ? key1 : key2;
            String negKey = coeff1 < 0 ? key1 : key2;
            double posCoeff = coeff1 > 0 ? coeff1 : coeff2;
            double negCoeff = coeff1 < 0 ? coeff1 : coeff2;

            // Check if coefficients are perfect squares
            double sqrtPos = Math.sqrt(Math.abs(posCoeff));
            double sqrtNeg = Math.sqrt(Math.abs(negCoeff));

            if (sqrtPos != Math.floor(sqrtPos) || sqrtNeg != Math.floor(sqrtNeg)) {
                return null;
            }

            // Check if variable parts are perfect squares
            String aStr = extractSquareRoot(posKey, (long)sqrtPos);
            String bStr = extractSquareRoot(negKey, (long)sqrtNeg);

            if (aStr == null || bStr == null) return null;

            return "(" + aStr + "+" + bStr + ")(" + aStr + "-" + bStr + ")";
        }

        private String extractSquareRoot(String key, long coeff) {
            if (key.isEmpty()) {
                return String.valueOf(coeff);
            }

            // Parse variable powers
            Map<String, Integer> vars = new HashMap<>();
            Pattern p = Pattern.compile("([a-zA-Z])(?:\\^(\\d+))?");
            Matcher m = p.matcher(key);

            while (m.find()) {
                String var = m.group(1);
                int power = m.group(2) != null ? Integer.parseInt(m.group(2)) : 1;

                if (power % 2 != 0) return null; // Not a perfect square

                vars.put(var, power / 2);
            }

            StringBuilder result = new StringBuilder();
            if (coeff != 1) {
                result.append(coeff);
            }

            for (Map.Entry<String, Integer> entry : vars.entrySet()) {
                result.append(entry.getKey());
                if (entry.getValue() > 1) {
                    result.append("^").append(entry.getValue());
                }
            }

            return result.toString();
        }

        // Strategy 4: Perfect square trinomial a^2 ± 2ab + b^2 = (a±b)^2
        private String factorPerfectSquare(String expr) {
            List<Term> terms = parseTerms(expr);
            Map<String, Double> combined = combineTerms(terms);

            if (combined.size() != 3) return null;

            // Find terms: should have x^2, x, and constant
            String variable = null;
            double a = 0, b = 0, c = 0;

            for (Map.Entry<String, Double> entry : combined.entrySet()) {
                String key = entry.getKey();
                double coeff = entry.getValue();

                if (key.contains("^2")) {
                    Pattern p = Pattern.compile("([a-zA-Z])");
                    Matcher m = p.matcher(key);
                    if (m.find()) {
                        variable = m.group(1);
                        a = coeff;
                    }
                } else if (!key.isEmpty() && key.matches(".*[a-zA-Z].*")) {
                    b = coeff;
                } else if (key.isEmpty()) {
                    c = coeff;
                }
            }

            if (variable == null || a == 0) return null;

            // Check if a and c are perfect squares
            double sqrtA = Math.sqrt(Math.abs(a));
            double sqrtC = Math.sqrt(Math.abs(c));

            if (sqrtA != Math.floor(sqrtA) || sqrtC != Math.floor(sqrtC)) {
                return null;
            }

            // Check if b = ±2*sqrt(a)*sqrt(c)
            double expectedB = 2 * sqrtA * sqrtC;

            if (Math.abs(b - expectedB) < 0.001) {
                // Positive perfect square
                return "(" + formatTerm((long)sqrtA, variable) + "+" + (long)sqrtC + ")^2";
            } else if (Math.abs(b + expectedB) < 0.001) {
                // Negative perfect square
                return "(" + formatTerm((long)sqrtA, variable) + "-" + (long)sqrtC + ")^2";
            }

            return null;
        }

        private String formatTerm(long coeff, String var) {
            if (coeff == 1) return var;
            if (coeff == -1) return "-" + var;
            return coeff + var;
        }

        // Strategy 5: Sum/Difference of cubes
        private String factorCubes(String expr) {
            List<Term> terms = parseTerms(expr);
            Map<String, Double> combined = combineTerms(terms);

            if (combined.size() != 2) return null;

            List<Map.Entry<String, Double>> entries = new ArrayList<>(combined.entrySet());

            String key1 = entries.get(0).getKey();
            double coeff1 = entries.get(0).getValue();
            String key2 = entries.get(1).getKey();
            double coeff2 = entries.get(1).getValue();

            // Check for cubes (power of 3)
            boolean isCube1 = key1.contains("^3");
            boolean isCube2 = key2.contains("^3");
            boolean isConst1 = key1.isEmpty();
            boolean isConst2 = key2.isEmpty();

            if (!((isCube1 && isConst2) || (isCube2 && isConst1))) {
                return null;
            }

            String cubeKey = isCube1 ? key1 : key2;
            double cubeCoeff = isCube1 ? coeff1 : coeff2;
            double constCoeff = isConst1 ? coeff1 : coeff2;

            // Extract cube roots
            double cbrtCube = Math.cbrt(Math.abs(cubeCoeff));
            double cbrtConst = Math.cbrt(Math.abs(constCoeff));

            if (cbrtCube != Math.floor(cbrtCube) || cbrtConst != Math.floor(cbrtConst)) {
                return null;
            }

            String var = cubeKey.replace("^3", "");
            String a = formatCoeffVar((long)cbrtCube, var);
            String b = String.valueOf((long)cbrtConst);

            if (cubeCoeff * constCoeff > 0) {
                // Sum of cubes: a^3 + b^3 = (a+b)(a^2-ab+b^2)
                return "(" + a + "+" + b + ")(" + a + "^2-" + a + "*" + b + "+" + b + "^2)";
            } else {
                // Difference of cubes: a^3 - b^3 = (a-b)(a^2+ab+b^2)
                return "(" + a + "-" + b + ")(" + a + "^2+" + a + "*" + b + "+" + b + "^2)";
            }
        }

        private String formatCoeffVar(long coeff, String var) {
            if (coeff == 1) return var;
            if (coeff == -1) return "-" + var;
            return coeff + var;
        }

        // Strategy 6: Factor by grouping
        private String factorByGrouping(String expr) {
            List<Term> terms = parseTerms(expr);

            if (terms.size() != 4) return null;

            // Try grouping first two and last two terms
            List<Term> group1 = Arrays.asList(terms.get(0), terms.get(1));
            List<Term> group2 = Arrays.asList(terms.get(2), terms.get(3));

            String factored1 = factorCommonTerms(buildExpressionFromTerms(group1));
            String factored2 = factorCommonTerms(buildExpressionFromTerms(group2));

            // Check if both groups have the same factor in parentheses
            Pattern p = Pattern.compile("(.*)\\((.+)\\)");
            Matcher m1 = p.matcher(factored1);
            Matcher m2 = p.matcher(factored2);

            if (m1.find() && m2.find()) {
                String common1 = m1.group(2);
                String common2 = m2.group(2);

                if (common1.equals(common2)) {
                    String coeff1 = m1.group(1);
                    String coeff2 = m2.group(1);
                    return "(" + common1 + ")(" + coeff1 + "+" + coeff2 + ")";
                }
            }

            return null;
        }

        private String buildExpressionFromTerms(List<Term> terms) {
            Map<String, Double> combined = combineTerms(terms);
            return buildExpression(combined);
        }

        public String solve(String expr) {
            if (!expr.contains("=")) {
                throw new RuntimeException("Equation must contain '='");
            }

            String[] parts = expr.split("=");
            if (parts.length != 2) {
                throw new RuntimeException("Invalid equation format");
            }

            String left = normalizeExpression(parts[0].trim());
            String right = normalizeExpression(parts[1].trim());

            List<Term> leftTerms = parseTerms(left);
            List<Term> rightTerms = parseTerms(right);

            for (Term t : rightTerms) {
                t.coefficient = -t.coefficient;
            }

            leftTerms.addAll(rightTerms);
            Map<String, Double> combined = combineTerms(leftTerms);

            String variable = null;
            for (String key : combined.keySet()) {
                if (!key.isEmpty()) {
                    Pattern pattern = Pattern.compile("[a-zA-Z]+");
                    Matcher matcher = pattern.matcher(key);
                    if (matcher.find()) {
                        variable = matcher.group();
                        break;
                    }
                }
            }

            if (variable == null) {
                throw new RuntimeException("No variable found");
            }

            double a = 0, b = 0;
            for (Map.Entry<String, Double> entry : combined.entrySet()) {
                if (entry.getKey().contains(variable) && !entry.getKey().contains("^")) {
                    a = entry.getValue();
                } else if (entry.getKey().isEmpty()) {
                    b = entry.getValue();
                }
            }

            if (a == 0) {
                return solveQuadratic(combined, variable);
            }

            double solution = -b / a;
            return variable + "=" + formatNumber(solution);
        }

        private String solveQuadratic(Map<String, Double> combined, String variable) {
            double a = 0, b = 0, c = 0;

            for (Map.Entry<String, Double> entry : combined.entrySet()) {
                String key = entry.getKey();
                if (key.contains(variable + "^2")) {
                    a = entry.getValue();
                } else if (key.contains(variable) && !key.contains("^")) {
                    b = entry.getValue();
                } else if (key.isEmpty()) {
                    c = entry.getValue();
                }
            }

            if (a == 0) {
                throw new RuntimeException("Cannot solve this equation");
            }

            double discriminant = b * b - 4 * a * c;

            if (discriminant < 0) {
                return "No real solutions";
            } else if (discriminant == 0) {
                double x = -b / (2 * a);
                return variable + "=" + formatNumber(x);
            } else {
                double x1 = (-b + Math.sqrt(discriminant)) / (2 * a);
                double x2 = (-b - Math.sqrt(discriminant)) / (2 * a);
                return variable + "=" + formatNumber(x1) + " or " + variable + "=" + formatNumber(x2);
            }
        }

        public String evaluate(String expr, Map<String, Double> values) {
            expr = normalizeExpression(expr);

            for (Map.Entry<String, Double> entry : values.entrySet()) {
                expr = expr.replaceAll(entry.getKey(), "(" + entry.getValue() + ")");
            }

            return evaluateNumeric(expr);
        }

        public String derivative(String expr, String variable) {
            expr = normalizeExpression(expr);

            // Parse the expression into terms
            List<Term> terms = parseTerms(expr);
            List<Term> derivativeTerms = new ArrayList<>();

            for (Term term : terms) {
                Term derivTerm = differentiateTerm(term, variable);
                if (derivTerm != null) {
                    derivativeTerms.add(derivTerm);
                }
            }

            if (derivativeTerms.isEmpty()) {
                return "0";
            }

            Map<String, Double> combined = combineTerms(derivativeTerms);
            return buildExpression(combined);
        }

        private Term differentiateTerm(Term term, String variable) {
            // If the term doesn't contain the variable, derivative is 0
            if (!term.variables.containsKey(variable)) {
                return null;
            }

            int power = term.variables.get(variable);

            // Power rule: d/dx(ax^n) = n*a*x^(n-1)
            Term result = new Term();
            result.coefficient = term.coefficient * power;
            result.variables = new HashMap<>(term.variables);

            // Reduce the power by 1
            if (power == 1) {
                result.variables.remove(variable);
            } else {
                result.variables.put(variable, power - 1);
            }

            return result;
        }

        public String integrate(String expr, String variable) {
            expr = normalizeExpression(expr);

            // Parse the expression into terms
            List<Term> terms = parseTerms(expr);
            List<Term> integralTerms = new ArrayList<>();

            for (Term term : terms) {
                Term intTerm = integrateTerm(term, variable);
                if (intTerm != null) {
                    integralTerms.add(intTerm);
                } else {
                    throw new RuntimeException("Cannot integrate this expression");
                }
            }

            Map<String, Double> combined = combineTerms(integralTerms);
            return buildExpression(combined);
        }

        private Term integrateTerm(Term term, String variable) {
            // Power rule for integration: ∫ax^n dx = a*x^(n+1)/(n+1)
            Term result = new Term();
            result.variables = new HashMap<>(term.variables);

            int currentPower = term.variables.getOrDefault(variable, 0);
            int newPower = currentPower + 1;

            // Check for special case: ∫1/x dx (would need logarithm)
            if (newPower == 0) {
                throw new RuntimeException("Integration of 1/x requires logarithm (not supported)");
            }

            result.coefficient = term.coefficient / newPower;
            result.variables.put(variable, newPower);

            return result;
        }

        private String normalizeExpression(String expr) {
            return expr.replace("×", "*").replace("÷", "/")
                    .replace("−", "-").replaceAll("\\s+", "");
        }

        private List<Term> parseTerms(String expr) {
            List<Term> terms = new ArrayList<>();
            List<String> parts = splitByAddSub(expr);

            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    terms.add(parseSingleTerm(part.trim()));
                }
            }

            return terms;
        }

        private Term parseSingleTerm(String str) {
            Term term = new Term();
            term.variables = new HashMap<>();

            Pattern coeffPattern = Pattern.compile("^([+-]?\\d*\\.?\\d*)(.*)$");
            Matcher matcher = coeffPattern.matcher(str);

            if (matcher.find()) {
                String coeffStr = matcher.group(1);
                String rest = matcher.group(2);

                if (coeffStr.isEmpty() || coeffStr.equals("+")) {
                    term.coefficient = 1.0;
                } else if (coeffStr.equals("-")) {
                    term.coefficient = -1.0;
                } else {
                    term.coefficient = Double.parseDouble(coeffStr);
                }

                if (!rest.isEmpty()) {
                    parseVariables(rest, term);
                }
            }

            return term;
        }

        private void parseVariables(String str, Term term) {
            Pattern varPattern = Pattern.compile("([a-zA-Z])(?:\\^(\\d+))?");
            Matcher matcher = varPattern.matcher(str);

            while (matcher.find()) {
                String var = matcher.group(1);
                String powerStr = matcher.group(2);
                int power = powerStr != null ? Integer.parseInt(powerStr) : 1;

                term.variables.put(var, term.variables.getOrDefault(var, 0) + power);
            }
        }

        private List<String> splitByAddSub(String expr) {
            List<String> parts = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            int parenDepth = 0;

            for (int i = 0; i < expr.length(); i++) {
                char c = expr.charAt(i);

                if (c == '(') parenDepth++;
                if (c == ')') parenDepth--;

                if (parenDepth == 0 && (c == '+' || c == '-') && i > 0) {
                    parts.add(current.toString());
                    current = new StringBuilder();
                    if (c == '-') current.append('-');
                } else {
                    current.append(c);
                }
            }

            parts.add(current.toString());
            return parts;
        }

        private Map<String, Double> combineTerms(List<Term> terms) {
            Map<String, Double> combined = new HashMap<>();

            for (Term term : terms) {
                String key = buildVariableKey(term.variables);
                combined.put(key, combined.getOrDefault(key, 0.0) + term.coefficient);
            }

            return combined;
        }

        private String buildVariableKey(Map<String, Integer> variables) {
            if (variables.isEmpty()) return "";

            StringBuilder key = new StringBuilder();
            List<String> vars = new ArrayList<>(variables.keySet());
            Collections.sort(vars);

            for (String var : vars) {
                int power = variables.get(var);
                if (power > 0) {
                    key.append(var);
                    if (power > 1) {
                        key.append("^").append(power);
                    }
                }
            }

            return key.toString();
        }

        private String buildExpression(Map<String, Double> combined) {
            if (combined.isEmpty()) return "0";

            StringBuilder result = new StringBuilder();
            List<String> keys = new ArrayList<>(combined.keySet());

            Collections.sort(keys, (a, b) -> {
                if (a.isEmpty() && !b.isEmpty()) return 1;
                if (!a.isEmpty() && b.isEmpty()) return -1;
                return compareVariableKeys(b, a);
            });

            boolean first = true;
            for (String key : keys) {
                double coeff = combined.get(key);
                if (Math.abs(coeff) < 0.0000001) continue;

                if (!first && coeff > 0) {
                    result.append("+");
                }

                if (key.isEmpty()) {
                    result.append(formatCoefficient(coeff));
                } else {
                    if (coeff == 1.0) {
                        result.append(key);
                    } else if (coeff == -1.0) {
                        result.append("-").append(key);
                    } else {
                        result.append(formatCoefficient(coeff)).append(key);
                    }
                }

                first = false;
            }

            return result.length() > 0 ? result.toString() : "0";
        }

        private int compareVariableKeys(String a, String b) {
            int powerA = extractMaxPower(a);
            int powerB = extractMaxPower(b);

            if (powerA != powerB) {
                return Integer.compare(powerA, powerB);
            }

            return a.compareTo(b);
        }

        private int extractMaxPower(String key) {
            Pattern p = Pattern.compile("\\^(\\d+)");
            Matcher m = p.matcher(key);
            int maxPower = 0;

            while (m.find()) {
                int power = Integer.parseInt(m.group(1));
                maxPower = Math.max(maxPower, power);
            }

            return maxPower > 0 ? maxPower : (key.isEmpty() ? 0 : 1);
        }

        private Term multiplyTerms(Term t1, Term t2) {
            Term result = new Term();
            result.coefficient = t1.coefficient * t2.coefficient;
            result.variables = new HashMap<>(t1.variables);

            for (Map.Entry<String, Integer> entry : t2.variables.entrySet()) {
                String var = entry.getKey();
                int power = entry.getValue();
                result.variables.put(var, result.variables.getOrDefault(var, 0) + power);
            }

            return result;
        }

        private long findGCD(List<Term> terms) {
            if (terms.isEmpty()) return 1;

            boolean allIntegers = true;
            for (Term t : terms) {
                if (Math.abs(t.coefficient - Math.round(t.coefficient)) > 0.001) {
                    allIntegers = false;
                    break;
                }
            }

            if (!allIntegers) return 1;

            long gcd = Math.abs(Math.round(terms.get(0).coefficient));
            for (int i = 1; i < terms.size(); i++) {
                gcd = gcdLong(gcd, Math.abs(Math.round(terms.get(i).coefficient)));
                if (gcd == 1) break;
            }

            return gcd;
        }

        private long gcdLong(long a, long b) {
            while (b != 0) {
                long temp = b;
                b = a % b;
                a = temp;
            }
            return a;
        }

        private Map<String, Integer> findCommonVariables(List<Term> terms) {
            if (terms.isEmpty()) return new HashMap<>();

            Map<String, Integer> common = new HashMap<>(terms.get(0).variables);

            for (int i = 1; i < terms.size(); i++) {
                Map<String, Integer> current = terms.get(i).variables;
                Iterator<Map.Entry<String, Integer>> iterator = common.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, Integer> entry = iterator.next();
                    String var = entry.getKey();

                    if (!current.containsKey(var)) {
                        iterator.remove();
                    } else {
                        int minPower = Math.min(entry.getValue(), current.get(var));
                        entry.setValue(minPower);
                    }
                }
            }

            return common;
        }

        private String formatCoefficient(double coeff) {
            if (coeff == (long) coeff) {
                return String.valueOf((long) coeff);
            }
            return String.valueOf(coeff);
        }

        private String formatNumber(double num) {
            if (num == (long) num) {
                return String.valueOf((long) num);
            }
            return String.format("%.6f", num).replaceAll("0*$", "").replaceAll("\\.$", "");
        }

        class Term {
            double coefficient;
            Map<String, Integer> variables;

            Term() {
                coefficient = 1.0;
                variables = new HashMap<>();
            }
        }
    }
}