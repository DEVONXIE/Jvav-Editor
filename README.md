# jvav Editor 使用说明

## 介绍
**jvav Editor** 是一个基于 Java Swing 的简单文本编辑器，具有、文件树浏览、编译运行等功能。

## 安装与运行
1. 确保你的系统中已经安装了 Java 运行环境（JRE）。
2. 下载并解压 jvav Editor 源代码。
3. 打开命令行终端，进入解压后的 jvav Editor 目录。
4. 运行以下命令启动编辑器：
    ```bash
    java Editor
    ```

## 主要功能

### 1. 文本编辑
- 打开文件：通过菜单栏的 "File" -> "Open File"，选择要编辑的文件。
- 保存文件：通过菜单栏的 "File" -> "Save" 或 "File" -> "Save As"，保存当前编辑的文件。


### 2. 文件树浏览
- 使用 "File" -> "Open Folder" 可以选择一个文件夹，并在侧边栏显示该文件夹的文件树。

### 3. 编译与运行
- 编译：通过菜单栏的 "Build" -> "Compile" 进行代码编译，编译结果会显示在底边栏。
- 编译并运行：通过菜单栏的 "Build" -> "Compile and Run" 进行编译并运行，会弹出一个cmd窗口来运行程序，程序跑完会在底边栏打印相应消息。
```java
ProcessBuilder processBuilder = new ProcessBuilder("CMD.exe", "/C", "start", "java", className);
// ProcessBuilder processBuilder = new ProcessBuilder("java", className);
```
#### 小说明
以上代码就是我编译并运行函数里的核心了，如果用下面注释掉的代码的话是可以将运行结果打印在底边栏的，比如程序中有`print`之类的打印函数的话他就会将结果打印在底边栏。但是这么做的话当你运行带有`Scanner`这种输入函数的代码的话就会因为无法输入而卡住，因为编辑器的底边栏仅仅是用来打印信息的，并不能对跑的程序进行输入（vscode那种终端实在做不出来T_T）。所以只能用没注释的那行代码，也就是曲线救国，先运行CMD，再传参给它，让它运行代码。这样做虽然可以输入了，但是这程序一跑完就会自动关闭，展现出来的结果就是一个黑框一闪而过。所以为了能看到输出结果，请一定要在写好的代码后再加一个`scanner.nextInt()`之类的输入来让这个程序暂停一下。

### 4. 错误提示
- 如果编译或运行过程中存在错误，错误信息会显示在底边栏，帮助你快速定位问题。

## 注意事项
- 请确保代码文件以 ".java" 结尾，以确保正确的编译和运行。
- 当前版本可能对部分高级功能支持有限，建议在简单的 Java 代码编辑中使用。

## 问题反馈
如果在使用过程中遇到问题或有改进意见，请在 [GitHub 仓库](https://github.com/DEVONXIE/Jvav-Editor) 提交 issue。

感谢使用 jvav Editor，愿你编码愉快！
