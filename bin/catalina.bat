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
rem Start/Stop Script for the CATALINA Server
rem
rem Environment Variable Prerequisites
rem
rem   Do not set the variables in this script. Instead put them into a script
rem   setenv.bat in CATALINA_BASE/bin to keep your customizations separate.
rem
rem   WHEN RUNNING TOMCAT AS A WINDOWS SERVICE:
rem   Note that the environment variables that affect the behavior of this
rem   script will have no effect at all on Windows Services. As such, any
rem   local customizations made in a CATALINA_BASE/bin/setenv.bat script
rem   will also have no effect on Tomcat when launched as a Windows Service.
rem   The configuration that controls Windows Services is stored in the Windows
rem   Registry, and is most conveniently maintained using the "tomcatXw.exe"
rem   maintenance utility, where "X" is the major version of Tomcat you are
rem   running.
rem
rem   CATALINA_HOME   May point at your Catalina "build" directory.
rem
rem   CATALINA_BASE   (Optional) Base directory for resolving dynamic portions
rem                   of a Catalina installation.  If not present, resolves to
rem                   the same directory that CATALINA_HOME points to.
rem
rem   CATALINA_OPTS   (Optional) Java runtime options used when the "start",
rem                   "run" or "debug" command is executed.
rem                   Include here and not in JAVA_OPTS all options, that should
rem                   only be used by Tomcat itself, not by the stop process,
rem                   the version command etc.
rem                   Examples are heap size, GC logging, JMX ports etc.
rem
rem   CATALINA_TMPDIR (Optional) Directory path location of temporary directory
rem                   the JVM should use (java.io.tmpdir).  Defaults to
rem                   %CATALINA_BASE%\temp.
rem
rem   JAVA_HOME       Must point at your Java Development Kit installation.
rem                   Required to run the with the "debug" argument.
rem
rem   JRE_HOME        Must point at your Java Runtime installation.
rem                   Defaults to JAVA_HOME if empty. If JRE_HOME and JAVA_HOME
rem                   are both set, JRE_HOME is used.
rem
rem   JAVA_OPTS       (Optional) Java runtime options used when any command
rem                   is executed.
rem                   Include here and not in CATALINA_OPTS all options, that
rem                   should be used by Tomcat and also by the stop process,
rem                   the version command etc.
rem                   Most options should go into CATALINA_OPTS.
rem
rem   JAVA_ENDORSED_DIRS (Optional) Lists of of semi-colon separated directories
rem                   containing some jars in order to allow replacement of APIs
rem                   created outside of the JCP (i.e. DOM and SAX from W3C).
rem                   It can also be used to update the XML parser implementation.
rem                   This is only supported for Java <= 8.
rem                   Defaults to $CATALINA_HOME/endorsed.
rem
rem   JPDA_TRANSPORT  (Optional) JPDA transport used when the "jpda start"
rem                   command is executed. The default is "dt_socket".
rem
rem   JPDA_ADDRESS    (Optional) Java runtime options used when the "jpda start"
rem                   command is executed. The default is localhost:8000.
rem
rem   JPDA_SUSPEND    (Optional) Java runtime options used when the "jpda start"
rem                   command is executed. Specifies whether JVM should suspend
rem                   execution immediately after startup. Default is "n".
rem
rem   JPDA_OPTS       (Optional) Java runtime options used when the "jpda start"
rem                   command is executed. If used, JPDA_TRANSPORT, JPDA_ADDRESS,
rem                   and JPDA_SUSPEND are ignored. Thus, all required jpda
rem                   options MUST be specified. The default is:
rem
rem                   -agentlib:jdwp=transport=%JPDA_TRANSPORT%,
rem                       address=%JPDA_ADDRESS%,server=y,suspend=%JPDA_SUSPEND%
rem
rem   JSSE_OPTS       (Optional) Java runtime options used to control the TLS
rem                   implementation when JSSE is used. Default is:
rem                   "-Djdk.tls.ephemeralDHKeySize=2048"
rem
rem   LOGGING_CONFIG  (Optional) Override Tomcat's logging config file
rem                   Example (all one line)
rem                   set LOGGING_CONFIG="-Djava.util.logging.config.file=%CATALINA_BASE%\conf\logging.properties"
rem
rem   LOGGING_MANAGER (Optional) Override Tomcat's logging manager
rem                   Example (all one line)
rem                   set LOGGING_MANAGER="-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager"
rem
rem   TITLE           (Optional) Specify the title of Tomcat window. The default
rem                   TITLE is Tomcat if it's not specified.
rem                   Example (all one line)
rem                   set TITLE=Tomcat.Cluster#1.Server#1 [%DATE% %TIME%]
rem ---------------------------------------------------------------------------

setlocal

rem Suppress Terminate batch job on CTRL+C
rem 第一个参数不是run,就转向 mainEntry 标签,我们传递的第一个参数从上面来看是 start ,所以直接看 mainEntry 标签。
if not ""%1"" == ""run"" goto mainEntry
if "%TEMP%" == "" goto mainEntry
if exist "%TEMP%\%~nx0.run" goto mainEntry
echo Y>"%TEMP%\%~nx0.run"
if not exist "%TEMP%\%~nx0.run" goto mainEntry
echo Y>"%TEMP%\%~nx0.Y"
call "%~f0" %* <"%TEMP%\%~nx0.Y"
rem Use provided errorlevel
set RETVAL=%ERRORLEVEL%
del /Q "%TEMP%\%~nx0.Y" >NUL 2>&1
exit /B %RETVAL%
:mainEntry
rem 删除临时目录下的 catalina.bat.run 文件,如果出错也不提示
del /Q "%TEMP%\%~nx0.run" >NUL 2>&1

rem Guess CATALINA_HOME if not defined
rem 设置 CURRENT_DIR 为当前目录bin(假如你是在bin目录中启动)
set "CURRENT_DIR=%cd%"
rem CATALINA_HOME 不是空就转向 gotHome
if not "%CATALINA_HOME%" == "" goto gotHome
rem 设置 CATALINA_HOME 指向 CURRENT_DIR 的目录,也就是 bin 目录
set "CATALINA_HOME=%CURRENT_DIR%"
rem 如果存在 %CATALINA_HOME%\bin\catalina.bat,那么就转向 okHome,否则退出到父目录(显然不存在bin\bin\catalina.bat)
if exist "%CATALINA_HOME%\bin\catalina.bat" goto okHome
cd ..
rem 设置 CATALINA_HOME 为当前目录(也就是Tomcat的根目录）
set "CATALINA_HOME=%cd%"
rem 进入 CURRENT_HOME 指向的目录,也就是 bin 目录,执行 gotHome 标签
cd "%CURRENT_DIR%"
:gotHome
rem 如果存在 %CATALINA_HOME%\bin\catalina.bat,就转向 okHome,现在 CATALINA_HOME 指向的目录是Tomcat的安装目录,所以 \bin\catalina.bat 文件是存在的,所以转向okHome标签
if exist "%CATALINA_HOME%\bin\catalina.bat" goto okHome
echo The CATALINA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okHome

rem Copy CATALINA_BASE from CATALINA_HOME if not defined
rem 如果 CATALINA_BASE 变量不是空,就转向 gotBase 标签(显然为空)
if not "%CATALINA_BASE%" == "" goto gotBase
rem 设置 CATALINA_BASE 的值等同与 CATALINA_HOME 的值(现在CATALINA_BASE 也指向的Tomcat的根目录了)
set "CATALINA_BASE=%CATALINA_HOME%"
:gotBase

rem Ensure that neither CATALINA_HOME nor CATALINA_BASE contains a semi-colon
rem as this is used as the separator in the classpath and Java provides no
rem mechanism for escaping if the same character appears in the path. Check this
rem by replacing all occurrences of ';' with '' and checking that neither
rem CATALINA_HOME nor CATALINA_BASE have changed
if "%CATALINA_HOME%" == "%CATALINA_HOME:;=%" goto homeNoSemicolon
echo Using CATALINA_HOME:   "%CATALINA_HOME%"
echo Unable to start as CATALINA_HOME contains a semicolon (;) character
goto end
:homeNoSemicolon

if "%CATALINA_BASE%" == "%CATALINA_BASE:;=%" goto baseNoSemicolon
echo Using CATALINA_BASE:   "%CATALINA_BASE%"
echo Unable to start as CATALINA_BASE contains a semicolon (;) character
goto end
:baseNoSemicolon

rem Ensure that any user defined CLASSPATH variables are not used on startup,
rem but allow them to be specified in setenv.bat, in rare case when it is needed.
rem 清空CLASSPATH
set CLASSPATH=

rem Get standard environment variables
rem 如果不存在 %CATALINA_BASE%\bin\setenv.bat 文件,那么就转向 checkSetenvHome 标签,我们现在使用的是apache-tomcat-8.5.42,该文件是不存在的,所以转向 checkSetenvHome 标签
if not exist "%CATALINA_BASE%\bin\setenv.bat" goto checkSetenvHome
call "%CATALINA_BASE%\bin\setenv.bat"
goto setenvDone
:checkSetenvHome
if exist "%CATALINA_HOME%\bin\setenv.bat" call "%CATALINA_HOME%\bin\setenv.bat"
:setenvDone

rem Get standard Java environment variables
rem 如果存在 %CATALINA_HOME%\bin\setclasspath.bat,那么就转向 okSetclasspath 标签(显然存在)
if exist "%CATALINA_HOME%\bin\setclasspath.bat" goto okSetclasspath
echo Cannot find "%CATALINA_HOME%\bin\setclasspath.bat"
echo This file is needed to run this program
goto end
:okSetclasspath
rem 调用 setclasspath.bat 并且传递第一个参数(start),有兴趣的同学可以自行查看setclasspath.bat文件做了什么,其实就是设置了JAVA_HOME,JRE_HOME等变量的值
call "%CATALINA_HOME%\bin\setclasspath.bat" %1
if errorlevel 1 goto end

rem Add on extra jar file to CLASSPATH
rem Note that there are no quotes as we do not want to introduce random
rem quotes into the CLASSPATH
rem 如果 CLASSPATH变量为空,就跳转到 emptyClasspath 标签,由上面能看出来 CLASSPATH 变量的确是空的,所以跳转到 emptyClasspath 标签
if "%CLASSPATH%" == "" goto emptyClasspath
set "CLASSPATH=%CLASSPATH%;"
:emptyClasspath
rem 设置 CLASSPATH 指向 %CLASSPATH%%CATALINA_HOME%\BIN\BOOTSTRAP.JAR(也就是TOMCAT安装目录下\BIN\BOOTSTRAP.JAR)
set "CLASSPATH=%CLASSPATH%%CATALINA_HOME%\bin\bootstrap.jar"
rem 如果 CATALINA_BASE 不为空(这里是为空)那么转向 gotBase 标签, gotBase 为空标签,继续往下执行,设置 CATALINA_BASE 等于 CATALINA_HOME
if not "%CATALINA_TMPDIR%" == "" goto gotTmpdir
set "CATALINA_TMPDIR=%CATALINA_BASE%\temp"
:gotTmpdir

rem Add tomcat-juli.jar to classpath
rem tomcat-juli.jar can be over-ridden per instance
rem 如果不存在 %CATALINA_BASE%\bin\tomcat-juli.jar 那么就跳转到 juliClasspathHome 标签,我们使用的tomcat版本是存在的，所以继续执行
if not exist "%CATALINA_BASE%\bin\tomcat-juli.jar" goto juliClasspathHome
rem 该命令set "CLASSPATH=%CLASSPATH%;%CATALINA_BASE%\bin\tomcat-juli.jar" 表示将tomcat-juli.jar的路径添加到 CLASSPATH 中
set "CLASSPATH=%CLASSPATH%;%CATALINA_BASE%\bin\tomcat-juli.jar"
rem 跳转 juliClasspathDone 标签,juliClasspathDone是空标签
goto juliClasspathDone
:juliClasspathHome
set "CLASSPATH=%CLASSPATH%;%CATALINA_HOME%\bin\tomcat-juli.jar"
:juliClasspathDone

if not "%JSSE_OPTS%" == "" goto gotJsseOpts
set JSSE_OPTS="-Djdk.tls.ephemeralDHKeySize=2048"
:gotJsseOpts
set "JAVA_OPTS=%JAVA_OPTS% %JSSE_OPTS%"

rem Register custom URL handlers
rem Do this here so custom URL handles (specifically 'war:...') can be used in the security policy
set "JAVA_OPTS=%JAVA_OPTS% -Djava.protocol.handler.pkgs=org.apache.catalina.webresources"
rem 继续执行,如果 LOGGING_CONFIG 变量不为空就跳转到 noJuliConfig 标签,否则设置 LOGGING_CONFIG=-Dnop
if not "%LOGGING_CONFIG%" == "" goto noJuliConfig
set LOGGING_CONFIG=-Dnop
rem 如果不存在 %CATALINA_BASE%\conf\logging.properties,那么就跳转 noJuliConfig 标签,否则设置 LOGGING_CONFIG等于-Djava.util.logging.config.file="%CATALINA_BASE%\conf\logging.properties"
rem noJuliConfig 标签的作用就是设置 JAVA_OPTS 变量为 JAVA_OPTS+LOGGING_CONFIG
rem 这里多说一句,我们可以在 catalina.bat 文件的开始位置添加 JAVA_OPTS 变量来指定jvm的配置,具体的参数可以自行搜索
if not exist "%CATALINA_BASE%\conf\logging.properties" goto noJuliConfig
set LOGGING_CONFIG=-Djava.util.logging.config.file="%CATALINA_BASE%\conf\logging.properties"
:noJuliConfig
rem 如果 LOGGING_MANAGER 为空就转向 noJuliManager 标签，否则设置 LOGGING_MANAGER 值为-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager
rem noJuliManager 标签,将 LOGGING_MANAGER 添加到 JAVA_OPTS 变量
if not "%LOGGING_MANAGER%" == "" goto noJuliManager
set LOGGING_MANAGER=-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager
:noJuliManager

rem Configure JAVA 9 specific start-up parameters
set "JDK_JAVA_OPTIONS=%JDK_JAVA_OPTIONS% --add-opens=java.base/java.lang=ALL-UNNAMED"
set "JDK_JAVA_OPTIONS=%JDK_JAVA_OPTIONS% --add-opens=java.base/java.io=ALL-UNNAMED"
set "JDK_JAVA_OPTIONS=%JDK_JAVA_OPTIONS% --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED"

rem Java 9 no longer supports the java.endorsed.dirs
rem system property. Only try to use it if
rem JAVA_ENDORSED_DIRS was explicitly set
rem or CATALINA_HOME/endorsed exists.
set ENDORSED_PROP=ignore.endorsed.dirs
if "%JAVA_ENDORSED_DIRS%" == "" goto noEndorsedVar
set ENDORSED_PROP=java.endorsed.dirs
goto doneEndorsed
:noEndorsedVar
if not exist "%CATALINA_HOME%\endorsed" goto doneEndorsed
set ENDORSED_PROP=java.endorsed.dirs
:doneEndorsed

rem ----- Execute The Requested Command ---------------------------------------

echo Using CATALINA_BASE:   "%CATALINA_BASE%"
echo Using CATALINA_HOME:   "%CATALINA_HOME%"
echo Using CATALINA_TMPDIR: "%CATALINA_TMPDIR%"
rem 打印相关信息,如果第一个参数等于 debug,转到 use_jdk 标签,否则打印相关信息,转向 java_dir_displayed 标签,java_dir_displayed 还是打印相关信息
if ""%1"" == ""debug"" goto use_jdk
echo Using JRE_HOME:        "%JRE_HOME%"
goto java_dir_displayed
:use_jdk
echo Using JAVA_HOME:       "%JAVA_HOME%"
:java_dir_displayed
echo Using CLASSPATH:       "%CLASSPATH%"
rem 设置 _EXECJAVA 等于变量 _RUNJAVA.(该变量在 setclasspath.bat 中设置,值为 %JRE_HOME%\bin\java.exe
set _EXECJAVA=%_RUNJAVA%
rem 设置下相关变量,MAINCLASS,ACTION,清空 SECURITY_POLICY_FILE,DEBUG_OPTS,JPDA
set MAINCLASS=org.apache.catalina.startup.Bootstrap
set ACTION=start
set SECURITY_POLICY_FILE=
set DEBUG_OPTS=
set JPDA=
rem 如果第一个参数不等于jpda，转向 noJpda 标签.noJpda 为空标签,因为第一个参数是 start 所以,继续往下看
if not ""%1"" == ""jpda"" goto noJpda
set JPDA=jpda
if not "%JPDA_TRANSPORT%" == "" goto gotJpdaTransport
set JPDA_TRANSPORT=dt_socket
:gotJpdaTransport
if not "%JPDA_ADDRESS%" == "" goto gotJpdaAddress
set JPDA_ADDRESS=localhost:8000
:gotJpdaAddress
if not "%JPDA_SUSPEND%" == "" goto gotJpdaSuspend
set JPDA_SUSPEND=n
:gotJpdaSuspend
if not "%JPDA_OPTS%" == "" goto gotJpdaOpts
set JPDA_OPTS=-agentlib:jdwp=transport=%JPDA_TRANSPORT%,address=%JPDA_ADDRESS%,server=y,suspend=%JPDA_SUSPEND%
:gotJpdaOpts
shift
:noJpda

if ""%1"" == ""debug"" goto doDebug
if ""%1"" == ""run"" goto doRun
rem 一开始就对第一个参数进行判断,进而转向不同的标签,因为我们第一个参数是start,所以我们直接就转向了doStart标签
if ""%1"" == ""start"" goto doStart
if ""%1"" == ""stop"" goto doStop
if ""%1"" == ""configtest"" goto doConfigTest
if ""%1"" == ""version"" goto doVersion

echo Usage:  catalina ( commands ... )
echo commands:
echo   debug             Start Catalina in a debugger
echo   debug -security   Debug Catalina with a security manager
echo   jpda start        Start Catalina under JPDA debugger
echo   run               Start Catalina in the current window
echo   run -security     Start in the current window with security manager
echo   start             Start Catalina in a separate window
echo   start -security   Start in a separate window with security manager
echo   stop              Stop Catalina
echo   configtest        Run a basic syntax check on server.xml
echo   version           What version of tomcat are you running?
goto end

:doDebug
shift
set _EXECJAVA=%_RUNJDB%
set DEBUG_OPTS=-sourcepath "%CATALINA_HOME%\..\..\java"
if not ""%1"" == ""-security"" goto execCmd
shift
echo Using Security Manager
set "SECURITY_POLICY_FILE=%CATALINA_BASE%\conf\catalina.policy"
goto execCmd

:doRun
shift
if not ""%1"" == ""-security"" goto execCmd
shift
echo Using Security Manager
set "SECURITY_POLICY_FILE=%CATALINA_BASE%\conf\catalina.policy"
goto execCmd
rem doStart标签,shift,参数左移;如果 Title 变量为空就将 Title 设置为Tomcat,设置 _EXECJAVA 变量为 start "%TITLE%" %_RUNJAVA%
:doStart
shift
if "%TITLE%" == "" set TITLE=Tomcat
set _EXECJAVA=start "%TITLE%" %_RUNJAVA%
rem 如果第一个参数不等于-security,转向 execCmd 标签
if not ""%1"" == ""-security"" goto execCmd
shift
echo Using Security Manager
set "SECURITY_POLICY_FILE=%CATALINA_BASE%\conf\catalina.policy"
goto execCmd

:doStop
shift
set ACTION=stop
set CATALINA_OPTS=
goto execCmd

:doConfigTest
shift
set ACTION=configtest
set CATALINA_OPTS=
goto execCmd

:doVersion
%_EXECJAVA% -classpath "%CATALINA_HOME%\lib\catalina.jar" org.apache.catalina.util.ServerInfo
goto end


:execCmd
rem Get remaining unshifted command line arguments and save them in the
rem 清空 CMD_LINE_ARGS 变量
set CMD_LINE_ARGS=
:setArgs
rem 如果第一个变量为空,转向 doneSetArgs,运行到这里,因为前面在执行 doStart 的时候已经执行了shift命令,所以第一个参数已经是空了所,以直接转向 doneSetArgs 标签
rem start "Tomcat" "D:\WorkSoftware\Java\jdk1.7.0_13\bin\java"
rem -Djava.util.logging.config.file="E:\xxx\tomcat7.0\conf\logging.properties"
rem -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager
rem -Djava.endorsed.dirs="E:\xxx\tomcat7.0\endorsed"
rem -classpath "E:\xxx\tomcat7.0\bin\bootstrap.jar;E:\xxx\tomcat7.0\bin\tomcat-juli.jar"
rem -Dcatalina.base="E:\xxx\tomcat7.0"
rem -Dcatalina.home="E:\xxx\tomcat7.0"
rem -Djava.io.tmpdir="E:\xxx\tomcat7.0\temp" org.apache.catalina.startup.Bootstrap  start
rem 新开一个窗口,窗口名字叫做Tomcat 使用java.exe,各种参数配置balabla,最后调用 org.apache.catalina.startup.Bootstrap类，(类入口当然是main方法)传递参数为start
rem 总结:
rem 1.tomcat启动入口是startup.bat文件,该文件会设置 CATALINA_HOME,CATALINA_BASE等常见变量,并且调用 catalina.bat文件,传递start参数;
rem 2.catalina.bat文件,所做的事情就是检查关键文件是否存在,设置关键变量:例如CATALINA_HOME,CATALINA_BASE,classpath等等;最后新开一个窗口调用Bootstrap类的main方法,传递参数start,并且将一大堆配置传递过去
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

rem Execute Java with the applicable properties
if not "%JPDA%" == "" goto doJpda
if not "%SECURITY_POLICY_FILE%" == "" goto doSecurity
%_EXECJAVA% %LOGGING_CONFIG% %LOGGING_MANAGER% %JAVA_OPTS% %CATALINA_OPTS% %DEBUG_OPTS% -D%ENDORSED_PROP%="%JAVA_ENDORSED_DIRS%" -classpath "%CLASSPATH%" -Dcatalina.base="%CATALINA_BASE%" -Dcatalina.home="%CATALINA_HOME%" -Djava.io.tmpdir="%CATALINA_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
goto end
:doSecurity
%_EXECJAVA% %LOGGING_CONFIG% %LOGGING_MANAGER% %JAVA_OPTS% %CATALINA_OPTS% %DEBUG_OPTS% -D%ENDORSED_PROP%="%JAVA_ENDORSED_DIRS%" -classpath "%CLASSPATH%" -Djava.security.manager -Djava.security.policy=="%SECURITY_POLICY_FILE%" -Dcatalina.base="%CATALINA_BASE%" -Dcatalina.home="%CATALINA_HOME%" -Djava.io.tmpdir="%CATALINA_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
goto end
:doJpda
if not "%SECURITY_POLICY_FILE%" == "" goto doSecurityJpda
%_EXECJAVA% %LOGGING_CONFIG% %LOGGING_MANAGER% %JAVA_OPTS% %JPDA_OPTS% %CATALINA_OPTS% %DEBUG_OPTS% -D%ENDORSED_PROP%="%JAVA_ENDORSED_DIRS%" -classpath "%CLASSPATH%" -Dcatalina.base="%CATALINA_BASE%" -Dcatalina.home="%CATALINA_HOME%" -Djava.io.tmpdir="%CATALINA_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
goto end
:doSecurityJpda
%_EXECJAVA% %LOGGING_CONFIG% %LOGGING_MANAGER% %JAVA_OPTS% %JPDA_OPTS% %CATALINA_OPTS% %DEBUG_OPTS% -D%ENDORSED_PROP%="%JAVA_ENDORSED_DIRS%" -classpath "%CLASSPATH%" -Djava.security.manager -Djava.security.policy=="%SECURITY_POLICY_FILE%" -Dcatalina.base="%CATALINA_BASE%" -Dcatalina.home="%CATALINA_HOME%" -Djava.io.tmpdir="%CATALINA_TMPDIR%" %MAINCLASS% %CMD_LINE_ARGS% %ACTION%
goto end

:end
