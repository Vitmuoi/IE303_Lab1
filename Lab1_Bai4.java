import java.util.*;
import java.io.*;
import java.text.Normalizer;

public class Lab1_Bai4 {

    public static Map<String, Integer> vocab = new HashMap<String, Integer>();
    public static Map<String, Integer> corpus = new HashMap<String, Integer>();
    public static Map<String, Integer> pairCorpus = new HashMap<String, Integer>();
    public static Double[] probs;
    public static Double[][] conditionalProbs;

    public static void readFile() {
        try {
            Vector<String> lines = new Vector<String>();
            File file = new File("UIT-ViOCD/UIT-ViOCD.txt");
            Scanner fileScanner = new Scanner(file, "UTF-8");

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                lines.addElement(line);
            }
            fileScanner.close();

            for (String line : lines) {
                // remove line break \n, \r and tab \t
                line = line.replace("\n", "").replace("\r", "").replace("\t", "");
                // remove all leading spaces
                line = line.replaceAll("^\\s+", "");
                // remove all ending spaces
                line = line.replaceAll("\\s+$", "");
                // lowering
                line = line.toLowerCase();
                // normalize Vietnamese characters
                line = Normalizer.normalize(line, Normalizer.Form.NFC);

                // collecting words
                int wordId = vocab.size();
                String[] words = line.split("\\s+");
                for (String word : words) {
                    if (corpus.containsKey(word)) {
                        corpus.put(word, corpus.get(word) + 1);
                    } else {
                        vocab.put(word, wordId);
                        corpus.put(word, 1);
                        wordId += 1;
                    }
                }

                // collecting pairs of words
                for (int i = 0; i < words.length - 1; i++) {
                    String words_ij = words[i] + "_" + words[i + 1];
                    if (pairCorpus.containsKey(words_ij)) {
                        pairCorpus.put(words_ij, pairCorpus.get(words_ij) + 1);
                    } else {
                        pairCorpus.put(words_ij, 1);
                    }
                }
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found!");
            fileNotFoundException.printStackTrace();
        }
    }

    public static void constructSingleProb() {
        // determine the total number of words in the dataset
        int totalWords = 0;
        for (Map.Entry<String, Integer> item : corpus.entrySet()) {
            totalWords += item.getValue();
        }

        // calculating the probability of each word
        probs = new Double[vocab.size()];
        for (Map.Entry<String, Integer> item : corpus.entrySet()) {
            String word = item.getKey();
            Integer wordCount = corpus.get(word);

            Integer wordId = vocab.get(word);

            // determining the P(w)
            if (wordCount >= 5) {
                probs[wordId] = (double) wordCount / totalWords;
            } else {
                probs[wordId] = 0.0;
            }
        }
    }

    public static void constructConditionalProb() {
        // determine the total number of words in the dataset
        int totalPairsOfWords = 0;
        for (Map.Entry<String, Integer> item : pairCorpus.entrySet()) {
            totalPairsOfWords += item.getValue();
        }

        // calculating the probability of each pair of words
        Double[][] jointProbs = new Double[vocab.size()][vocab.size()];
        for (Map.Entry<String, Integer> item_i : corpus.entrySet())
            for (Map.Entry<String, Integer> item_j : corpus.entrySet()) {
                // get the information of word i
                String word_i = item_i.getKey();
                Integer wordId_i = vocab.get(word_i);

                // get the information of word j
                String word_j = item_j.getKey();
                Integer wordId_j = vocab.get(word_j);

                if (word_i.equals(word_j)) {
                    jointProbs[wordId_i][wordId_j] = 1e-20;
                    continue;
                }

                String wordKey_ij = word_i + "_" + word_j;
                jointProbs[wordId_i][wordId_j] = pairCorpus.getOrDefault(wordKey_ij, 0) / (double) totalPairsOfWords;

                String wordKey_ji = word_j + "_" + word_i;
                jointProbs[wordId_j][wordId_i] = pairCorpus.getOrDefault(wordKey_ji, 0) / (double) totalPairsOfWords;
            }

        // calculating the conditional probability of each pair of words
        conditionalProbs = new Double[vocab.size()][vocab.size()];
        for (Map.Entry<String, Integer> item_i : corpus.entrySet())
            for (Map.Entry<String, Integer> item_j : corpus.entrySet()) {
                // get the information of word i
                String word_i = item_i.getKey();
                Integer wordId_i = vocab.get(word_i);

                // get the information of word j
                String word_j = item_j.getKey();
                Integer wordId_j = vocab.get(word_j);

                // determining the P(w_i | w_j)
                if (probs[wordId_j] > 0) {
                    conditionalProbs[wordId_i][wordId_j] = jointProbs[wordId_i][wordId_j] / probs[wordId_j];
                } else { // determining the P(w_j | w_i)
                    conditionalProbs[wordId_i][wordId_j] = 1e-20;
                }
            }
    }

    public static void training() {
        constructSingleProb();
        constructConditionalProb();
    }

    public static Vector<String> inferring(String w0) {
        Vector<String> words = new Vector<String>();
        words.add(w0);

        Integer w0Idx = vocab.get(w0);
        if (w0Idx == null || probs[w0Idx] == null || probs[w0Idx] == 0.0) {
            System.out.println("Từ không có trong từ điển hoặc không có xác suất.");
            return words;
        }

        Double logProbs = -Math.log(probs[w0Idx]);
        for (int t = 1; t <= 5; t++) {
            // determine the word that gives the highest probability P(w0, w1, ..., wt)
            String maxWord = "";
            Double maxLogProb = Double.NEGATIVE_INFINITY;
            Integer w1Idx = 0;
            for (Map.Entry<String, Integer> item : vocab.entrySet()) {
                w1Idx = item.getValue();
                Double prob = conditionalProbs[w0Idx][w1Idx];
                if (prob > maxLogProb) {
                    maxWord = item.getKey();
                    maxLogProb = prob;
                }
            }
            logProbs += -Math.log(maxLogProb);
            words.add(maxWord);
            w0Idx = vocab.get(maxWord);
        }
        return words;
    }

    public static void main(String[] args) throws Exception {
        readFile();
        training();
        Vector<String> predicted_words = inferring("hàng");
        String sentence = String.join(" ", predicted_words);
        System.out.println(sentence);
    }
}