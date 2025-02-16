package searchengine.dto.indexing;


//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class IndexingResult {
    private boolean result;
    private String message;

    public IndexingResult(boolean result) {
        this.result = result;
    }

    public IndexingResult(boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}
