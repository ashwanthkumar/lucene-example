package in.ashwanthkumar.lucene.example;

import in.ashwanthkumar.utils.func.Function;
import in.ashwanthkumar.utils.func.Functions;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import static in.ashwanthkumar.utils.collections.Iterables.foreach;
import static in.ashwanthkumar.utils.collections.Lists.map;

/**
 * $ java in.ashwanthkumar.lucene.example.Search &lt;indexLocation from CreateIndex&gt; &lt;search term&gt;
 */
public class Search {
    private static final Logger LOG = LoggerFactory.getLogger(CreateIndex.class);
    public static final int MAX_RESULTS = 10;

    public static void main(String[] args) throws IOException, ParseException {
        String indexLocation = args[0];
        String searchString = args[1];

        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation)));
        final IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();

        QueryParser parser = new QueryParser("title", analyzer);
        TopDocs result = searcher.search(parser.parse(searchString), MAX_RESULTS);
        foreach(
                map(Arrays.asList(result.scoreDocs), extractWord(searcher)),
                Functions.<String>STDOUT()
        );

        reader.close();
    }

    private static Function<ScoreDoc, String> extractWord(final IndexSearcher searcher) {
        return new Function<ScoreDoc, String>() {
            @Override
            public String apply(ScoreDoc input) {
                try {
                    return searcher.doc(input.doc).get("title");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
