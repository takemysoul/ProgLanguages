package lab1;
import java.util.*;

public class Parser
{
    private final List<Token> tokens;
    private final int len;

    private int iterator;
    private int curLine;
    private Token curToken;

    public boolean correctCode;

    public Parser(List<Token> tokens, int len)
    {
        this.tokens = tokens;
        this.len = len;
        curLine = 0;
        iterator = 0;
        curToken = tokens.get(iterator);
        correctCode = true;
    }

    public void lang() throws ParseException
    {
        for (int i = 0; i < len; i++)
        {
            curLine++;
            expr();
        }
    }

    public void expr()
    {
        body();
        checkError("ENDL");
    }

    public void body()
    {
        switch (curToken.getType())
        {
            case "VAR" -> expr_assign();
            case "IF" -> if_op();
            case "WHILE" -> while_op();
            case "DO" -> do_while_op();
            case "FOR" -> for_op();
            case "PRINTF" -> printf();
            default -> checkError("VAR");
        }
    }

    //-----------ERROR---WORK-----------
    public void callError(String tokenType) throws ParseException
    {
        if (!Objects.equals(curToken.getType(), tokenType))
        {
            correctCode = false;
            throw new ParseException(curLine, iterator, curToken, tokenType);
        }
    }

    public void checkError(String tokenType)
    {
        try
        {
            callError(tokenType);
        }
        catch (ParseException e)
        {
            e.getInfo(curLine, iterator + 1, e.getCurrent(), e.getExpected());
            curToken = tokens.get(--iterator);
            System.exit(0);
        }
        curToken = tokens.get(++iterator);
    }

    //-----------EXPRESSIONS-----------
    public boolean smthInBody()
    {
        return switch (curToken.getType())
        {
            case "VAR", "IF", "FOR", "WHILE", "DO", "PRINTF" -> true;
            default -> false;
        };
    }

    public boolean smthInBody_DO_WHILE()
    {
        return switch (curToken.getType())
        {
            case "VAR", "IF", "FOR", "DO", "PRINTF" -> true;
            default -> false;
        };
    }

    public void expr_value()
    {
        switch (curToken.getType())
        {
            case "VAR", "DIGIT" -> value();
            case "L_BC" -> infinityBR();
            default -> checkError("VAR");
        }
        while ("OP".equals(curToken.getType()))
        {
            checkError("OP");
            value();
        }
    }

    public void value()
    {
        switch (curToken.getType())
        {
            case "DIGIT" -> checkError("DIGIT");
            case "L_BC" -> infinityBR();
            default -> checkError("VAR");
        }
    }

    public void infinityBR()
    {
        checkError("L_BC");
        expr_value();
        checkError("R_BC");
    }

    public void assign()
    {
        checkError("VAR");
        checkError("ASSIGN_OP");
        expr_value();
    }

    public void expr_assign()
    {
        assign();
        while ("DIV".equals(curToken.getType()))
        {
            checkError("DIV");
            assign();
        }
    }

    //-----------CONDITIONS-----------
    public void condition()
    {
        checkError("VAR");
        checkError("COMPARE_OP");
        expr_value();
    }

    public void condition_in_br()
    {
        checkError("L_BC");
        condition();
        checkError("R_BC");
    }

    //-----------OPERATIONS-----------
    public void if_op()
    {
        checkError("IF");
        condition_in_br();
        do
        {
            body();
        } while (smthInBody());
        if ("ELSE".equals(curToken.getType()))
        {
            else_op();
        }
    }

    public void else_op()
    {
        checkError("ELSE");
        checkError("L_BRACE");
        do
        {
            expr();
        } while (smthInBody());
        checkError("R_BRACE");
    }

    public void while_op()
    {
        checkError("WHILE");
        condition_in_br();
        do
        {
            body();
        } while (smthInBody());
    }

    public void do_while_op()
    {
        checkError("DO");
        do
        {
            body();
        } while (smthInBody_DO_WHILE());
        checkError("WHILE");
        condition_in_br();
    }

    public void for_op()
    {
        checkError("FOR");
        checkError("L_BC");
        assign();
        checkError("DIV");
        condition();
        checkError("DIV");
        assign();
        checkError("R_BC");
        do
        {
            body();
        } while (smthInBody());
    }

    public void printf()
    {
        checkError("PRINTF");
        if ("L_BC".equals(curToken.getType()))
        {
            checkError("L_BC");
            if ("DIGIT".equals(curToken.getType()))
            {
                checkError("DIGIT");
            }
            else
            {
                checkError("VAR");
            }
            checkError("R_BC");
        }
    }
}