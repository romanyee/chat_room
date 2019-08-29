package com.bittech.java.client.service;
import com.bittech.java.util.CommUtils;
import com.bittech.java.vo.MessageVO;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: yuisama
 * @Date: 2019-08-19 11:33
 * @Description:
 */
public class CreateGroupGUI {
    private JPanel createGroupPanel;
    private JPanel friendLabelPanel;
    private JTextField groupNameText;
    private JButton conformBtn;

    private String myName;
    private Set<String> friends;
    private Connect2Server connect2Server;
    private FriendsList friendsList;

    public CreateGroupGUI(String myName,
                          Set<String> friends,
                          Connect2Server connect2Server,
                          FriendsList friendsList) {
        this.myName = myName;
        this.friends = friends;
        this.connect2Server = connect2Server;
        this.friendsList = friendsList;
        JFrame frame = new JFrame("创建群组");
        frame.setContentPane(createGroupPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        // 将在线好友以checkBox展示到界面中
        friendLabelPanel.setLayout(new BoxLayout(friendLabelPanel,BoxLayout.Y_AXIS));
        Iterator<String> iterator = friends.iterator();
        while (iterator.hasNext()) {
            String labelName = iterator.next();
            JCheckBox checkBox = new JCheckBox(labelName);
            friendLabelPanel.add(checkBox);
        }
        // 点击提交按钮提交信息到服务端
        conformBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1.判断哪些好友选中加入群聊
                Set<String> selectedFriends = new HashSet<>();
                Component[] comps = friendLabelPanel.getComponents();
                for (Component comp : comps) {
                    JCheckBox checkBox = (JCheckBox) comp;
                    if (checkBox.isSelected()) {
                        String labelName = checkBox.getText();
                        selectedFriends.add(labelName);
                    }
                }
                selectedFriends.add(myName);
                // 2.获取群名输入框输入的群名称
                String groupName = groupNameText.getText();
                // 3.将群名+选中好友信息发送到服务端
                // type:3
                // content:groupName
                // to:[user1,user2,user3...]
                MessageVO messageVO = new MessageVO();
                messageVO.setType("3");
                messageVO.setContent(groupName);
                messageVO.setTo(CommUtils.object2Json(selectedFriends));
                try {
                    PrintStream out = new PrintStream(connect2Server.getOut(),
                            true,"UTF-8");
                    out.println(CommUtils.object2Json(messageVO));
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                // 4.将当前创建群界面隐藏,刷新好友列表界面的群列表
                frame.setVisible(false);
                // addGroupInfo
                // loadGroup
                friendsList.addGroup(groupName,selectedFriends);
                friendsList.loadGroupList();
            }
        });
    }
}
