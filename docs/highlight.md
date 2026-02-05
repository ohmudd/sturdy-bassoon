语法:
```java
// 不指定高亮标签,默认采用<em></em>返回高亮内容
highLight(高亮字段);
// 指定高亮标签
highLight(高亮字段,开始标签,结束标签)
```
```java
    @Test
    public void testHighlight() throws IOException {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        String keyword = "过硬";
        wrapper.match(Document::getContent,keyword);
        wrapper.highLight(Document::getContent);
        SearchResponse response = documentMapper.search(wrapper);
        System.out.println(response);
    }
```
> **Tips:**
> - 如果需要多字段高亮,则字段与字段之间可以用逗号隔开
> - 必须使用SearchResponse接收,否则返回体中无高亮字段
> 

