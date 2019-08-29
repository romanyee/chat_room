package com.bittech.java.util;
import java.util.HashSet;
import	java.util.Properties;
import java.util.Set;

import com.bittech.java.client.entity.User;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommUtilsTest {

    @Test
    public void loadProperties() {
        String fileName = "datasource.properties";
        Properties properties = CommUtils.loadProperties(fileName);
        Assert.assertNotNull(properties);
    }

    @Test
    public void object2Json() {
        User user = new User();
        user.setId(1);
        user.setUserName("test");
        user.setPassword("123");
        user.setBrief("帅");
        Set<String> strings = new HashSet<>();
        strings.add("test2");
        strings.add("test3");
        strings.add("test4");
        user.setUserNames(strings);
        String str = CommUtils.object2Json(user);
        System.out.println(str);
    }

    @Test
    public void json2Object() {
        Set<String> strings = new HashSet<>();
        strings.add("test2");
        strings.add("test3");
        strings.add("test4");
        System.out.println(strings);
    }
}