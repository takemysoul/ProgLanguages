package lab1;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer
{
    private static List<Token> tokens = new LinkedList<>();

    private final static Map<String, Pattern> lexems = new HashMap<>();
    static
    {
        lexems.put("VAR", Pattern.compile("(^[a-zA-Z\\_][0-9a-zA-Z\\_]*$)"));
        lexems.put("DIGIT", Pattern.compile("^0|([1-9][0-9]*)$"));
        lexems.put("OP", Pattern.compile("^([%+*\\-\\//]|(\\**)|(\\++))$"));
        lexems.put("ASSIGN_OP", Pattern.compile("^\\=$"));
        lexems.put("COMPARE_OP", Pattern.compile("(^[<>!]|(>=)|(<=)|(==)|!=)$"));

        lexems.put("L_BC", Pattern.compile("^\\($"));
        lexems.put("R_BC", Pattern.compile("^\\)$"));
        lexems.put("L_BRACE", Pattern.compile("^\\{$"));
        lexems.put("R_BRACE", Pattern.compile("^^\\}$"));
        lexems.put("ENDL", Pattern.compile("^;$"));
        lexems.put("DIV", Pattern.compile(",{1}$"));

        lexems.put("IF", Pattern.compile("^IF$"));
        lexems.put("ELSE", Pattern.compile("^ELSE$"));
        lexems.put("WHILE", Pattern.compile("^WHILE$"));
        lexems.put("DO",Pattern.compile("^DO$"));
        lexems.put("FOR", Pattern.compile("^FOR$"));
        lexems.put("PRINTF",Pattern.compile("^PRINTF$"));
    }

    public void lexerWork(String srcExample)
    {
        String resultToken = "";
        String oracleStr = "";

        for(int i = 0; i < srcExample.length(); i++)
        {
            if(!srcExample.substring(i, i + 1).equals(" "))
            {
                resultToken += srcExample.substring(i, i + 1);

                if (i < srcExample.length() - 1)
                {
                    oracleStr = resultToken + srcExample.substring(i + 1, i + 2);
                }

                for (String lexemName : lexems.keySet())
                {
                    Matcher match1 = lexems.get(lexemName).matcher(resultToken);
                    Matcher match2 = lexems.get(lexemName).matcher(oracleStr);
                    if (match1.matches())
                    {
                        if(!match2.matches())
                        {
                            //System.out.println(lexemName + " found ");
                            tokens.add(new Token(lexemName, resultToken));
                            resultToken = "";
                            if(tokens.get(tokens.size() - 1).getType().equals("VAR"))
                                switch(tokens.get(tokens.size() - 1).getValue())
                                {
                                    case "IF" -> tokens.get(tokens.size() - 1).setType("IF");
                                    case "ELSE" -> tokens.get(tokens.size() - 1).setType("ELSE");
                                    case "WHILE" -> tokens.get(tokens.size() - 1).setType("WHILE");
                                    case "DO" -> tokens.get(tokens.size() - 1).setType("DO");
                                    case "FOR" -> tokens.get(tokens.size() - 1).setType("FOR");
                                    case "PRINTF" -> tokens.get(tokens.size() - 1).setType("PRINTF");
                                    default -> tokens.get(tokens.size() - 1).setType("VAR");
                                }
                        }
                        break;
                    }
                }
            }
        }

        if(resultToken.length() != 0)
        {
            for (String lexemName : lexems.keySet())
            {
                Matcher match1 = lexems.get(lexemName).matcher(resultToken);
                if (match1.matches())
                {
                    //System.out.println(lexemName + " found ");
                    tokens.add(new Token(lexemName, resultToken));
                    break;
                }
            }
        }
    }

    public void printTokens()
    {
        int i = 0;
        for (Token token: tokens)
        {
            i++;
            System.out.printf("№%d ", i);
            System.out.println(token);
        }
    }

    public List<Token> getTokens()
    {
        return tokens;
    }
}
