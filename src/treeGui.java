/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

//package components;

/**
 * This application that requires the following additional files:
 *   TreeDemoHelp.html
 *    arnold.html
 *    bloch.html
 *    chan.html
 *    jls.html
 *    swingtutorial.html
 *    tutorial.html
 *    tutorialcont.html
 *    vm.html
 */



import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.net.URL;
import java.util.Stack;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.GridLayout;

public class treeGui extends JPanel
        implements TreeSelectionListener {
    private JEditorPane htmlPane;
    private JTree tree;
    private URL helpURL;
    private static boolean DEBUG = false;

    //Optionally play with line styles.  Possible values are
    //"Angled" (the default), "Horizontal", and "None".
    private static boolean playWithLineStyle = false;
    private static String lineStyle = "Horizontal";

    //Optionally set the look and feel.
    private static boolean useSystemLookAndFeel = false;


    public static boolean is_leaf = false;




    
    public treeGui(Node x) {
        super(new GridLayout(1,0));

        //Create the nodes.
        DefaultMutableTreeNode top =
                new DefaultMutableTreeNode(x);
        createNodes(top,x);



        //Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        if (playWithLineStyle) {
            System.out.println("line style = " + lineStyle);
            tree.putClientProperty("JTree.lineStyle", lineStyle);
        }

        //Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(tree);

        //Create the HTML viewing pane.
        htmlPane = new JEditorPane();
        htmlPane.setEditable(false);
        JScrollPane htmlView = new JScrollPane(htmlPane);
        //Add the scroll panes to a split pane.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(treeView);
        splitPane.setBottomComponent(htmlView);

        Dimension minimumSize = new Dimension(100, 50);
        htmlView.setMinimumSize(minimumSize);
        treeView.setMinimumSize(minimumSize);
        splitPane.setDividerLocation(100);
        splitPane.setPreferredSize(new Dimension(500, 300));


        ImageIcon leafIcon = createImageIcon("middle.gif");
        if (leafIcon != null) {
            DefaultTreeCellRenderer renderer =
                    new DefaultTreeCellRenderer();
            renderer.setClosedIcon(leafIcon);
            renderer.setOpenIcon(leafIcon);
            tree.setCellRenderer(renderer);
        }


        //Add the split pane to this panel.
        add(splitPane);
    }


    /** Required by TreeSelectionListener interface. */
    /*public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                           tree.getLastSelectedPathComponent();

        if (node == null) return;

        Object nodeInfo = node.getUserObject();
        if (node.isLeaf()) {
            BookInfo book = (BookInfo)nodeInfo;
            displayURL(book.bookURL);
            if (DEBUG) {
                System.out.print(book.bookURL + ":  \n    ");
            }
        } else {
            displayURL(helpURL);
        }
        if (DEBUG) {
            System.out.println(nodeInfo.toString());
        }
    }*/

    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                tree.getLastSelectedPathComponent();
        if (node == null) return;
        else {
            is_leaf = node.isLeaf();
            Object nodeData = node.getUserObject();
            displaycal((Node) nodeData);
        }
    }


    private class BookInfo {
        public String bookName;
        public URL bookURL;

        public BookInfo(String book, String filename) {
            bookName = book;
            bookURL = getClass().getResource(filename);
            if (bookURL == null) {
                System.err.println("Couldn't find file: "
                        + filename);
            }
        }

        public String toString() {
            return bookName;
        }
    }



    private void displaycal(Node x) {
        System.out.println("Attempted to display a null URL.");
        if(!is_leaf) {
            htmlPane.setText(infix(x) + "=" + calculate(x));
        }else {
            htmlPane.setText(x.toString());
        }

    }

    private void createNodes(DefaultMutableTreeNode top,Node node) {


        if(node.left != null) {

            DefaultMutableTreeNode LEFT = new DefaultMutableTreeNode(node.left);
            top.add(LEFT);
            createNodes(LEFT,node.left);


        }

        if(node.right != null) {

            DefaultMutableTreeNode RIGHT = new DefaultMutableTreeNode(node.right);
            top.add(RIGHT);
            createNodes(RIGHT,node.right);
        }



    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI(Node x) {
        if (useSystemLookAndFeel) {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Couldn't use system look and feel.");
            }
        }

        //Create and set up the window.
        JFrame frame = new JFrame("treeGui");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new treeGui(x));

        //Display the window.
        frame.pack();
        frame.setVisible(true);

    }


    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.

        //String posfix = "251-*32*+";
        String posfix = args[0];
        char [] postfix_array = posfix.toCharArray();

        Node root = bulit_tree(postfix_array);


        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(root);
            }
        });
    }


    public static Node bulit_tree(char posfix_array []) {

        Stack<Node> data = new Stack();
        Node node,num1,num2;

        for(int i=0;i<posfix_array.length;i++) {

            if(!is_operater(posfix_array[i])) {

                node = new Node(posfix_array[i]);
                data.push(node);

            }else {

                node = new Node(posfix_array[i]);
                num2 = data.pop();
                num1 = data.pop();

                node.left = new Node();
                node.left = num1;
                node.right = new Node();
                node.right = num2;

                data.push(node);




            }

        }

        node = data.peek();
        return node;



    }


    public static boolean is_operater(char x) {

        if(x== '+' || x=='-' || x=='*' || x=='/') {

            return true;

        }


        return false;

    }


    public static String infix(Node node) {

        StringBuilder infix = new StringBuilder();
        infix = inorder(node);

        infix.deleteCharAt(0);
        infix.deleteCharAt(infix.length()-1);
        for(int i =1;i<infix.length()-1;i++) {

            if(infix.charAt(i-1) == '(' && !is_operater(infix.charAt(i)) && infix.charAt(i+1) == ')') {
                infix.setCharAt(i-1, ' ');
                infix.setCharAt(i+1, ' ');
            }else if(infix.charAt(i)== ' ') {

                continue;
            }

        }

        for(int i=0;i<infix.length();i++) {

            if(infix.charAt(i) == ' ') {
                infix.deleteCharAt(i);

            }

        }


        return infix.toString();
    }

    public static StringBuilder inorder(Node node) {

        StringBuilder infix = new StringBuilder();

        subinorder(node,infix);

        return infix;

    }

    public static void subinorder(Node node,StringBuilder infix) {

        if (node != null) {
            infix.append("(");
            subinorder(node.left, infix);
            infix.append(node.value);
            subinorder(node.right, infix);
            infix.append(")");
        }

    }


    public static int calculate(Node node) {




        if(node.value == '+') {

            return calculate(node.left) + calculate(node.right);

        }else if(node.value == '-') {

            return calculate(node.left) - calculate(node.right);

        }else if(node.value == '*') {

            return calculate(node.left) * calculate(node.right);

        }else if(node.value == '/') {

            return calculate(node.left)  / calculate(node.right);

        }else return Integer.parseInt((String.valueOf(node.value)));


    }


    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = treeGui.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }


}