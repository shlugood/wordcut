# wordcut
一个基于词典的前缀扫描中文分词

中文分词是很多文本分析的基础。最近一个项目，输入一个地址，需要识别出地址中包含的省市区街道等单词。与以往的分词技术不同。jieba/hanlp等常用的分词技术，除了基于词典，还有基于隐马尔科夫/条件随机场等机器学习技术对未登录词的分词，有一定的概率性。而我们所使用的地址识别，要求必须基于词库进行精确的分词。这些比较高级的分词技术反而成为了不必要的风险。

另外还有一个原因是，流行的分词技术对多用户词典和词典的动态管理支持也不是很好。本项目就实现了一个可以多词典间相互隔离的分词工具。

基于前缀词典树的中文分词概念简单。其中使用到的trie树/有向无环图（dag）/动态规划计算最长路径等算法，可以说是教科书一样的例子。所以我就自己实现了一遍，测试下来效果还不错。

算法是pom项目。测试用例如下。词典需放在resources目录下。

```java
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
```
