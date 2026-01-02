pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // 👇 ESTA LÍNEA ES LA CLAVE
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "StreamCuscoApp"
include(":app")