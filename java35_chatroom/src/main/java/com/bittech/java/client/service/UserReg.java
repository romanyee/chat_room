package com.bittech.java.client.service;

import com.bittech.java.client.dao.AccountDao;
import com.bittech.java.client.entity.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @Author: yuisama
 * @Date: 2019-08-12 10:47
 * @Description:
 */
public class UserReg {
    private JPanel userRegPanel;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JTextField brifeText;
    private JButton regBtn;
    private AccountDao accountDao = new AccountDao();

    public UserReg() {
        JFrame frame = new JFrame("用户注册");
        frame.setContentPane(userRegPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        // 点击注册按钮,将信息持久化到db中,成功弹出提示框
        regBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 获取用户输入的注册信息
                String userName = userNameText.getText();
                String password = String.valueOf
                        (passwordText.getPassword());
                String brief = brifeText.getText();
                // 将输入信息包装为User类，保存到数据库中
                User user = new User();
                user.setUserName(userName);
                user.setPassword(password);
                user.setBrief(brief);
                // 调用dao对象
                if (accountDao.userReg(user)) {
                    // 返回登录页面
                    JOptionPane.showMessageDialog(frame,"注册成功!",
                            "提示信息",JOptionPane.INFORMATION_MESSAGE);
                    frame.setVisible(false);
                }else {
                    // 保留当前注册页面
                    JOptionPane.showMessageDialog(frame,"注册失败!",
                            "错误信息",JOptionPane.ERROR_MESSAGE);
                }
            }
        });

    }
}
