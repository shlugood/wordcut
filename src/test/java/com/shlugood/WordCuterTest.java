package com.shlugood;

import org.junit.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WordCuterTest {

    @Test
    public void cut() throws URISyntaxException {
        System.out.println("hello world");
        WordCuter wordCuter = WordCuter.createInstance("dic.txt");
        for(String sentence : testSentence()){
            List<String> words = wordCuter.cut(sentence);
            System.out.println(words);
            assertTrue(words.size() <= sentence.length());
        }
    }

    private List<String> testSentence(){
        List<String> list = new ArrayList<>(22);
        list.add("北京顺义区李桥镇馨港庄园8区80号楼123单元201");
        list.add("浙江杭州市余杭区五常街道西溪庭院98—2—444");
        list.add("广东深圳市宝安区松岗街道松岗镇溪头村委西六十七巷一百二十三号");

        return list;
    }
}