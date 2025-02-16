package searchengine.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;

@Slf4j
public class TextHandler {

    private static final LuceneMorphology russianMorphology;
    public static final String WORD_SEPARATOR_REGEX = "[^а-яА-Я]";
    public static final String SPACE_REGEX = "[ \t]+";

    static {
        try {
            russianMorphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] getWordArray(String text) {
        return text.split(WORD_SEPARATOR_REGEX);
    }

    public static String getLemma(String word) {
        try {
            return russianMorphology.getNormalForms(word.toLowerCase()).get(0);
        } catch (Exception e) {
            return word;
        }
    }


    //todo метод возвращает HashMap<String, Integer>, ключи которого - леммы, значения - их количества
    public static Map<String, Integer> getLemmas(String text) {
        text = text.toLowerCase();
        Map<String,Integer> lemmas = new HashMap<>();

        String[] words = text.split(WORD_SEPARATOR_REGEX);

        for(String word : words) {
            if (!isIndependentWord(word)) continue;

            String lemma = russianMorphology.getNormalForms(word).get(0);
            if (lemmas.containsKey(lemma)) {
                lemmas.put(lemma, lemmas.get(lemma) + 1);
            } else {
                lemmas.put(lemma, 1);
            }
        }

        return lemmas;
    }

    private static boolean isIndependentWord(String word){
        if (word.isEmpty()) return false;
//        try {
        String morphInfo = russianMorphology.getMorphInfo(word).get(0);
        return !(morphInfo.contains("МЕЖД") || morphInfo.contains("СОЮЗ") ||
                morphInfo.contains("ЧАСТ") || morphInfo.contains("ПРЕДЛ"));
//        } catch (Exception e) {
//            return false;
//        }
    }

    public static String clearHtmlTags(String htmlText) {
        StringBuilder result = new StringBuilder();
        boolean tagOpen = false;

        for(char ch : htmlText.toCharArray()){
            if (ch == '>') {
                result.append(' ');
                tagOpen = false;
                continue;
            }
            if (tagOpen) continue;
            if (ch == '<') {
                tagOpen = true;
                continue;
            }
            result.append(ch);
        }
        return result.toString();
    }

    public static String getSnippet(String HtmlText, List<String> lemmas) {
//        System.out.println(HtmlText);

        if(HtmlText.isEmpty() || lemmas.isEmpty()) {
            log.info("Совпадений нет!");
            return null;
        }

        final int maxAmountWords = lemmas.size() + 20;
        final int boundLength = 5;
        int currentCount = 0;
        boolean flagForSaving = false;
        final String text = clearHtmlTags(HtmlText);
        final String[] words = text.split(SPACE_REGEX);

        List<Integer> indexesOfFoundWords = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if (wordIsMatchesOfLemmas(words[i], lemmas)) {
                indexesOfFoundWords.add(i);
            }
        }

//        log.info("Количество совпадений с леммами: " + indexesOfFoundWords.size());
        if (indexesOfFoundWords.isEmpty()) {
            log.info("Совпадений нет!");
            return null;
        }

        int indexByFoundWords = 0;
        int startIndex = indexesOfFoundWords.get(0);
        int leftBound = startIndex - boundLength;
        int rightBound = startIndex + boundLength;

        int lemmaCountInSnippet = 0;
        int maxCount = 0;
        String snippet = "";
        StringBuilder snippetBuilder = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            if (i >= leftBound && i <= rightBound) {
                flagForSaving = true;

                if (indexesOfFoundWords.contains(i)) {
                    snippetBuilder.append("<b>");
                    snippetBuilder.append(words[i]);
                    snippetBuilder.append("</b>");
                    indexByFoundWords++;
                    lemmaCountInSnippet++;
                    leftBound = i - boundLength;
                    rightBound = i + boundLength;

                } else {
                    snippetBuilder.append(words[i]);
                }
                snippetBuilder.append(" ");
                currentCount++;
            }

            if (i > rightBound || currentCount >= maxAmountWords) { //i > rightBound && flagForSaving
                rightBound = 0;
                flagForSaving = false;
                if (lemmaCountInSnippet > maxCount) {
                    snippet = snippetBuilder.toString();
                    maxCount = lemmaCountInSnippet;
                }
                lemmaCountInSnippet = 0;

                snippetBuilder = new StringBuilder();
                currentCount = 0;
                i = indexesOfFoundWords.contains(i) ? i - 1 : i;

                if (indexByFoundWords < indexesOfFoundWords.size()) { //если массив с индексами лемм не перебрали
                    leftBound = indexesOfFoundWords.get(indexByFoundWords) - boundLength;
                    rightBound = indexesOfFoundWords.get(indexByFoundWords) + boundLength;
                }
            }
        }
        return flagForSaving && lemmaCountInSnippet > maxCount ? snippetBuilder.toString() : snippet;
    }

    public static boolean wordIsMatchesOfLemmas(String word, List<String> lemmas) {
        word = word.toLowerCase().replaceAll("[^а-я]", "");
        word = getLemma(word);
        for (String lemma : lemmas) {
            if (lemma.equals(word)) return true;
        }
        return false;
    }

}
