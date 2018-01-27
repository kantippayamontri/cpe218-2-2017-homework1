
public class Node {

    Character value;

    Node left;
    Node right;

    Node(char x){

        value = x;
        left = null;
        right = null;

    }

    Node(){
        left = null;
        right = null;
    }

    public String toString() {
        return value.toString();
    }
}
