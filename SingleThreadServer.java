package com.bittech;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SingleThreadServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = null;
        Scanner readfromClient = null;
        PrintStream sendMsgToClient = null;

        try {
            //建立服务器Socket等待客户连接
            serverSocket=new ServerSocket(6666);
            System.out.println("等待客户端连接……");
            //等待客户端连接
            Socket cilent = serverSocket.accept();
            System.out.println("有新的客户端连接，端口号为：" + cilent.getPort());
            System.out.println(cilent.getLocalPort());
            //输入使用Scanner，使出使用打印流。取得输入输出流，进行通信
            readfromClient = new Scanner(cilent.getInputStream());
            sendMsgToClient = new PrintStream(cilent.getOutputStream(),true,"UTF-8");
            if (readfromClient.hasNext()) {
                System.out.println("客户端说："+readfromClient.nextLine());
            }
            sendMsgToClient.println("HI I am Server!");
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭流
            serverSocket.close();
            readfromClient.close();
            sendMsgToClient.close();
        }

    }
}
