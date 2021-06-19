# Welcome to TUFIND Application

TU FIND Application was developed to solve the loss in Thammasat University Rangsit Campus which has many buildings and large area. the system to find building is Image processing. It uses ORB (Oriented FAST and Rotated BRIEF) to search for keypoints and descriptors from photos which received from user and images stored in database. 
## Description

Mobile Application for building recognition from a photo or Mobile Application “TU FIND” was developed to solve the loss in Thammasat University Rangsit Campus which has many buildings and large area. The system will find a building from a photo and show a direction including the near places of that building. When the processing was completed, it will save the result, date and type of user which is member or non-member in database. The system will show statistics of finding to the administrator for planning management to meet user needs.  This Mobile Application has the core ability to search for buildings from the photos.

## Installation
TUFIND is a Image processing-enabled, mobile-ready, online-storage compatible,  Java-powered Android Studio editor.

-   [Install - Android Studio](https://developer.android.com/studio)
-   [Install - Python 3](https://www.python.org/downloads/)


## Open Project on Android Studio
1. open **Android Studio**
2. File > New > Project from Version Control
3. After clicking on the **Project from Version Control** a pop-up screen will arise like below. In the **Version control** choose **Git** from the drop-down menu.
4.  Copy the  **hyperlink**  by click on the green button  **Code**.
`'https://github.com/pingpingNtp/TUFIND'` 
5. **paste the link in the URL** and choose your **Directory**
6. Click on the **Clone** button and you are done.

In some versions of Android Studio a certain error occurs:
> error: package android.support.v4.app does not exist.

To fix it go to Gradle Scripts -> build.gradle(Module:app) and the add the dependecies:
```
dependencies {      
    implementation fileTree(dir: "libs", include: ["*.jar"])  
	implementation 'androidx.appcompat:appcompat:1.2.0'  
}
```
### Problems I encountered at the beginning of the project :
> Android Support plugin for IntelliJ IDEA (or Android Studio) cannot open this project

how to solve it :

Change to
```
distributionUrl=https\://services.gradle.org/distributions/gradle-6.5-all.zip
```
in file `gradle-wrapper.properties`, and then change to
```
classpath 'com.android.tools.build:gradle:4.0.2'
```
in file build.gradle (Project).

## Directory Structure
```
D:.
├───.gradle
│   ├───6.5
│   │   ├───executionHistory
│   │   ├───fileChanges
│   │   ├───fileHashes
│   │   └───vcsMetadata-1
│   ├───buildOutputCleanup
│   ├───checksums
│   └───vcs-1
├───.idea
│   ├───caches
│   ├───codeStyles
│   ├───inspectionProfiles
│   └───modules
│       └───app
├───app
│   └───src
│       ├───androidTest
│       │   └───java
│       │       └───com
│       │           └───example
│       │               └───tufind
│       ├───main
│       │   ├───java
│       │   │   └───com
│       │   │       └───example
│       │   │           └───tufind
│       │   │               ├───Login
│       │   │               ├───Map
│       │   │               └───ViewPager
│       │   └───res
│       │       ├───anim
│       │       ├───drawable
│       │       ├───drawable-v24
│       │       ├───font
│       │       ├───layout
│       │       ├───menu
│       │       ├───mipmap-anydpi-v26
│       │       ├───mipmap-hdpi
│       │       ├───mipmap-mdpi
│       │       ├───mipmap-xhdpi
│       │       ├───mipmap-xxhdpi
│       │       ├───mipmap-xxxhdpi
│       │       ├───values
│       │       ├───values-th-rTH
│       │       └───xml
│       │           └───.idea
│       │               └───codeStyles
│       └───test
│           └───java
│               └───com
│                   └───example
│                       └───tufind
└───gradle
    └───wrapper
```
## Contact us

Please feel free to contact us if you have any questions.

-	damitonal@hotmail.com
-	topchk1@gmail.com
