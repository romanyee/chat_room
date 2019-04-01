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
    //使用
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
//                        G:Hello i am ...
                            String msg = str.split(":")[1];
                            GroupChat(msg);
                            continue;
                        } else if (str.startsWith("P:")) {
//                        私聊实现：
//                        P:zhangsan-hello i am ...
                            String tempMsg = str.split(":")[1];
                            String userName = tempMsg.split("-")[0];
                            String msg = tempMsg.split("-")[1];
                            privateChat(userName, msg);
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
                out.println("用户注册成功！");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void GroupChat(String msg) {
            Set<Map.Entry<String,Socket>> clientSet = clientsMap.entrySet();
            for (Map.Entry<String, Socket> entry : clientSet) {
                Socket socket = entry.getValue();
                PrintStream out = null;
                try {
                    out = new PrintStream(socket.getOutputStream(), true, "UTF-8");
                    out.println("群聊信息为："+msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void privateChat(String userName, String msg){
            Socket client = clientsMap.get(userName);
            try {
                PrintStream out = new PrintStream(client.getOutputStream(),true,"UTF-8");
                out.println(client.getPort()+"私聊信息为："+msg);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        private void userExit(String userName) {
            clientsMap.remove(userName);
            System.out.println("用户："+userName+"下线了");
            System.out.println("当前聊天室人数为："+clientsMap.size());
        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(6666);
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        System.out.println("等待用户连接");
        for (int i = 0;i < 20;i++) {
            Socket client = serverSocket.accept();
            System.out.println("有新用户连接!端口号为:"+client.getPort());
            ExecuteClientRequest executeClientRequest = new ExecuteClientRequest(client);
            executorService.submit(executeClientRequest);
        }
        executorService.shutdown();
        serverSocket.close();
    }
}
