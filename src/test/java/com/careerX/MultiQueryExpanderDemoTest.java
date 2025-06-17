package com.careerX;

import com.careerX.demo.rag.MultiQueryExpanderDemo;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MultiQueryExpanderDemoTest {
    @Resource
    private MultiQueryExpanderDemo multiQueryExpanderDemo;
    @Test
    public void test() {
        String query = "谁是fanfan";
        List<Query> queries = multiQueryExpanderDemo.expand(query);
        Assertions.assertNotNull(queries);
    }
}
