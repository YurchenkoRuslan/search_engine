package searchengine.dto.indexing;

import lombok.Data;

@Data
public class UnsuccessfulResult {
    private boolean result;
    private String message;
}
