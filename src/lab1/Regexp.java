package lab1;
import java.util.*;
import java.util.regex.*;

public class Regexp
{

    private static Map<String, Pattern> lexems = new HashMap<>();

    static
    {
        lexems.put("VAR", Pattern.compile("[a-z][a-z0-9]{0,}"));
        lexems.put("DIGIT", Pattern.compile("0|([1-9][0-9]*)"));
        lexems.put("ASSIGN_OP", Pattern.compile("="));
        lexems.put("OP", Pattern.compile("\\+|-|\\/|\\*"));
    }

    public static void main(String[] args)
    {
        int end = 0, start = 0;
        String srcExample = "aboba=abo+ba";
        char[] srcExampleChar = srcExample.toCharArray();
        String resultToken = "";

        List<Token> tokens = new LinkedList<>();

        for (String lexemName : lexems.keySet())
        {
            Matcher m = lexems.get(lexemName).matcher(srcExample);
            while (m.find())
            {

                end = m.end();
                start = m.start();
                for(int i = start; i < end; i++)
                {
                    resultToken += srcExampleChar[i];
                }
                System.out.println(lexemName + " found ");
                tokens.add(new Token(lexemName, resultToken));
                resultToken = "";
            }
        }
        for (Token token: tokens)
        {
            System.out.println(token);
        }


    }

}

class Token
{

    private String type;
    private String value;

    public Token(String type, String value)
    {
        this.type = type;
        this.value = value;
    }


    @Override
    public String toString()
    {
        return "TOKEN[type=\"" + this.type + "\", value=\"" + this.value + "\"]";
    }

}