package com.xpc.easyes.sample.test.nested;

import com.xpc.easyes.core.conditions.LambdaEsQueryWrapper;
import com.xpc.easyes.core.toolkit.FieldUtils;
import com.xpc.easyes.sample.entity.Document;
import com.xpc.easyes.sample.entity.User;
import com.xpc.easyes.sample.mapper.DocumentMapper;
import com.xpc.easyes.sample.test.TestEasyEsApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * 嵌套测试
 * <p>
 * Copyright © 2022 xpc1024 All Rights Reserved
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestEasyEsApplication.class)
public class NestedTest {
    @Resource
    private DocumentMapper documentMapper;

    @Test
    public void testNestedMatch() {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.match(Document::getContent, "人才");
        wrapper.nestedMatch(Document::getUsers, FieldUtils.val(User::getUsername), "用户");
        List<Document> documents = documentMapper.selectList(wrapper);
        System.out.println(documents);
    }
}
