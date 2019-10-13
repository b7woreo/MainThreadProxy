# MainThreadProxy

## 用途

在 Android 开发中，经常需要传递 Callback 给第三方 SDK，但是并不确定第三方 SDK 回调函数时是否都在主线程中执行，于是需要通过其它方式在每次 Callback 调用时执行一次线程切换操作，非常的繁琐。本项目利用 Java 动态代理的特性封装了回调中的线程切换操作，避免一遍又一遍的编写模板代码。示例：

```kotlin
val callback = object: Callback{
  	override void onCall(){
      	// do something in main thread
    }
}
sdk.call(callback.mainThreadProxy())
```

## License

```
MIT License

Copyright (c) 2019 ChenRenJie

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```