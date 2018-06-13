package com.company;

import javafx.scene.shape.Path;

import java.io.*;

import java.net.*;
import java.nio.file.*;
import java.util.ArrayList;

public class server {
    static ArrayList<String> userNames = new ArrayList<String>();
    static  ArrayList<String> passWord = new ArrayList<String>();
    static ArrayList<String> stat = new ArrayList<String>();
    static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
    public static void main(String[] args) throws Exception{
        // TODO Auto-generated method stub
        System.out.println("Waiting for clients...");
        ServerSocket ss = new ServerSocket(9806);
        while (true)
        {
            Socket soc = ss.accept();
            System.out.println("Connection established");
            ChatHandler handler = new ChatHandler(soc);
            handler.start();
        }
    }
}

class ChatHandler extends Thread {
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    String name;
    String pass;
    PrintWriter pw;
    static FileWriter fw;
    static BufferedWriter bw;

    public ChatHandler(Socket socket) throws IOException {
        this.socket = socket;
        fw = new FileWriter("C:\\Users\\19713\\Desktop\\history.txt");
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw, true);
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                String info[] = in.readLine().split(":");
                name = new String(info[1]);
                pass = new String(info[2]);
                System.out.println(info[0]);
                System.out.println(name);
                System.out.println(pass);
                boolean flag = server.userNames.contains(name);
                if (info[0].equals("LOG")) {
                    if (flag == false) {
                        out.println("EXIST");
                    } else {
                        int index = server.userNames.indexOf(name);
                        if (pass.equals(server.passWord.get(index))) {
                            out.println("ACCEPT" + ":" + name);
                            break;
                        } else {
                            out.println("REJECT");
                        }
                    }
                } else if (info[0].equals("REG")) {
                    if (flag == true) {
                        out.println("EU");
                    } else {
                        server.userNames.add(name);
                        server.passWord.add(pass);
                        String now = "true";
                        server.stat.add(now);
                        out.println("ACCEPT" + ":" + name);
                        break;
                    }
                }
            }
            server.printWriters.add(out);
            while (true) {
                String order[] = in.readLine().split(":");
                System.out.println(order[0]);
                if (order[0].equals("EXIT")) {
                    int index = server.userNames.indexOf(order[1]);
                    server.stat.set(index, "false");
                } else if (order[0].equals("STATUS")) {
                    String info = "STATUS";
                    int i = server.userNames.size();
                    System.out.println(i);
                    for (int n = 0; n < server.userNames.size(); n++) {
                        System.out.println(server.userNames.get(n));
                        System.out.println(server.stat.get(n));
                    }
                    for (int x = 0; x < server.userNames.size(); x++) {
                        info = info + ":" + server.userNames.get(x) + ":" + server.stat.get(x);
                        System.out.println(info);
                        x = x + 1;
                    }
                    System.out.println(info);
                    info = info + ":" + "END";
                    out.println(info);
                } else if (order[0].equals("HIS")) {
                    String filename = "C:\\Users\\19713\\Desktop\\history.txt";
                    String data = new String(Files.readAllBytes(Paths.get(filename)));
                    if (data.equals("")) {
                        out.println("EMPTYHIS");
                    } else {
                        data = "HIS" + ":" + data;
                        System.out.println(data);
                        out.println(data);
                    }
                } else if (order[0].equals("SING")) {

                } else if (order[0].equals("MUT")) {
                    out.println("MUTI");
                    while (true) {
                        String message = in.readLine();
                        if (message == null)
                            return;
                        pw.println(name + ":" + message);
                        for (PrintWriter writer : server.printWriters) {
                            writer.println(name + ":" + message);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);

        }
    }
}