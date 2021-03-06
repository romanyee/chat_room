package com.bittech;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MutiThreadServer {

    //为什么要用ConcurrentHashMap?
    //首先是线程安全问题，肯定不能用HashMap.
    //相比Hashtable,虽然线程安全了,但是Hashtable本身的效率比较低，
    //要实现Hashtable就要给它的各种方法（如put/get/size）加上synchronized
    //这会导致所有并发线程竞争同一把锁，一个线程在进行同步操作时，其他线程只能等待
    //降低并发操作效率。
    //分段锁、jdk1.5之后出现、
    //它支持并发操作
    private static Map<String,Socket> clientsMap = new ConcurrentHashMap<>();

    private static class ExecuteClientRequest implements Runnable{
        private Socket client;

        public ExecuteClientRequest(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            Scanner readFromClient = null;
            try {
                readFromClient = new Scanner(client.getInputStream());
                while (true) {
                    if (readFromClient.hasNextLine()) {
                        String str = readFromClient.nextLine();
                        //进行\r的过滤操作
                        //Windows下进行换行过滤
                        Pattern pattern = Pattern.compile("\r");
                        Matcher matcher = pattern.matcher(str);
                        str = matcher.replaceAll("");
                        if (str.startsWith("userName")) {
//                        用户注册：
//                        userName:zhangsan
                            String userName = str.split(":")[1];
                            userRegister(userName,client);
                            continue;
                        } else if (str.startsWith("G:")) {
//                        群聊实现：
//                        G:zhangsan-Hello i am ...
                            String tmp = str.split(":")[1];
                            String userName = tmp.split("-")[0];
                            String msg = tmp.split("-")[1];
                            GroupChat(userName,msg);
                            continue;
                        } else if (str.startsWith("P:")) {
//                        私聊实现：
//                        P:sender:receiver:hello i am ...
                            String sender = str.split(":",-1)[1];
                            String receiver = str.split(":",-1)[2];
                            String msg = str.split(":",-1)[3];
                            privateChat(sender,receiver,msg);
                            continue;
                        } else if (str.contains("byebye")) {
                            //zhangsan:byebye
                            String userName = str.split(":")[0];
                            userExit(userName);
                            continue;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void userRegister(String userName, Socket client) {
            clientsMap.put(userName,client);
            System.out.println("用户：" + userName+"上线了");
            System.out.println("当前聊天室人数为：" + (clientsMap.size()));
            try {
                PrintStream out = new PrintStream(client.getOutputStream(), true, "UTF-8");
                out.println(userName+"注册成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
        }

        private void GroupChat(String userName,String msg) {
            Set<Map.Entry<String,Socket>> clientSet = clientsMap.entrySet();
            for (Map.Entry<String, Socket> entry : clientSet) {
                Socket socket = entry.getValue();
                PrintStream out = null;
                try {
                    out = new PrintStream(socket.getOutputStream(), true, "UTF-8");
                    out.println("群聊----"+userName+":"+msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void privateChat(String sender,String receiver, String msg){
            Socket client = clientsMap.get(receiver);
            try {
                PrintStream out = new PrintStream(client.getOutputStream(),true,"UTF-8");
                out.println(sender+"："+msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void userExit(String userName) {
            for (String name : clientsMap.keySet()) {
                if (clientsMap.get(name).equals(client)) {
                    userName = name;
                }
            }
            clientsMap.remove(userName);
            System.out.println();
            System.out.println("用户："+userName+"下线了");
            System.out.println("当前聊天室人数为："+(clientsMap.size()));
        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(6666);

        ExecutorService executorService = Executors.newFixedThreadPool(50);

        System.out.println("等待用户连接……");
        for (int i = 0;i < 50;i++) {//源码中默认连接数
            Socket client = serverSocket.accept();
            System.out.println("有新用户连接!端口号为:"+client.getPort());
            System.out.println();

            ExecuteClientRequest executeClientRequest = new ExecuteClientRequest(client);
            executorService.submit(executeClientRequest);
        }
        executorService.shutdown();
        serverSocket.close();
    }
}
