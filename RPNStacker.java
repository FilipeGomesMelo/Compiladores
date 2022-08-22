import java.util.*;
import java.io.File;
import java.io.IOException;

class Token {
  public int value;
  public Token next;

  Token(int value) {
    this.value = value;
    this.next = null;
  }
}

class Stack {
  public Token top;

  Stack() {
    this.top = null;
  }

  public void push(int newValue) {
    Token last_top = this.top;
    this.top = new Token(newValue);
    this.top.next = last_top;
  }

  public int pop() {
    Token new_top = this.top.next;
    Token last_top = this.top;
    this.top = new_top;
    return last_top.value;
  }
}

class Main {
  public static void main(String[] args) throws IOException {
    Stack stack = new Stack();
    String file = "input.txt";
    Scanner scanner = new Scanner(new File(file));
    scanner.useDelimiter("\n");
    while (scanner.hasNext()) {
      String entry = scanner.next();
      boolean isNumeric = entry.chars().allMatch(Character::isDigit);
      if (isNumeric) {
        stack.push(Integer.parseInt(entry));
      } else {
        int snd_oper = stack.pop();
        int fst_oper = stack.pop();
        int result = 0;
        if (entry.equals("+")) {
          result = fst_oper + snd_oper;
        } else if (entry.equals("*")) {
          result = fst_oper * snd_oper;
        } else if (entry.equals("-")) {
          result = fst_oper - snd_oper;
        } else {
          result = fst_oper / snd_oper;
        }
        stack.push(result);
      }
    }
    int final_result = stack.pop();
    System.out.println(final_result);
    scanner.close();
  }
}