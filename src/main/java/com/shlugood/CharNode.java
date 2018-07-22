package com.shlugood;

import java.util.*;

/**
 * 词典树分段，表示词典树的一个分枝
 */
public class CharNode implements Comparable<CharNode> {

    // 数组大小上限
    private static final int ARRAY_LENGTH_LIMIT = 3;

    // Map存储结构
    private Map<Character, CharNode> childrenMap;
    // 数组方式存储结构
    private CharNode[] childrenArray;

    // 当前节点上存储的字符
    private final Character nodeChar;
    // 当前节点存储的Segment数目
    // storeSize <=ARRAY_LENGTH_LIMIT ，使用数组存储， storeSize >ARRAY_LENGTH_LIMIT
    // ,则使用Map存储
    private int storeSize = 0;
    /**
     * 当前词的词频。当==0时，表示该词不是一个单词
     */
    private int wordFrequency = 0;

    public CharNode() {
        this.nodeChar = null;
    }

    private CharNode(Character nodeChar) {
        if (nodeChar == null) {
            throw new IllegalArgumentException("参数为空异常，字符不能为空");
        }
        this.nodeChar = nodeChar;
    }


    public List<HitWord> match(char[] chars, int beginIndex) {
        List<HitWord> result = new ArrayList<>();
        int count = chars.length;
        if (beginIndex >= count - 1) {
            return result;
        }
        CharNode charNode = this;
        for (int i = beginIndex; i < count; i++) {
            CharNode tmpCharNode = charNode.searchChild(chars[i]);
            if (tmpCharNode == null) {
                break;
            }
            charNode = tmpCharNode;
            if (tmpCharNode.wordFrequency > 0) {
                HitWord hitWord = new HitWord(i, tmpCharNode.wordFrequency);
                result.add(hitWord);
            }
        }
        return result;
    }


    /**
     * 在节点中填入单词
     *
     * @param charArray     字符串
     * @param wordFrequency 词频
     */
    public void fillSegment(char[] charArray, int wordFrequency) {
        int count = charArray.length;
        CharNode charNode = this;
        for (int i = 0; i < count; i++) {
            CharNode child = charNode.searchOrAddChild(charArray[i]);
            charNode = child;
        }
        charNode.wordFrequency += wordFrequency;
    }

    /**
     * 查找本节点下对应的keyChar的segment *
     *
     * @param keyChar
     * @return
     */
    private CharNode searchOrAddChild(Character keyChar) {
        CharNode ds = null;
        if (this.storeSize <= ARRAY_LENGTH_LIMIT) {
            // 获取数组容器，如果数组未创建则创建数组
            CharNode[] segmentArray = getChildrenArray();
            // 搜寻数组
            CharNode keySegment = new CharNode(keyChar);
            int position = Arrays.binarySearch(segmentArray, 0, this.storeSize, keySegment);
            if (position >= 0) {
                ds = segmentArray[position];
            }
            // 遍历数组后没有找到对应的segment
            if (ds == null) {
                ds = keySegment;
                if (this.storeSize < ARRAY_LENGTH_LIMIT) {
                    // 数组容量未满，使用数组存储
                    segmentArray[this.storeSize] = ds;
                    // segment数目+1
                    this.storeSize++;
                    Arrays.sort(segmentArray, 0, this.storeSize);

                } else {
                    // 数组容量已满，切换Map存储
                    // 获取Map容器，如果Map未创建,则创建Map
                    Map<Character, CharNode> segmentMap = getChildrenMap();
                    // 将数组中的segment迁移到Map中
                    migrate(segmentArray, segmentMap);
                    // 存储新的segment
                    segmentMap.put(keyChar, ds);
                    // segment数目+1 ， 必须在释放数组前执行storeSize++ ， 确保极端情况下，不会取到空的数组
                    this.storeSize++;
                    // 释放当前的数组引用
                    this.childrenArray = null;
                }
            }
        } else {
            // 获取Map容器，如果Map未创建,则创建Map
            Map<Character, CharNode> segmentMap = getChildrenMap();
            // 搜索Map
            ds = (CharNode) segmentMap.get(keyChar);
            if (ds == null) {
                // 构造新的segment
                ds = new CharNode(keyChar);
                segmentMap.put(keyChar, ds);
                // 当前节点存储segment数目+1
                this.storeSize++;
            }
        }
        return ds;
    }

    private CharNode searchChild(Character keyChar) {
        if (this.storeSize > ARRAY_LENGTH_LIMIT) {
            Map<Character, CharNode> segmentMap = getChildrenMap();
            return segmentMap.get(keyChar);
        }
        CharNode[] segmentArray = getChildrenArray();
        CharNode keySegment = new CharNode(keyChar);
        int position = Arrays.binarySearch(segmentArray, 0, this.storeSize, keySegment);
        if (position < 0) {
            return null;
        }
        return segmentArray[position];
    }


    /**
     * 获取数组容器 线程同步方法
     */
    private CharNode[] getChildrenArray() {
        if (this.childrenArray == null) {
            synchronized (this) {
                if (this.childrenArray == null) {
                    this.childrenArray = new CharNode[ARRAY_LENGTH_LIMIT];
                }
            }
        }
        return this.childrenArray;
    }


    /**
     * 获取Map容器 线程同步方法
     */
    private Map<Character, CharNode> getChildrenMap() {
        if (this.childrenMap == null) {
            synchronized (this) {
                if (this.childrenMap == null) {
                    this.childrenMap = new HashMap<Character, CharNode>(ARRAY_LENGTH_LIMIT * 2, 0.8f);
                }
            }
        }
        return this.childrenMap;
    }


    /**
     * 将数组中的segment迁移到Map中
     *
     * @param segmentArray
     */
    private void migrate(CharNode[] segmentArray, Map<Character, CharNode> segmentMap) {
        for (CharNode segment : segmentArray) {
            if (segment != null) {
                segmentMap.put(segment.nodeChar, segment);
            }
        }
    }

    @Override
    public int compareTo(CharNode o) {
        return this.nodeChar.compareTo(o.nodeChar);
    }


}