## ephemeral-ports

A PoC project to help identifying networks leaking ephemeral ports.

According to [this article](https://groups.google.com/forum/?fromgroups=#!topic/android-ndk/JYQEfOEg_3A), the following files
```
/proc/net/tcp
/proc/net/tcp6
/proc/net/udp
/proc/net/udp6
```

provide (aka `netstat`) details about TCP/UDP connections currently open under Android OS. Process or app uid is also revealed in those files.
Using simple code like [this](http://agolovatyuk.blogspot.com/2012/04/android-traffic-statistics-inside.html) it is possible to find the actual package name of the app that created the connection.

Some of the networks, notoriously WIFI ones, preserve ephemeral port number of the TCP/UDP packets leaving the system. Using this project, it is possible to detect if the network, your device is currently connected to, leaks this information.

This is a screenshot of the Android app in action. Building it requires [Android Studio](https://developer.android.com/studio/).
![alt text](https://raw.githubusercontent.com/rtybase/ephemeral-ports/master/app-screen.png "Andoird app screenshot")
