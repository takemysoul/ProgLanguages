package lab1;
import java.util.*;

/*
a = 2, b = 3;
c = a + b;
d = (a * b) / c;
WHILE(a < 10)
a = a + 3;
PRINTF(a);
FOR(i = 0, i < 5, i = i + 1)
PRINT(i);
IF (c > 3)
d = (a * b) / c;
PRINTF(d);
*/

public class Based
{
    public static void main(String[] args)
    {
        String srcExample = "";
        String temporary = "";
        Scanner scanner = new Scanner(System.in);

        //-----------INPUT-----------
        while (true)
        {
            temporary = scanner.nextLine();
            if (temporary.isEmpty())
            {
                break;
            }
            srcExample += temporary;
        }

        //-----------LEXER---WORK-----------
        Lexer lex = new Lexer();
        lex.lexerWork(srcExample);
        lex.printTokens();
        System.out.println();

        //-----------PARSER---WORK-----------
        Parser p = new Parser(lex.getTokens(), srcExample.length());
        try
        {
            p.lang();
        }
        catch (ParseException | IndexOutOfBoundsException ignored) {}

        //-----------INTERPRETER---WORK-----------
        if (p.correctCode)
        {
            Interpreter interpreter = new Interpreter(lex.getTokens());
            System.out.println(interpreter);
        }
    }
}
