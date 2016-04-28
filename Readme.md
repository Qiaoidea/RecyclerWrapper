## RecyclerView 下拉刷新（pullToRefresh）和加载更多(LoadMore)

　　方便快速的实现为RecyclerView添加下拉刷新头部和加载更多尾部，一行代码的事儿。

![演示动画](raw/RecyclerDemo.gif)

``to be continue..``
　　还在优化设计阶段，当前版本只针对 **Orientation** 为 **VERTICAL** 情况

### V 1.0
- 为普通的RecyclerView 增加下拉刷新头
- 增加加载更多的footer
- 简单接口能快速实现更丰富的动画效果
- 更友好的配置选项和实现方式，更优雅的完成加载

### 如何使用
1. ``需要下拉刷新头和加载更多时``，使用
```
        RecyclerWrapper.bindOn(mRecyclerView)
                .layoutBy(mLayoutManager)
                .withRefreshHeader(mView, mOnRefreshListener)
                .withLoadMoreFooter(loadingView, mOnLoadMoreListener)
                .setAdapter(mAdapter)
                .build();
```

OK,Done.App可以快速应用起来头尾了。


　　当然，如果只是简单添加 ``Header``
```
            RecyclerWrapper.bindOn(mRecyclerView)
                   .withRefreshHeader(mView, mOnRefreshListener)
                   .build();
```
或者 ``Footer``
```
            RecyclerWrapper.bindOn(mRecyclerView)
                .withLoadMoreFooter(loadingView, mOnLoadMoreListener)
                .build();
```
一样可以快速高效。

> 注：
LayoutManager 和 Adapter 可以使用recyclerView提供的set来设置，RecyclerWrapper只是简单包装了链式调用。


2. 如果想***根据滑动距离来增加复杂的刷新动画效果***，可以继承 implements ``IRefreshHeader``
　　Make a flexiable Animate Header

```
public interface IRefreshHeader {
    void onVisibleHeightChanged(int visibleHeight);

    void onRefresh();

    void onComplete();
}

//eg:自定义头部示例
public class MyRefreshHeader extends View implements IRefreshHeader{
    //...
}

```
使用同上。***RecyclerWrapper.bindOn().withRefreshHeader(mView, mOnRefreshListener)***

效果：
![演示动画](raw/HeaderDemo.gif)


### 补充说明
1. 列表Header和Footer只是对Adapter额外做了一层包装，增加了滑动响应。

2. IRefreshHeader接口可选择实现，默认视为简单View，实现该接口则可以在响应回调时做动画交互。其实实现机制很简单：

```
  withRefreshHeader(View header,onRefreshListener onRefreshListener){
        //...
        if(header instanceof IRefreshHeader){
            iRefreshHeader = (IRefreshHeader)header;
        }
  }
  
  //...
  if(null!+iRefreshHeader) iRefreshHeader.onVisibleHeightChanged
```