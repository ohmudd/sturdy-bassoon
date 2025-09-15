> For field sorting, ascending sorting and descending sorting are supported:

```java
wrapper.orderByDesc(Sort fields, support multiple fields)
wrapper.orderByAsc(Sort fields, support multiple fields)
```
Example of use:
```java
    @Test
    public void testSort(){
        // To test the sorting, we added a creation time field to the Document object, updated the index, and added two pieces of data
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.like(Document::getContent,"Hello");
        wrapper.select(Document::getTitle,Document::getGmtCreate);
        List<Document> before = documentMapper.selectList(wrapper);
        System.out.println("before:"+before);
        wrapper.orderByDesc(Document::getGmtCreate);
        List<Document> desc = documentMapper.selectList(wrapper);
        System.out.println("desc:"+desc);
    }
```
