> **前言:**ES难用,索引首当其冲,索引的创建不仅复杂,而且难于维护,一旦索引有变动,就必须面对索引重建带来的服务停机和数据丢失等问题,尽管ES官方提供了索引别名机制来解决问题,但门槛依旧很高,步骤繁琐,在生产环境中由人工操作非常容易出现失误带来严重的问题,为了解决这些痛点,Easy-Es提供了多种策略,将用户彻底从索引的维护中解放出来.
> 其中全自动平滑模式,首次采用全球领先的"哥哥你不用动,EE我全自动"的模式,索引的创建,更新,数据迁移等所有全生命周期均无需用户介入,由EE全自动完成,过程零停机,连索引类型都可智能自动推断,是全球开源首创,充分借鉴了JVM垃圾回收算法思想,史无前例,尽管网上已有平滑过渡方案,但并非全自动,过程依旧靠人工介入,即便是SpringData也没有---来自国产开源的蜜汁自信,请允许我再多嘴一句,国产现在真的不错,新能源车,手机,电器,运动品牌等全面崛起,国人是时候自信点了,00后在这方面就很好.


**模式一:自动托管之平滑模式(自动挡-雪地模式) 默认开启此模式 (v0.9.9+支持)**

---

在此模式下,索引的创建更新数据迁移等全生命周期用户均不需要任何操作即可完成,过程零停机,用户无感知,可实现在生产环境的平滑过渡,类似汽车的自动档-雪地模式,平稳舒适,彻底解放用户,尽情享受自动架势的乐趣!

其核心处理流程梳理如下图所示:
![平滑模式.png](https://cdn.nlark.com/yuque/0/2022/png/21559896/1650104902864-f73adff5-9fe9-431b-ab99-c818a59a20fa.png#clientId=ua5372466-c3ac-4&crop=0&crop=0&crop=1&crop=1&from=ui&id=ubeb9f25f&margin=%5Bobject%20Object%5D&name=%E5%B9%B3%E6%BB%91%E6%A8%A1%E5%BC%8F.png&originHeight=720&originWidth=1280&originalType=binary&ratio=1&rotation=0&showTitle=false&size=96182&status=done&style=none&taskId=u6f9e690d-11c6-4037-9016-ddcad015613&title=)

**模式二:自动托管之非平滑模式(自动挡-运动模式)  (v0.9.9+支持)**

---

在此模式下,索引额创建及更新由EE全自动异步完成,但不处理数据迁移工作,速度极快类似汽车的自动挡-运动模式,简单粗暴,弹射起步! 适合在开发及测试环境使用,当然如果您使用logstash等其它工具来同步数据,亦可在生产环境开启此模式.
![非平滑模式.png](https://cdn.nlark.com/yuque/0/2022/png/21559896/1650105018698-30c85c81-19ff-4506-956d-791c618b50af.png#clientId=ua5372466-c3ac-4&crop=0&crop=0&crop=1&crop=1&from=ui&id=ub25b79dd&margin=%5Bobject%20Object%5D&name=%E9%9D%9E%E5%B9%B3%E6%BB%91%E6%A8%A1%E5%BC%8F.png&originHeight=720&originWidth=1280&originalType=binary&ratio=1&rotation=0&showTitle=false&size=69680&status=done&style=none&taskId=u56eb2612-2ee0-4512-b4a2-146b637f8ea&title=)
> 以上两种自动模式中,索引信息主要依托于实体类,如果用户未对该实体类进行任何配置,EE依然能够根据字段类型智能推断出该字段在ES中的存储类型,此举可进一步减轻开发者负担,对刚接触ES的小白更是福音.


**模式三:手动模式(手动挡)**

---

在此模式下,索引的所有维护工作EE框架均不介入,由用户自行处理,EE提供了开箱即用的索引[CRUD](https://www.yuque.com/laohan-14b9d/foyrfa/myborf)相关API,您可以选择使用该API手动维护索引,亦可通过es-head等工具来维护索引,总之在此模式下,您拥有更高的自由度,比较适合那些质疑EE框架的保守用户或追求极致灵活度的用户使用,类似汽车的手动挡,新手不建议使用此模式,老司机请随便.
![手动模式.png](https://cdn.nlark.com/yuque/0/2022/png/21559896/1650105357060-879a2f4b-5145-483b-9b44-caf809b28330.png#clientId=ua5372466-c3ac-4&crop=0&crop=0&crop=1&crop=1&from=ui&id=u5f3ae9e8&margin=%5Bobject%20Object%5D&name=%E6%89%8B%E5%8A%A8%E6%A8%A1%E5%BC%8F.png&originHeight=720&originWidth=1280&originalType=binary&ratio=1&rotation=0&showTitle=false&size=47281&status=done&style=none&taskId=u497ff5f2-008e-48d8-b1cb-da977ab128e&title=)

**配置启用模式**

---

以上三种模式的配置,您只需要在您项目的配置文件application.properties或application.yml中加入一行配置即可:
```yaml
easy-es:
  global-config:
    process_index_mode: smoothly #smoothly:平滑模式, not_smoothly:非平滑模式, manual:手动模式
```
若缺省此行配置,则默认开启平滑模式.

> **TIPS:**
> - 以上三种模式,用户可根据实际需求灵活选择,自由体验,在使用过程中如有任何意见或建议可反馈给我们,我们将持续优化和改进,
> - EE在索引托管采用了策略+工厂设计模式,未来如果有更多更优模式,可以在不改动原代码的基础上轻松完成拓展,符合开闭原则,也欢迎各路开源爱好者贡献更多模式PR!
> - 我们将持续秉承把复杂留给框架,把易用留给用户这一理念,砥砺前行.

