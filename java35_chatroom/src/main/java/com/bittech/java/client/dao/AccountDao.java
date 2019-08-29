package com.bittech.java.client.dao;

import com.bittech.java.client.entity.User;
import org.apache.commons.codec.digest.DigestUtils;

import javax.swing.*;
import java.sql.*;

/**
 * @Author: yuisama
 * @Date: 2019-08-12 09:57
 * @Description:
 */
public class AccountDao extends BasedDao{
    // 用户注册 insert
    public boolean userReg(User user) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            String sql = "INSERT INTO user(username, password,brief) " +
                    " VALUES(?,?,?) ";
            statement = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,user.getUserName());
            statement.setString(2, DigestUtils.md5Hex(user.getPassword()));
            statement.setString(3,user.getBrief());
            int rows = statement.executeUpdate();
            if (rows == 1)
                return true;
        }catch (SQLException e) {
            System.err.println("用户注册失败");
            e.printStackTrace();
        }finally {
            closeResources(connection,statement);
        }
        return false;
    }

    public User userLogin(String userName,String password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1,userName);
            statement.setString(2,DigestUtils.md5Hex(password));
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                User user = getUser(resultSet);
                return user;
            }
        }catch (SQLException e) {
            System.err.println("用户登录失败");
            e.printStackTrace();
        }finally {
            closeResources(connection,statement,resultSet);
        }
        return null;
    }

    private User getUser(ResultSet resultSet) throws SQLException{
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUserName(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setBrief(resultSet.getString("brief"));
        return user;
    }
}
