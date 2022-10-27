// package stacker.rpn.lexer;

import java.util.regex.*;
import java.util.Arrays;

public class Regex {

  public static boolean isNum(String token) {
    String regex = "[0-9]+";

    Pattern p = Pattern.compile(regex);

    if (token == null) {
      return false;
    }

    Matcher m = p.matcher(token);

    return m.matches();
  }

  public static boolean isOP(String token) {
    String[] values = { "+", "-", "*", "/" };
    boolean contains = Arrays.asList(values).contains(token);
    return contains;
  }

  public static boolean isID(String token) {
    String regex = "(?i)[a-z][a-z0-9_]*";
    Pattern p = Pattern.compile(regex);

    if (token == null) {
      return false;
    }

    Matcher m = p.matcher(token);

    return m.matches();
  }
}