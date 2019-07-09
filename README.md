# Clickable log output for Eclipse / Android Studio

## Download
project/build.gradle

    repositories {
        google()
        jcenter()
        maven { url "https://github.com/im97mori-github/maven/raw/master" }
    }

project/module/build.gradle

    dependencies {
        implementation 'org.im97mori:stacklog:0.0.2'
    }

## How to use

### output sample on Eclipse
    package org.im97mori.stacklog;
    
    import org.junit.Test;
    
    public class LogUtilsTest {
    
        @Test
        public void test_eclipse1() {
            LogUtils.stackLog("abc"); // line number 9
        }
    }

![Image of Eclipse console](https://github.com/im97mori-github/stackLog/blob/master/image/eclipse.png)

### output sample on Android Studio
![Image of Eclipse console](https://github.com/im97mori-github/stackLog/blob/master/image/androidstudio.png)