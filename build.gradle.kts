val lwjglVersion: String = "3.2.3"
val lwjglNatives: String = "natives-windows"

plugins {
    java
    application
}

group = "com.magicaleiling"
version = "1.0-SNAPSHOT"

application.mainClass.set("com.magicaleiling.labyrinth.App")

repositories {
    mavenCentral()
}

dependencies {
}
