package lab1;
import java.util.*;

public class Interpreter
{
    private final List<Token> infixExpr;
    private final Map<String, Object> variables = new HashMap<>();

    private int iterator;
    private Token currentToken;
    private boolean transCondition;

    private int operationPriority(Token op)
    {
        return switch (op.getValue())
        {
            case "(" -> 0;
            case "+", "-" -> 1;
            case "*", "/" -> 2;
            default -> throw new IllegalArgumentException("Illegal value: " + op.getValue());
        };
    }

    private double execute(Token op, double first, double second)
    {
        return switch (op.getValue())
        {
            case "+" -> first + second;
            case "-" -> first - second;
            case "*" -> first * second;
            case "/" -> first / second;
            default -> throw new IllegalArgumentException("Illegal value: " + op.getValue());
        };
    }

    private boolean compare(Token comp, double first, double second)
    {
        return switch (comp.getValue())
        {
            case ">" -> Double.compare(first, second) == 1;
            case "~" -> Double.compare(first, second) == 0;
            case "<" -> Double.compare(first, second) == -1;
            case "!=" -> Double.compare(first, second) != 0;
            default -> throw new IllegalArgumentException("Illegal value: " + comp.getValue());
        };
    }

    public Interpreter(List<Token> infixExpr)
    {
        this.infixExpr = infixExpr;
        currentToken = infixExpr.get(0);
        iterator = 0;
        transCondition = false;
        run();
    }

    private void run()
    {
        for (; iterator < infixExpr.size(); iterator++)
        {
            currentToken = infixExpr.get(iterator);
            switch (currentToken.getType())
            {
                case "ASSIGN_OP" -> valueTranslate("ENDL");
                case "IF" -> ifTranslate();
                case "WHILE" -> whileTranslate();
                case "DO" -> DoTranslate();
                case "FOR" -> forTranslate();
                case "PRINTF" -> printfTranslate();
            }
        }
    }

    private void ifTranslate()
    {
        interpret_condition();
        if (!transCondition)
        {
            while (!"ENDL".equals(currentToken.getType()))
            {
                if ("ELSE".equals(currentToken.getType()))
                {
                    break;
                }
                iterator++;
                currentToken = infixExpr.get(iterator);
            }
        }
        else
        {
            iterator++;
            valueTranslate("ELSE");
            while (!"ENDL".equals(currentToken.getType()))
            {
                iterator++;
                currentToken = infixExpr.get(iterator);
            }
        }
    }

    private void whileTranslate()
    {
        int start_iteration = iterator;
        interpret_condition();

        while (transCondition)
        {
            iterator++;
            valueTranslate("ENDL");
            iterator = start_iteration;
            currentToken = infixExpr.get(iterator);
            interpret_condition();
        }

        while (!"ENDL".equals(currentToken.getType()))
        {
            iterator++;
            currentToken = infixExpr.get(iterator);
        }
    }

    private void DoTranslate()
    {
        iterator += 2;
        currentToken = infixExpr.get(iterator);
        int start_iteration = iterator;

        do
        {
            valueTranslate("WHILE");
            interpret_condition();
            iterator = start_iteration;
            currentToken = infixExpr.get(iterator);
        } while (transCondition);

        while (!"ENDL".equals(currentToken.getType()))
        {
            iterator++;
            currentToken = infixExpr.get(iterator);
        }
    }

    private void forTranslate()
    {
        iterator += 3;
        currentToken = infixExpr.get(iterator);
        valueTranslate("DIV");

        iterator--;
        int condition = iterator;
        interpret_condition();

        int indexAfterFor = iterator + 1;
        while (transCondition)
        {
            while (!"R_BC".equals(currentToken.getType()))
            {
                iterator++;
                currentToken = infixExpr.get(iterator);
            }
            iterator += 2;
            currentToken = infixExpr.get(iterator);
            valueTranslate("ENDL");

            iterator = indexAfterFor;
            currentToken = infixExpr.get(iterator);
            valueTranslate("R_BC");

            iterator = condition;
            interpret_condition();
        }

        while (!"ENDL".equals(currentToken.getType()))
        {
            iterator++;
            currentToken = infixExpr.get(iterator);
        }
    }

    private void printfTranslate()
    {
        iterator++;
        currentToken = infixExpr.get(iterator);
        if ("L_BC".equals(currentToken.getType()))
        {
            Token c = infixExpr.get(iterator + 1);
            switch (c.getType()) {
                case "DIGIT" -> System.out.println(Double.parseDouble(c.getValue()));
                case "VAR" -> System.out.println(variables.get(c.getValue()));
            }
            iterator += 2;
        }
        else
        {
            System.out.println(variables);
        }
    }

    private void valueTranslate(String trans)
    {
        int indexVar = iterator - 1;
        int startExpr = iterator + 1;
        while (!trans.equals(currentToken.getType()))
        {
            if ("DIV".equals(currentToken.getType()))
            {
                double rez = calc(convertToPostfix(infixExpr, startExpr, iterator));
                variables.put(infixExpr.get(indexVar).getValue(), rez);
                indexVar = iterator + 1;
                startExpr = iterator + 3;
            }
            iterator++;
            currentToken = infixExpr.get(iterator);
        }

        double rez = calc(convertToPostfix(infixExpr, startExpr, iterator));
        variables.put(infixExpr.get(indexVar).getValue(), rez);
    }

    private void interpret_condition()
    {
        int first_argument_index = iterator + 2;
        int comparison_op_index = iterator + 3;
        int second_argument_index = iterator + 4;
        Token s = infixExpr.get(second_argument_index);
        double first = (double) variables.get(infixExpr.get(first_argument_index).getValue());
        double second = switch (s.getType())
        {
            case "DIGIT" -> Double.parseDouble(s.getValue());
            case "VAR" -> (double) variables.get(s.getValue());
            default ->  0.0;
        };

        iterator += 6;
        currentToken = infixExpr.get(iterator);

        transCondition = compare(infixExpr.get(comparison_op_index), first, second);
    }

    private List<Token> convertToPostfix(List<Token> infixExpr, int start, int end)
    {
        List<Token> postfixExpr = new LinkedList<>();
        Stack<Token> stack = new Stack<>();
        for (int i = start; i < end; i++)
        {
            Token current = infixExpr.get(i);
            switch (current.getType())
            {
                case "DIGIT", "VAR" -> postfixExpr.add(current);
                case "L_BC" -> stack.push(current);
                case "R_BC" ->
                {
                    while (stack.size() > 0 && !"L_BC".equals(stack.peek().getType()))
                        postfixExpr.add(stack.pop());
                    stack.pop();
                }
                case "OP" ->
                {
                    while (stack.size() > 0 && (operationPriority(stack.peek()) >= operationPriority(current)))
                    {
                        postfixExpr.add(stack.pop());
                    }
                    stack.push(current);
                }
            }
        }

        while (!stack.isEmpty())
        {
            postfixExpr.add(stack.pop());
        }

        return postfixExpr;
    }

    private double calc(List<Token> postfixExpr)
    {
        Stack<Double> locals = new Stack<>();

        for (Token c : postfixExpr)
        {
            switch (c.getType())
            {
                case "DIGIT" -> locals.push(Double.parseDouble(c.getValue()));
                case "VAR" -> locals.push((double) variables.get(c.getValue()));
                case "OP" ->
                {
                    double second = locals.size() > 0 ? locals.pop() : 0,
                    first = locals.size() > 0 ? locals.pop() : 0;
                    locals.push(execute(c, first, second));
                }
            }
        }

        return locals.pop();
    }

    public Map<String, Object> getVariables()
    {
        return variables;
    }

    @Override
    public String toString()
    {
        return "Res" + variables;
    }
}