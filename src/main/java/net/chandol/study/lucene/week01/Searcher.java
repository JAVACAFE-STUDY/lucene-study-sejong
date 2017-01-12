package net.chandol.study.lucene.week01;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Searcher {
    private static final String INDEX_PATH = "lucene/index";

    public void search(String q) throws IOException, ParseException {
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(INDEX_PATH)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();

        String field = "contents";
        QueryParser parser = new QueryParser(field, analyzer);
        Query query = parser.parse(q);

        TopDocs docs = searcher.search(query, 10);

        System.out.println("\nFound " + docs.totalHits + " in query" + q + "':");

        for (ScoreDoc scoreDoc : docs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            System.out.println("  " + doc.get("path"));
        }
    }
}