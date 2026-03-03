package com.example.langchain4jdemo.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.data.document.Metadata.metadata;

/**
 * Easy RAG 配置
 * <p>
 * 1. 存储：加载 /resources/university 下的知识文档，切割、向量化并入库
 * 2. 检索：构建 ContentRetriever 供 AiService 使用
 */
@Configuration
public class RagConfig {

    private static final Logger log = LoggerFactory.getLogger(RagConfig.class);
    private static final String UNIVERSITY_PATH = "university";
    private static final int MAX_SEGMENT_CHARS = 500;
    private static final int MAX_OVERLAP_CHARS = 50;
    private static final int MAX_RESULTS = 5;
    private static final double MIN_SCORE = 0.6;

    /**
     * 构建向量数据库操作对象（内存存储）
     */
    @Bean
    public EmbeddingStore<TextSegment> universityEmbeddingStore(EmbeddingModel embeddingModel) throws IOException {
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // 加载知识数据文档
        List<Document> documents = loadUniversityDocuments();
        if (documents.isEmpty()) {
            log.warn("未找到 university 目录下的文档，RAG 知识库为空");
            return embeddingStore;
        }

        log.info("加载到 {} 个文档，开始向量化并存储...", documents.size());

        // 封装了文档切割、向量化并存储到向量数据库的整个流程
        EmbeddingStoreIngestor.builder()
                // 递归切割文档，参数1：每个片段的最大字符数，参数2：片段之间的重叠字符数
                .documentSplitter(DocumentSplitters.recursive(MAX_SEGMENT_CHARS, MAX_OVERLAP_CHARS))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build()
                .ingest(documents);

        log.info("RAG 向量库构建完成，共 {} 个文档", documents.size());
        return embeddingStore;
    }

    /**
     * 构建向量数据库检索对象，配置 maxResults、minScore
     */
    @Bean("universityContentRetriever")
    public ContentRetriever universityContentRetriever(
            EmbeddingStore<TextSegment> universityEmbeddingStore,
            EmbeddingModel embeddingModel) {

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(universityEmbeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(MAX_RESULTS)
                .minScore(MIN_SCORE)
                .build();
    }

    /**
     * 从 classpath:university/ 加载所有 .md 文档
     */
    private List<Document> loadUniversityDocuments() throws IOException {
        List<Document> documents = new ArrayList<>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:" + UNIVERSITY_PATH + "/*.md");

        for (Resource resource : resources) {
            if (!resource.isReadable()) continue;
            try (InputStream is = resource.getInputStream()) {
                String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String filename = resource.getFilename();
                if (filename != null && !text.isBlank()) {
                    documents.add(Document.from(text, metadata("source", filename)));
                }
            }
        }

        return documents;
    }
}
