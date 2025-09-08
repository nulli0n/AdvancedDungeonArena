package su.nightexpress.dungeons.util;

import java.util.*;
import java.util.function.Predicate;

public class PredicateParser {

    private final Map<String, Predicate<Object>> registry = new HashMap<>();

    public void register(String name, Predicate<Object> predicate) {
        registry.put(name, predicate);
    }

    public Predicate<Object> parse(String expr) {
        // Tokenize by space or operator (keeps operators as separate tokens)
        List<String> tokens = tokenize(expr);

        // --- Shunting-yard: infix → postfix ---
        List<String> output = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();

        for (String token : tokens) {
            switch (token) {
                case "&&":
                case "||":
                case "!":
                    while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(token)) {
                        output.add(ops.pop());
                    }
                    ops.push(token);
                    break;
                case "(":
                    ops.push(token);
                    break;
                case ")":
                    while (!ops.isEmpty() && !ops.peek().equals("(")) {
                        output.add(ops.pop());
                    }
                    ops.pop(); // discard "("
                    break;
                default:
                    output.add(token); // predicate name
            }
        }
        while (!ops.isEmpty()) {
            output.add(ops.pop());
        }

        // --- Evaluate postfix ---
        Deque<Predicate<Object>> stack = new ArrayDeque<>();
        for (String token : output) {
            switch (token) {
                case "&&": {
                    Predicate<Object> right = stack.pop();
                    Predicate<Object> left = stack.pop();
                    stack.push(left.and(right));
                    break;
                }
                case "||": {
                    Predicate<Object> right = stack.pop();
                    Predicate<Object> left = stack.pop();
                    stack.push(left.or(right));
                    break;
                }
                case "!": {
                    Predicate<Object> operand = stack.pop();
                    stack.push(operand.negate());
                    break;
                }
                default:
                    Predicate<Object> pred = registry.get(token);
                    if (pred == null) {
                        throw new IllegalArgumentException("Unknown predicate: " + token);
                    }
                    stack.push(pred);
            }
        }
        return stack.pop();
    }

    private int precedence(String op) {
        return switch (op) {
            case "!" -> 3;
            case "&&" -> 2;
            case "||" -> 1;
            default -> 0;
        };
    }

    private List<String> tokenize(String expr) {
        // Split while keeping operators and parentheses
        return Arrays.asList(expr
            .replace("(", " ( ")
            .replace(")", " ) ")
            .replace("&&", " && ")
            .replace("||", " || ")
            .replace("!", " ! ")
            .trim()
            .split("\\s+"));
    }
}
