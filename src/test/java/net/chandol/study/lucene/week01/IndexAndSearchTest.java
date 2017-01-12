package net.chandol.study.lucene.week01;

import org.junit.Before;
import org.junit.Test;

public class IndexAndSearchTest {
    private Indexer indexer;
    private Searcher searcher;

    @Before
    public void init(){
        indexer = new Indexer();
        searcher = new Searcher();
    }


    @Test
    public void indexAndSearch() throws Exception {
        // given
        String query = "apache";

        // when, then
        indexer.index();
        searcher.search(query);
    }

}