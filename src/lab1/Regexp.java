package lab1;
import java.util.*;
import java.util.regex.*;

public class Regexp {

    private static Map<String, Pattern> lexems = new HashMap<>();

    static {

        lexems.put("VAR", Pattern.compile("^[a-z][a-z0-9]{0,}$"));
        lexems.put("DIGIT", Pattern.compile("^0|([1-9][0-9]*)$"));
        lexems.put("ASSIGN_OP", Pattern.compile("^=$"));
    }


    public static void main(String[] args) {

        String srcExample = "=";

        List<Token> tokens = new LinkedList<>();

        for (String lexemName: lexems.keySet()) {
            Matcher m = lexems.get(lexemName).matcher(srcExample);
            if (m.find()) {
                System.out.println(lexemName + " found ");
                tokens.add(new Token(lexemName, srcExample));
            }
        }

        for (Token token: tokens) {
            System.out.println(token);
        }


    }

}

class Token {

    private String type;
    private String value;

    public Token(String type, String value){
        this.type = type;
        this.value = value;
    }


    @Override
    public String toString(){
        return "TOKEN[type=\"" + this.type + "\", value=\"" + this.value + "\"]";
    }

}