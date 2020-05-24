# UETool-Xposed插件版
## UETool介绍-饿了么提供
UETool 是一个各方人员（设计师、程序员、测试）都可以使用的调试工具。它可以作用于任何显示在屏幕上的 view，比如 Activity/Fragment/Dialog/PopupWindow 等等。
详细资料请看：[饿了么UETool](https://github.com/eleme/UETool)
对于平常的开发者来说项目中集成一个框架太过于复杂，特别是这个的主要适用的人群是设计。所以针对这个问题，将其封装成一个插件，对开发和设计都比较友好。

## 进阶版-VirtualUETool
项目地址：https://github.com/zhangke3016/VirtualUETool
项目介绍：https://www.jianshu.com/p/20bd558fdaf9
感谢zhangke3016大佬，我实在他项目的基础上实现的，大佬踩了不少坑。
他的原理是底层使用VirtualAPP实现的，但由于VirtualApp商业化已经好多年了，普通版对于高版本的支持性并不好，因为这个原因，决定开发以Xposed为底层的UETool,可以在免Root的太极阴中使用，对于普通用户来说，使用成本降低了很多。

## 超级进阶版-XposedUETool
项目底层是使用Xposed框架，适用范围：Xposed,EDXposed，太极等依赖Xposed框架的软件。

太极：模块管理->勾选相应的软件->打开APP，点击打开UETool,(PS:第一次会请求悬浮权限)，出现UE图标就是正常状态了。

EDXposed/Xposed:安装软件，打开模块->勾选相应的软件->重启手机->打开软件,点击打开UETool,(PS:第一次会请求悬浮权限)，出现UE图标就是正常状态了。

![](https://github.com/longshihan1/XposedUETool/blob/master/image/image-20200524214435404.png)

捕捉功能：

![](https://github.com/longshihan1/XposedUETool/blob/master/image/image-20200524214726294.png)

相对位置：

![](https://github.com/longshihan1/XposedUETool/blob/master/image/image-20200524214837975.png)

手术刀(页面层级)：

![](https://github.com/longshihan1/XposedUETool/blob/master/image/image-20200524214959821.png)

我们使用酷安网演示了一下具体的功能的使用。基本满足于开发和设计的需求。