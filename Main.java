import java.util.*;
import java.io.File;
import java.io.IOException;

class InvalidTokenException extends Exception {
  public InvalidTokenException(String errorMessage) {
    super(errorMessage);
  }
}

class Token {
  public Token next;
  public final int line;
  public final TokenType type; // token type
  public final String lexeme; // token value

  Token(TokenType type, String value, int line) {
    this.type = type;
    this.lexeme = value;
    this.line = line;
  }

  Token(TokenType type, String value, int line, Token next) {
    this.type = type;
    this.lexeme = value;
    this.line = line;
    this.next = next;
  }

  @Override
  public String toString() {
    return "Token [type=" + this.type + ", lexeme=" + this.lexeme + ", line=" + this.line + "]";
  }
}

class StackScanner {
  public Token top;
  public Token bottom;
  public int size;
  public HashMap<String, String> env;
  

  StackScanner(HashMap<String, String> env) {
    this.top = null;
    this.bottom = null;
    this.size = 0;
    this.env = env;
  }

  @Override
  public String toString() {
    Token curr_token = this.top;
    StringJoiner sb = new StringJoiner("\n");
    while (curr_token != null) {
      sb.add(curr_token.toString());
      curr_token = curr_token.next;
    }

    return sb.toString();
  }

  public void push(String newValue) throws InvalidTokenException {
    TokenType type = TokenType.NUM;
    boolean isNumeric = Regex.isNum(newValue);
    boolean isOp = Regex.isOP(newValue);
    boolean isId = Regex.isID(newValue);
    if (!isOp && !isNumeric && !isId) {
      throw new InvalidTokenException(
        "Invalid Operator found on line " + this.size + ". \"" + newValue + "\" is not a valid value.");
    } else {
      if (isOp) {
        if (newValue.equals("+")) {
        type = TokenType.PLUS;
        } else if (newValue.equals("*")) {
          type = TokenType.STAR;
        } else if (newValue.equals("-")) {
          type = TokenType.MINUS;
        } else if (newValue.equals("/")) {
          type = TokenType.SLASH;
        }
      } else if (isId) {
        if (this.env.containsKey(newValue)) {
          newValue = this.env.get(newValue);
        } else {
          throw new InvalidTokenException(
            "Non-existent ID found on line " + this.size + ". \"" + newValue + "\" was not declared.");
        }
      }
    }
    this.top = new Token(type, newValue, this.size + 1, this.top);
    this.size++;
  }

  public Token pop() {
    if (this.top == null) {
      return null;
    }
    Token new_top = this.top.next;
    Token curr_top = this.top;
    this.top = new_top;
    this.size--;
    return curr_top;
  }

  public Token[] toArray() {
    Token[] token_array = new Token[this.size];
    int curr_pos = this.size - 1;
    while (curr_pos >= 0) {
      token_array[curr_pos] = this.pop();
      curr_pos--;
    }
    return token_array;
  }
}

class StackParser {
  public Token top;
  public Token bottom;
  public int size;

  StackParser() {
    this.top = null;
    this.bottom = null;
    this.size = 0;
  }

  @Override
  public String toString() {
    Token curr_token = this.top;
    StringJoiner sb = new StringJoiner("\n");
    while (curr_token != null) {
      sb.add(curr_token.toString());
      curr_token = curr_token.next;
    }

    return sb.toString();
  }

  public void push(Token newValue) {
    newValue.next = this.top;
    this.top = newValue;
    this.size++;
  }

  public Token pop() {
    if (this.top == null) {
      return null;
    }
    Token new_top = this.top.next;
    Token curr_top = this.top;
    this.top = new_top;
    this.size--;
    return curr_top;
  }
}

class Interpreter {
  public final HashMap<String, String> env;
  public Interpreter(HashMap<String, String> env){
    this.env = env;
  }

  public void run(String source) throws IOException, InvalidTokenException {
    StackScanner stackScanner = new StackScanner(this.env);
    Scanner scanner = new Scanner(new File(source));
    scanner.useDelimiter("\n");
    while (scanner.hasNext()) {
      String entry = scanner.next();
      stackScanner.push(entry);
    }
    scanner.close();

    Token[] token_array = stackScanner.toArray();

    StackParser stackParser = new StackParser();
    for (Token curr_token : token_array) {
      if (curr_token.type == TokenType.NUM) {
        stackParser.push(curr_token);
      } else {
        if (stackParser.size < 2) {
          throw new InvalidTokenException("Too few arguments for operation on line " + curr_token.line + ".");
        }
        if (stackParser.size > 2) {
          throw new InvalidTokenException("Too many arguments for operation on line " + curr_token.line + ".");
        }
        Token snd_oper = stackParser.pop();
        Token fst_oper = stackParser.pop();
        if (fst_oper.type != TokenType.NUM || snd_oper.type != TokenType.NUM) {
          throw new InvalidTokenException("Invalid Token usage. \"" + curr_token.toString() + "\" is not a operator.");
        }
        int result = 0;
        if (curr_token.type == TokenType.PLUS) {
          result = Integer.parseInt(fst_oper.lexeme) + Integer.parseInt(snd_oper.lexeme);
        } else if (curr_token.type == TokenType.MINUS) {
          result = Integer.parseInt(fst_oper.lexeme) - Integer.parseInt(snd_oper.lexeme);
        } else if (curr_token.type == TokenType.STAR) {
          result = Integer.parseInt(fst_oper.lexeme) * Integer.parseInt(snd_oper.lexeme);
        } else if (curr_token.type == TokenType.SLASH) {
          result = Integer.parseInt(fst_oper.lexeme) / Integer.parseInt(snd_oper.lexeme);
        }
        stackParser.push(new Token(TokenType.NUM, Integer.toString(result), curr_token.line));
      }
    }
    if (stackParser.size > 1) {
      throw new InvalidTokenException("Invalid number of results, missing operator at the end of expression.");
    }
    Token final_result = stackParser.pop();
    System.out.println(final_result.lexeme);
  }
}

class Main {
  public static void main(String[] args) throws IOException, InvalidTokenException {
    HashMap<String, String> hash_map = new HashMap<String, String>();
    hash_map.put("y", "4");
    hash_map.put("z", "10");
    Interpreter interpreter = new Interpreter(hash_map);
    interpreter.run("input.txt");
  }
}