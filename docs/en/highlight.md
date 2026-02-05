**syntax:**
```java
// Do not specify the highlight tag, and use <em>your highlight content</em> to return the highlighted content by default
highLight(highlightField);
// Specify highlight label
highLight(highlightField,startTag, endTag)
```
```java
    @Test
    public void testHighlight() throws IOException {
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        String keyword = "World";
        wrapper.match(Document::getContent,keyword);
        wrapper.highLight(Document::getContent);
        SearchResponse response = documentMapper.search(wrapper);
        System.out.println(response);
    }
```
> **Tips:**
> If you need to highlight multiple fields, you can separate the fields with commas
> Must use SearchResponse to receive, otherwise there is no highlighted field in the return body

