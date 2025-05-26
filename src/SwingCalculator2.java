import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SwingCalculator2 extends JFrame {
    private final JTextField inputField;
    private final JTextArea outputArea;
    private final StringBuilder inputText;

    public SwingCalculator2() {
        setTitle("Калькулятор");
        setSize(350, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        inputField = new JTextField();
        add(inputField, BorderLayout.NORTH);
        inputField.setBackground(Color.ORANGE);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
        outputArea.setBackground(Color.ORANGE);

        inputText = new StringBuilder();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 4));

        String[] buttons = {
                "C", "", "", "DEL",
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "√", "x²", "x^y", "!"
        };

        for (String label : buttons) {
            JButton button = new JButton(label);
            button.setBackground(Color.ORANGE);
            button.addActionListener(e -> buttonClicked(label));
            buttonPanel.add(button);
        }
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void buttonClicked(String command) {
        switch (command) {
            case "C" -> {
                inputText.setLength(0);
                inputField.setText("");
            }
            case "DEL" -> {
                String text = inputField.getText();
                if (!text.isEmpty()) {
                    inputField.setText(text.substring(0, text.length() - 1));
                }
            }
            case "=" -> calculateResult();
            case "√" -> inputText.append("√");
            case "x²" -> inputText.append("²");
            case "x^y" -> inputText.append("^");
            case "!" -> inputText.append("!");
            default -> inputText.append(command);
        }
        inputField.setText(inputText.toString());
    }

    private void calculateResult() {
        try {
            String input = inputField.getText();
            double result = evaluateExpression(input);
            outputArea.setText(input + " = " + result);
        } catch (Exception e) {
            outputArea.setText("Ошибка: " + e.getMessage());
        }
    }

    private double evaluateExpression(String expression) {
        String[] tokens = expression.split("(?<=[-+*/^√!()²])|(?=[-+*/^√!()²])");
        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<String> operators = new ArrayList<>();

        for (String token : tokens) {
            if (token.isEmpty()) continue;

            try {
                numbers.add(Double.parseDouble(token));
            } catch (NumberFormatException e) {
                operators.add(token);
            }
        }

        for (int i = 0; i < operators.size(); i++) {
            String op = operators.get(i);
            switch (op) {
                case "²" -> {
                    if (i >= numbers.size()) continue;
                    double num = numbers.get(i);
                    numbers.set(i, num * num);
                    operators.remove(i);
                    i--;
                }
                case "√" -> {
                    if (i >= numbers.size()) continue;
                    double num = numbers.get(i);
                    numbers.set(i, Math.sqrt(num));
                    operators.remove(i);
                    i--;
                }
                case "!" -> {
                    if (i >= numbers.size()) continue;
                    int num = numbers.get(i).intValue();
                    numbers.set(i, (double) factorial(num));
                    operators.remove(i);
                    i--;
                }
            }
        }

        for (int i = 0; i < operators.size(); i++) {
            String op = operators.get(i);
            if (op.equals("*") || op.equals("/") || op.equals("^")) {
                if (i + 1 >= numbers.size()) continue;
                double left = numbers.get(i);
                double right = numbers.get(i + 1);
                double res;

                switch (op) {
                    case "*":
                        res = left * right;
                        break;
                    case "/":
                        if (right == 0) throw new ArithmeticException("Деление на ноль");
                        res = left / right;
                        break;
                    case "^":
                        res = Math.pow(left, right);
                        break;
                    default:
                        continue;
                }

                numbers.set(i, res);
                numbers.remove(i + 1);
                operators.remove(i);
                i--;
            }
        }

        double result = numbers.isEmpty() ? 0 : numbers.get(0);
        for (int i = 0; i < operators.size(); i++) {
            if (i + 1 >= numbers.size()) continue;
            String operator = operators.get(i);
            double nextNum = numbers.get(i + 1);

            switch (operator) {
                case "+":
                    result += nextNum;
                    break;
                case "-":
                    result -= nextNum;
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестный оператор: " + operator);
            }
        }
        return result;
    }

    private long factorial(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingCalculator2 calculator = new SwingCalculator2();
            calculator.setVisible(true);
        });
    }
}