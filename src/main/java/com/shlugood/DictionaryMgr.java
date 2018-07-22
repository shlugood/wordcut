package com.shlugood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictionaryMgr {
    private static DictionaryMgr ourInstance = new DictionaryMgr();

    public static DictionaryMgr getInstance() {
        return ourInstance;
    }

    private DictionaryMgr() {
    }

    /**
     * 字典map，key为字典名称，value为字典root
     */
    private HashMap<String, CharNode> dictionaryMap = new HashMap<>();

    public void createFromMap(Map<String, Integer> wordFrequencyMap, String dictionaryName){
        if(dictionaryMap.containsKey(dictionaryName)){
            throw new RuntimeException("duplicate dictionary name");
        }
        CharNode root = new CharNode();
        dictionaryMap.put(dictionaryName, root);
        for(Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()){
            char[] chars = entry.getKey().toCharArray();
            int frequency = entry.getValue();
            root.fillSegment(chars, frequency);
        }
    }

    public List<HitWord> match(char[] chars, int beginIndex, String dictionaryName) {
        CharNode root = this.dictionaryMap.get(dictionaryName);
        if(root == null){
            return new ArrayList<>();
        }
        List<HitWord> match = root.match(chars, beginIndex);
        for (HitWord hitWord : match) {
            hitWord.setFrequency(Math.log(hitWord.getFrequency()));
        }
        return match;
    }
}
