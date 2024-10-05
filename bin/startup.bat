@echo off
rem Licensed to the Apache Software Foundation (ASF) under one or more
rem contributor license agreements.  See the NOTICE file distributed with
rem this work for additional information regarding copyright ownership.
rem The ASF licenses this file to You under the Apache License, Version 2.0
rem (the "License"); you may not use this file except in compliance with
rem the License.  You may obtain a copy of the License at
rem
rem     http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

rem ---------------------------------------------------------------------------
rem Start script for the CATALINA Server
rem ---------------------------------------------------------------------------

rem      该命令用于注释,rem起始的行不会作为代码执行
rem pause    该命令用于暂停正在执行的批处理文件,并且提示用户按键，然后程序继续执行
rem echo:     该命令用于在dos控制台显示一段文本,相当于print,如果想要显示环境变量需要在环境变量前后加上%,例如显示操作系统 echo %OS%
rem echo off: 该命令可以防止将批处理文件中的具体命令打印出来,而只会输出执行结果。
rem @echo off:该命令与echo off相同,唯一的区别在于 @echo off不仅会隐藏具体命令还会连'echo off'这个自身命令也隐藏起来
rem set:      设置环境变量;例如 set A = 100 设置A变量为100
rem label:    使用 ':'(冒号)来设置一个标签,供给goto命令使用;例如":init"代表一个init标签
rem goto:     该命令使正在执行的命令强制跳转到他指定的标签;例如我需要跳转指定A标签下的命令:如下:goto A
rem not:       该命令用来取反,相当于逻辑非
rem if:       该命令表示判断
rem exist:     该命令通常用来测试文件是否存在,一般和if一起使用
rem shift:     该命令用来将参数后移一位即将%2%赋值给%1%,%3%赋值给%2%,也可以理解为参数列表左移即删除现有参数列表的第一位
rem call:     该命令用来调用另外一条命令
rem setLocal: 该命令表示该批处理文件中修改的环境变量只在本文件中起作用,或者直到endLocal命令出现;被修改的环境变量才恢复原状
rem start:    重新开启一个dos窗口

setlocal

rem Guess CATALINA_HOME if not defined
rem 设置变量 CURRENT_DIR 为当前目录;例如打开了tomcat的 bin 目录,那么 CURRENT_DIR 就指向了 bin 目录
set "CURRENT_DIR=%cd%"
rem 判断系统变量 CATALINA_HOME 如果不是空串,那么就跳到 gotHome 标签执行,实际很少设置 CATALINA_HOME 这个系统变量
if not "%CATALINA_HOME%" == "" goto gotHome
rem 设置 CATALINA_HOME 为 CURRENT_DIR 指向的目录,也就是 bin 目录
set "CATALINA_HOME=%CURRENT_DIR%"
rem 判断 CATALINA_HOME\bin\catalina.bat 文件是否存在,如果存在就转向 okHome 标签,明显 CATALINA_HOME 现在指向的是 bin 目录.那么 bin\bin\catalina.bat 文件是不存在的,所以继续往下看,不会跳转到 okHome
if exist "%CATALINA_HOME%\bin\catalina.bat" goto okHome
rem 退出到上一级目录
cd ..
rem 设置 CATALINA_HOME 指向当前目录,也就是Tomcat的根目录
set "CATALINA_HOME=%cd%"
rem 进入 CURRENT_DIR 指向的目录,也就是 bin 目录
cd "%CURRENT_DIR%"
rem 执行 gotHome 标签
:gotHome
rem 如果存在 CATALINA_HOME%\bin\catalina.bat 这个文件就跳转执行okHome标签
if exist "%CATALINA_HOME%\bin\catalina.bat" goto okHome
echo The CATALINA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okHome
rem 设置变量 EXECUTABLE 指向 catalina.bat 文件
set "EXECUTABLE=%CATALINA_HOME%\bin\catalina.bat"

rem Check that target executable exists
rem 双重保险继续判断下 EXECUTABLE 指向的 catalina.bat 文件是否存在.存在就跳转执行 okExec 标签
if exist "%EXECUTABLE%" goto okExec
echo Cannot find "%EXECUTABLE%"
echo This file is needed to run this program
goto end
:okExec

rem Get remaining unshifted command line arguments and save them in the
rem 表示清空变量CMD_LINE_ARGS
set CMD_LINE_ARGS=
rem 执行setArgs标签
:setArgs
rem 第一个变量(%1%)为空字符串,那么就跳转到 doneSetArgs 标签,因为我们是直接运行startup.bat,没有传递任何参数,所以我们应该是跳转到 doneSetArgs 标签,由此也可以猜想出,如果不是使用双击执行的话,使用命令行启动 startup.bat 那么是可以传递参数的
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs
rem 跳转到 doneSetArgs,调用 EXECUTABLE 指向的文件,也就是 catalina.bat 文件,同时传递 start参数,因为 CMD_LINE_ARGS 为空,所以只传递了一个start参数
call "%EXECUTABLE%" start %CMD_LINE_ARGS%
rem rem 可以看出 end 标签是很多,判断失败跳转的标签,是参数不正确的时候的结束标志
:end
