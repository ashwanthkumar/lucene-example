package in.ashwanthkumar.lucene.example;

import in.ashwanthkumar.utils.collections.Iterables;
import in.ashwanthkumar.utils.func.Function;
import in.ashwanthkumar.utils.io.IO;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * $ java in.ashwanthkumar.lucene.example.CreateIndex &lt;source file&gt; &lt;index directory&gt;
 */
public class CreateIndex {
    private static final Logger LOG = LoggerFactory.getLogger(CreateIndex.class);

    public static void main(String[] args) throws IOException {
        String source = args[0];
        String destination = args[1];
        new CreateIndex().create(source, destination);
    }

    public void create(String source, String destination) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);


        final IndexWriter writer = new IndexWriter(FSDirectory.open(Paths.get(destination)), config);
        List<String> lines = IO.linesFromFile(source);
        Iterables.foreach(lines, new Function<String, Void>() {
            @Override
            public Void apply(String input) {
                addToIndex(input, writer);
                return null;
            }
        });
        LOG.info(String.format("Wrote %d titles to index", lines.size()));
        writer.close();
    }

    private void addToIndex(String input, IndexWriter writer) {
        try {
            Document doc = new Document();
            doc.add(new TextField("title", input, Field.Store.YES));
            writer.addDocument(doc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
