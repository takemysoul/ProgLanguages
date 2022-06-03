package lab1;

public class ParseException extends Exception
{
    private Token current;
    private String expected;
    private int numLine;
    private int numToken;

    public ParseException(int numLine, int numToken, Token current, String expected)
    {
        this.numLine = numLine;
        this.numToken = numToken;
        this.current = current;
        this.expected = expected;
    }

    public void getInfo(int numLine, int numToken, Token current, String expected)
    {
        System.out.printf("Line: %s Token: %d - Expected: %s but received: %s\n", numLine, numToken, expected, current.getType());
    }

    public Token getCurrent()
    {
        return current;
    }

    public String getExpected()
    {
        return expected;
    }
}
