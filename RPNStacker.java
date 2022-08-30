import java.util.*;
import java.io.File;
import java.io.IOException;

class Node {
  public int value;
  public Node next;

  Node(int value) {
    this.value = value;
    this.next = null;
  }

  Node(int value, Node next) {
    this.value = value;
    this.next = next;
  }
}

class Stack {
  public Node top;

  Stack() {
    this.top = null;
  }

  public void push(int newValue) {
    this.top = new Node(newValue, this.top);
  }

  public int pop() {
    Node new_top = this.top.next;
    Node curr_top = this.top;
    this.top = new_top;
    return curr_top.value;
  }
}

class Main {
  public static void main(String[] args) throws IOException {
    Stack stack = new Stack();
    Scanner scanner = new Scanner(new File("input.txt"));
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