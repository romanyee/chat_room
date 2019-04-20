package com.bittech;


import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.*;


//从服务器读消息
class ReadFromServer implements Runnable{
    private Socket client;

    //通过构造方法传入通信的Socket
    public ReadFromServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        Scanner readFromServer = null;
        try {
            readFromServer = new Scanner(client.getInputStream());
            readFromServer.useDelimiter("\n");

            //不断读取服务器信息
            while (true) {
                if (readFromServer.hasNext()) {
                    String str = readFromServer.next();
                    System.out.println(str);
                    System.out.println();
                }
                if (client.isClosed()) {
                    System.out.println("客户端已关闭！");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            readFromServer.close();
        }
    }
}
class SendMsgToServer implements Runnable{
    private Socket client;

    public SendMsgToServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        //获取键盘输入
        Scanner in = new Scanner(System.in);

        PrintStream sendMsgToServer = null;
        try {
            sendMsgToServer = new PrintStream(client.getOutputStream(), true, "UTF-8");
            System.out.println("输入要发送的信息：（注册->userName:zhangsan  群聊->G:zhangsan-Hello i am ...  私聊->P:sender:receiver:hello i am ...  退出->zhangsan:byebye）");
            while (true) {
                if (in.hasNextLine()) {
                    String strToServer = in.nextLine();
                    sendMsgToServer.println(strToServer);
                    if (strToServer.contains("byebye")) {
                        System.out.println("正在退出聊天室……");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendMsgToServer.close();
            in.close();
        }
    }
}
public class MutilTreadClient {
    public static void main(String[] args) throws IOException {
        Socket client = new Socket("127.0.0.1",6666);
        Thread readThread = new Thread(new ReadFromServer(client));
        Thread sendThread = new Thread(new SendMsgToServer(client));
        readThread.start();
        sendThread.start();
    }
}
