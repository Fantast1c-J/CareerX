package com.careerX;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class PgVectorVectorStoreConfigTest {

    @Autowired
    VectorStore pgVectorVectorStore;

    @Test
    void test() {
        List<Document> documents = List.of(
                new Document("应届生无经验求职困难", Map.of("meta1", "meta1")),
                new Document("社招更困难"),
                new Document("我很牛逼", Map.of("meta2", "meta2")));
        // 添加文档
        pgVectorVectorStore.add(documents);
        // 相似度查询
        List<Document> results = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("应届生找不到工作是因为什么").topK(5).build());
        Assertions.assertNotNull(results);
    }
}
