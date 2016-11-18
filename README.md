# Hybrid JS Debug

> Hybrid JS Debug 是一个高效的Hybrid调试工具，可以直接通过PC端浏览器将H5页面展示在App中. 

> 不再依赖App去改动Url，支持App中所有的JSApi

### Using Hybrid JS Debug Library in your application
```groovy
debugCompile 'com.vivavideo.mobile:debugjs:0.1.0@aar'
```
Use `debugCompile` so that it will only compile in your debug build not in release apk.

That’s all, just start the application, you will be able to see logs in the logcat like below :

* D/DebugJS: Open http://XXX.XXX.X.XXX:8080 in your browser (adb shell netcfg)

Important: 手机必须和电脑在同一个局域网(公司内quvideo2和有线在一个局域网)

### Hybrid配置 参考 [Hybrid接入指南](https://quvideo.worktile.com/drive/57678b7512de9f970cab8334/575125fe2c29f5270554f1a3)

* 需要注意的是H5Activity需要注册到Manifest中。

### TODO
* 支持调试JSApi
* 展示目前App支持的Api

### License
```
   Copyright (C) 2016 xiyouMc
   Copyright (C) 2011 Android Open Source Project

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```

### 欢迎各位一同开发，增加更多的功能