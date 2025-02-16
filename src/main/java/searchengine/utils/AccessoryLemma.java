package searchengine.utils;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AccessoryLemma implements Comparable<AccessoryLemma>{
    private String word;
    private int frequency;

    @Override
    public int compareTo(AccessoryLemma lemma) {
        return frequency - lemma.frequency;
    }
}
