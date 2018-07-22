package com.shlugood;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class WordCuter {
    private static DictionaryMgr dictionaryMgr = DictionaryMgr.getInstance();

    private String dictionaryKey = UUID.randomUUID().toString();


    public static WordCuter createInstance(String fileName) throws URISyntaxException {
        Map<String, Integer> mapFromFile = getMapFromFile(fileName);
        WordCuter wordCuter = new WordCuter();
        dictionaryMgr.createFromMap(mapFromFile, wordCuter.dictionaryKey);
        return wordCuter;
    }

    private List<List<HitWord>> createDag(String sentence){
        List<List<HitWord>> result = new ArrayList<>();
        char[] chars = sentence.toCharArray();
        int count = chars.length;
        for(int i = 0; i < count; i++){
            List<HitWord> matchList = dictionaryMgr.match(chars, i, this.dictionaryKey);
            result.add(matchList);
        }
        return result;
    }


    private Map<Integer, Integer> calculateRouter(List<List<HitWord>> dag){
        int count = dag.size();
        Map<Integer, Integer> router = new HashMap<>(count);
        double[] frequencies = new double[count + 1];
        for(int i = count - 1; i >= 0; i--){
            List<HitWord> list = dag.get(i);
            if(list.isEmpty()){
                frequencies[i] = frequencies[i + 1];
                continue;
            }
            for (HitWord hitWord : list) {
                int endIndex = hitWord.getEndIndex();
                double tmpFrequency = hitWord.getFrequency() + frequencies[endIndex + 1];
                if(tmpFrequency > frequencies[i]){
                    frequencies[i] = tmpFrequency;
                    router.put(i, endIndex);
                }
            }
        }
        return router;
    }

    private List<String> cutFromRouter(String sentence, Map<Integer, Integer> router){
        List<String> result = new ArrayList<>();
        int count = sentence.length();
        for(int i = 0; i < count; i++){
            int j = i;
            if(router.containsKey(i)){
                j = router.get(i);
            }
            String word = sentence.substring(i, j + 1);
            i = j;
            result.add(word);
        }
        return  result;
    }

    public List<String> cut(String sentence){
        List<List<HitWord>> dag = this.createDag(sentence);
        Map<Integer, Integer> router = this.calculateRouter(dag);
        return this.cutFromRouter(sentence, router);
    }

    private static Map<String, Integer> getMapFromFile(String fileName) throws URISyntaxException {
        Map<String, Integer> map = new HashMap<>();
        URI uri = ClassLoader.getSystemResource(fileName).toURI();
        String mainPath = Paths.get(uri).toString();
        Path path = Paths.get(mainPath );
        try (Stream<String> stream = Files.lines(path)) {
            stream.forEach(line -> {
                String[] split = line.split(" ");
                if(split == null || split.length < 2){
                    return;
                }
                String word = split[0];
                Integer frequency = Integer.getInteger(split[1], 5);
                map.put(word, frequency);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }

}
