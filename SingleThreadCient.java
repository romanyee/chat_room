package com.bittech;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class SingleThreadCient {
    public static void main(String[] args) throws IOException {
        Socket client = null;
        Scanner readFromServer = null;
        PrintStream writeMsgToServer = null;
        try {
            //连接到指定服务器
            client = new Socket("127.0.0.1",6666);
            //取得输入输出流
            readFromServer = new Scanner(client.getInputStream());
            writeMsgToServer = new PrintStream(client.getOutputStream());
            writeMsgToServer.println("I am Client!");
            if (readFromServer.hasNext()) {
                System.out.println("服务器发来的消息为："+readFromServer.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭流
            client.close();
            readFromServer.close();
            writeMsgToServer.close();

        }
    }
}
