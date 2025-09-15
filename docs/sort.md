针对字段的排序,支持升序排序和降序排序:
```java
// 降序排列
wrapper.orderByDesc(排序字段,支持多字段)
// 升序排列
wrapper.orderByAsc(排序字段,支持多字段)
```
使用示例:
```java
    @Test
    public void testSort(){
        // 测试排序 为了测试排序,我们在Document对象中新增了创建时间字段,更新了索引,并新增了两条数据
        LambdaEsQueryWrapper<Document> wrapper = new LambdaEsQueryWrapper<>();
        wrapper.likeRight(Document::getContent,"推");
        wrapper.select(Document::getTitle,Document::getGmtCreate);
        List<Document> before = documentMapper.selectList(wrapper);
        System.out.println("before:"+before);
        wrapper.orderByDesc(Document::getGmtCreate);
        List<Document> desc = documentMapper.selectList(wrapper);
        System.out.println("desc:"+desc);
    }
```
效果:<br />![1](https://iknow.hs.net/8730de70-29af-4279-9d40-43baa363a95b.png)
