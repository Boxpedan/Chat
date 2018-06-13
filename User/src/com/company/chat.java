package com.company;

import jdk.nashorn.internal.scripts.JO;
import org.junit.*;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class chat {
    static String name;
    static JFrame login = new JFrame("Login");
    static JFrame client = new JFrame("Chat");
    static JFrame menu = new JFrame("Menu");
    static JFrame stat = new JFrame("User Status");
    static JFrame used = new JFrame("Chat History");
    static JFrame sclient = new JFrame("Chat");
    static JLabel blank = new JLabel("\n");
    static JTextField textField1 = new JTextField(20);
    static JPasswordField passwordField = new JPasswordField(20);
    static JTextField textField3 = new JTextField(10);

    static BufferedReader in;
    static PrintWriter out;

    public static void main(String asg[]) throws Exception{
        Socket soc = new Socket("localhost", 9806);
        chat client = new chat();
        client.start(soc);
    }

    void start(Socket soc) throws Exception{
        boolean flag = logUI(soc);
        if(flag == true){
            menuUI(soc);
        }
    }

    public static boolean logUI(Socket soc) throws Exception{
        JLabel label1 = new JLabel("Account ");
        JLabel label2 = new JLabel("PassWord ");
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Signup");
        JButton exitButton = new JButton("Leave");

        login.setLayout(new FlowLayout());
        login.add(label1);
        login.add(textField1);
        login.add(blank);
        login.add(label2);
        login.add(passwordField);
        login.add(blank);
        login.add(loginButton);
        login.add(blank);
        login.add(registerButton);
        login.add(blank);
        login.add(exitButton);
        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.setSize(300,200);
        login.setVisible(true);

        loginButton.addActionListener(new Listerner1());
        registerButton.addActionListener(new Listerner2());
        exitButton.addActionListener(new Listerner3());

        in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        out = new PrintWriter(soc.getOutputStream(),true);

        boolean flag = false;
        while(flag == false){
            String sign[] = in.readLine().split(":");

            if(sign[0].equals("EPU")){
                JOptionPane.showMessageDialog(login,"No username input.");
            }
            else if(sign[0].equals("EPP")){
                JOptionPane.showMessageDialog(login, "No password input");
            }
            else if(sign[0].equals("EU")){
                JOptionPane.showMessageDialog(login,"Username has been registerred.");
            }
            else if(sign[0].equals("EXIST")){
                JOptionPane.showMessageDialog(login,"Username is not exist.");
            }
            else if(sign[0].equals("ACCEPT")){
                JOptionPane.showMessageDialog(login,"Successful!");
                flag = true;
                name = new String(sign[1]);
                login.setVisible(false);
            }
            else if(sign[0].equals("REJECT")){
                JOptionPane.showMessageDialog(login,"Wrong password.", "Tips", JOptionPane.WARNING_MESSAGE);
            }
        }
        return true;
    }

    public static void menuUI(Socket soc) throws Exception{
        menu.setLayout(new FlowLayout());
        JButton fresh = new JButton("Refresh");
        JButton history = new JButton("Chat History");
        JButton singlechat = new JButton("One-to-one");
        JButton mutiple = new JButton("Group");
        JButton status = new JButton("User Status");
        JButton exit = new JButton("Logout");

        menu.add(singlechat);
        menu.add(blank);
        menu.add(mutiple);
        menu.add(blank);
        menu.add(status);
        menu.add(blank);
        menu.add(history);
        menu.add(blank);
        menu.add(exit);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menu.setSize(300,200);
        menu.setVisible(true);

        exit.addActionListener(new Listerner4());
        status.addActionListener(new Listerner5());
        mutiple.addActionListener(new Listerner6());
        singlechat.addActionListener(new Listerner7());
        history.addActionListener(new Listerner8());

        in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        out = new PrintWriter(soc.getOutputStream(), true);

        while(true){
            int i = 0;
            String aa[] = in.readLine().split(":");
            //System.out.println(aa[1]+aa[2]+aa[3]);
            if(aa[0].equals("STATUS")){
                stat.setLayout(new FlowLayout());
                JTextArea display2 = new JTextArea(20,10);
                JButton close = new JButton("Close");
                close.addActionListener(new Listerner3());
                stat.add(display2);
                stat.add(blank);
                stat.add(close);
                stat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                stat.setSize(500,400);
                stat.setVisible(true);

                display2.setText("");
                boolean flag = true;
                int x = 1;
                while(flag == true){
                    if(aa[x].equals("END")){
                        display2.append("END"+"\n");
                        flag = false;
                    }
                    else if(aa[x + 1].equals("false")) {
                        display2.append(x + " " + aa[x] + " " + "offline" + "\n");
                    }else{
                            display2.append(x + " " + aa[x] + " " + "online" + "\n");
                    }
                    x = x + 2;
                }

            }
            else if(aa[0].equals("EMPTYHIS")){
                JOptionPane.showMessageDialog(menu,"Empty chat history.","Tips",JOptionPane.WARNING_MESSAGE );
            }
            else if(aa[0].equals("HISTORY")){
                used.setLayout(new FlowLayout());
                JTextArea display3 = new JTextArea(20,30);
                JButton leave = new JButton("Close");
                leave.addActionListener(new Listerner3());
                used.add(display3);
                used.add(blank);
                used.add(leave);
                used.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                used.setSize(300,200);
                used.setVisible(true);

                int y = aa.length;
                int temp = 1;
                while(temp <= y ){
                    display3.append("Sender: " +aa[temp] + aa[temp + 1] + "\n");
                    temp = temp + 2;
                }
            }
            else if(aa[0].equals("MUTI")){
                menu.setVisible(false);
                chatUI(soc);
            }
            else if(aa[0].equals("SING")){
                menu.setVisible(false);
                onetoUI(soc);
            }
        }
    }

    public static void chatUI(Socket soc) throws Exception{
        in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        out = new PrintWriter(soc.getOutputStream(),true);

        client.setLayout(new FlowLayout());
        JTextArea chatArea = new JTextArea(20,40);
        chatArea.setEditable(false);
        JButton send = new JButton("Send");
        JButton exit = new JButton("Leave");

        client.add(new JScrollPane(chatArea));
        client.add(blank);
        client.add(textField3);
        client.add(blank);
        client.add(send);
        client.add(exit);
        client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.setSize(475,500);
        client.setVisible(true);

        send.addActionListener(new Listerner10());
        exit.addActionListener(new Listerner11());
        while(true) {
            String message = in.readLine();
            chatArea.append(message + "\n");
        }
    }

    public static void onetoUI(Socket soc) throws Exception{
        in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        out = new PrintWriter(soc.getOutputStream(), true);


        sclient.setLayout(new FlowLayout());
        JTextArea chatArea = new JTextArea(20,40);
        chatArea.setEditable(false);
        sclient.add(new JScrollPane(chatArea));
        sclient.add(blank);
        sclient.add(textField3);
        sclient.add(blank);
        JButton send = new JButton("send");
        JButton exit = new JButton("exit");

        sclient.add(send);
        sclient.add(exit);
        sclient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sclient.setSize(475, 600);
        sclient.setVisible(true);

        send.addActionListener(new Listerner10());
        textField3.addActionListener(new Listerner10());
        exit.addActionListener(new Listerner12());

        while(true) {
            String mess = in.readLine();
            if (mess.equals("EXIT_ORDER")) {
                sclient.getDefaultCloseOperation();
                break;
            } else if (mess.equals("CHECKBOX")) {
                out.println("CHECKBOX");
            } else
                chatArea.append(mess + "\n");

        }
    }
}

class Listerner1 implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e) {
        String info = "LOG";
        info = info + ":" + chat.textField1.getText();
        String passText = new String(chat.passwordField.getPassword());
        info = info + ":" + passText;
        chat.out.println(info);
    }
}

class Listerner2 implements ActionListener{
    public void actionPerformed(ActionEvent e){
        String info = "REG";
        info = info + ":" + chat.textField1.getText();
        String passText = new String(chat.passwordField.getPassword());
        info = info + ":" + passText;
        chat.out.println(info);
    }
}

class Listerner3 implements ActionListener{
    public void actionPerformed(ActionEvent e){
        System.exit(0);
    }
}

class Listerner4 implements ActionListener{
    public void actionPerformed(ActionEvent e){
        chat.out.println("EXIT" + "|" + chat.name);
        System.exit(0);
    }

}


class Listerner5 implements ActionListener{
    public void actionPerformed(ActionEvent e){
        chat.out.println("STATUS");
    }

}


class Listerner6 implements ActionListener{
    public void actionPerformed(ActionEvent e){
        chat.out.println("MUT");
    }

}


class Listerner7 implements ActionListener{
    public void actionPerformed(ActionEvent e){
        chat.out.println("SING");
    }

}


class Listerner8 implements ActionListener{
    public void actionPerformed(ActionEvent e){
        chat.out.println("HIS");
    }

}

class Listerner10 implements ActionListener{
    public void actionPerformed(ActionEvent e){
        chat.out.println(chat.textField3.getText());
        chat.textField3.setText("");
    }

}

class Listerner11 implements ActionListener{
    public void actionPerformed(ActionEvent e){
        chat.client.setVisible(false);
        chat.menu.setVisible(true);
    }
}

class Listerner12 implements ActionListener{
    public void actionPerformed(ActionEvent e){
        chat.sclient.setVisible(false);
        chat.sclient.setVisible(true);
    }
}