package org.crochet.blog.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for text similarity calculations using TF-IDF and cosine similarity
 */
public class TextSimilarityUtil {

    /**
     * Calculate TF-IDF vectors for a list of documents
     */
    public static Map<String, Map<String, Double>> calculateTFIDFVectors(List<String> documents) {
        // Preprocess documents: tokenize and normalize
        List<List<String>> tokenizedDocs = documents.stream()
                .map(TextSimilarityUtil::tokenizeAndNormalize)
                .collect(Collectors.toList());

        // Calculate term frequencies for each document
        List<Map<String, Double>> tfVectors = tokenizedDocs.stream()
                .map(TextSimilarityUtil::calculateTermFrequency)
                .toList();

        // Calculate IDF for each term
        Map<String, Double> idfMap = calculateInverseDocumentFrequency(tokenizedDocs);

        // Calculate TF-IDF vectors
        Map<String, Map<String, Double>> tfidfVectors = new HashMap<>();
        for (int i = 0; i < documents.size(); i++) {
            Map<String, Double> tfidfVector = new HashMap<>();
            Map<String, Double> tfVector = tfVectors.get(i);

            for (Map.Entry<String, Double> entry : tfVector.entrySet()) {
                String term = entry.getKey();
                double tf = entry.getValue();
                double idf = idfMap.getOrDefault(term, 0.0);
                tfidfVector.put(term, tf * idf);
            }

            tfidfVectors.put(documents.get(i), tfidfVector);
        }

        return tfidfVectors;
    }

    /**
     * Calculate cosine similarity between two TF-IDF vectors
     */
    public static double calculateCosineSimilarity(Map<String, Double> vector1, Map<String, Double> vector2) {
        // Apache Commons Text cosine similarity expects integer frequencies, but TF-IDF produces doubles
        // We'll implement our own cosine similarity calculation for double vectors

        // Get all unique terms from both vectors
        Set<String> allTerms = new HashSet<>();
        allTerms.addAll(vector1.keySet());
        allTerms.addAll(vector2.keySet());

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (String term : allTerms) {
            double val1 = vector1.getOrDefault(term, 0.0);
            double val2 = vector2.getOrDefault(term, 0.0);

            dotProduct += val1 * val2;
            norm1 += val1 * val1;
            norm2 += val2 * val2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0; // Avoid division by zero
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * Find similar documents using TF-IDF and cosine similarity
     */
    public static List<SimilarityResult> findSimilarDocuments(String targetDocument,
                                                            List<String> candidateDocuments,
                                                            int topN) {
        List<String> allDocuments = new ArrayList<>();
        allDocuments.add(targetDocument);
        allDocuments.addAll(candidateDocuments);

        Map<String, Map<String, Double>> tfidfVectors = calculateTFIDFVectors(allDocuments);

        Map<String, Double> targetVector = tfidfVectors.get(targetDocument);
        List<SimilarityResult> results = new ArrayList<>();

        for (String candidate : candidateDocuments) {
            Map<String, Double> candidateVector = tfidfVectors.get(candidate);
            double similarity = calculateCosineSimilarity(targetVector, candidateVector);
            results.add(new SimilarityResult(candidate, similarity));
        }

        // Sort by similarity descending and return top N
        return results.stream()
                .sorted((a, b) -> Double.compare(b.similarity, a.similarity))
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Tokenize and normalize text (simple implementation)
     */
    private static List<String> tokenizeAndNormalize(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return Arrays.stream(text.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", " ") // Remove punctuation
                .split("\\s+"))
                .filter(word -> word.length() > 2) // Filter short words
                .collect(Collectors.toList());
    }

    /**
     * Calculate term frequency for a document
     */
    private static Map<String, Double> calculateTermFrequency(List<String> tokens) {
        Map<String, Integer> termCount = new HashMap<>();
        for (String token : tokens) {
            termCount.put(token, termCount.getOrDefault(token, 0) + 1);
        }

        int totalTerms = tokens.size();
        Map<String, Double> tfMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : termCount.entrySet()) {
            tfMap.put(entry.getKey(), (double) entry.getValue() / totalTerms);
        }

        return tfMap;
    }

    /**
     * Calculate inverse document frequency
     */
    private static Map<String, Double> calculateInverseDocumentFrequency(List<List<String>> tokenizedDocs) {
        int totalDocs = tokenizedDocs.size();
        Map<String, Integer> docFrequency = new HashMap<>();

        // Count documents containing each term
        for (List<String> tokens : tokenizedDocs) {
            Set<String> uniqueTerms = new HashSet<>(tokens);
            for (String term : uniqueTerms) {
                docFrequency.put(term, docFrequency.getOrDefault(term, 0) + 1);
            }
        }

        Map<String, Double> idfMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : docFrequency.entrySet()) {
            String term = entry.getKey();
            int df = entry.getValue();
            idfMap.put(term, Math.log((double) totalDocs / df));
        }

        return idfMap;
    }

    /**
     * Result class for similarity calculations
     */
    public static class SimilarityResult {
        public final String document;
        public final double similarity;

        public SimilarityResult(String document, double similarity) {
            this.document = document;
            this.similarity = similarity;
        }

        @Override
        public String toString() {
            return String.format("SimilarityResult{document='%s', similarity=%.4f}", document, similarity);
        }
    }
}