package net.chandol.study.lucene.week01;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class Indexer {
    private static final String INDEX_PATH = "lucene/index";
    private static final String DOC_PATH = "lucene/doc";

    public void index() throws IOException {
        // index writer 열어줍니다.
        IndexWriter writer = createIndexWriter(Paths.get(INDEX_PATH));

        // index를 생성합니다.
        indexDocs(writer, Paths.get(DOC_PATH));

        // writer를 닫습니다.
        writer.close();
    }

    // index Writer를 생성한다.
    private IndexWriter createIndexWriter(Path indexPath) throws IOException {
        Directory indexDir = FSDirectory.open(indexPath);

        // 기본 Analyzer를 사용
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        // 만약 파일이 존재하는 경우 create + append
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

        // index writer 생성
        return new IndexWriter(indexDir, iwc);
    }

    // 입력된 파일List를 index처리한다.
    static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        // filePath를 기준으로 문서를 가져온 다음
        List<Path> filePaths = getAllFilesPath(path);

        // 순회를 하면서 최종 수정시간을 가져온다.
        filePaths.forEach(p -> indexDoc(writer, p, getLastModifiedTime(p)));
    }

    private static void indexDoc(IndexWriter writer, Path file, long lastModified) {
        try (InputStream stream = Files.newInputStream(file)) {
            Document doc = new Document();
            Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            doc.add(pathField);
            doc.add(new LongPoint("modified", lastModified));
            doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

            if (writer.getConfig().getOpenMode() == IndexWriterConfig.OpenMode.CREATE) {
                System.out.println("adding " + file);
                writer.addDocument(doc);
            } else {
                System.out.println("updating " + file);
                writer.updateDocument(new Term("path", file.toString()), doc);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static long getLastModifiedTime(Path p) {
        try {
            return Files.getLastModifiedTime(p).toMillis();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Path> getAllFilesPath(Path path) throws IOException {
        List<Path> filePaths = new ArrayList<>();

        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path internalPath, BasicFileAttributes attrs) throws IOException {
                    filePaths.add(internalPath);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            filePaths.add(path);
        }

        return filePaths;
    }

}