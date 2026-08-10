pluginManagement {
    repositories {
        // Official upstream profile (manually switch by commenting out the
        // mirror entries below and uncommenting these entries):
        // google()
        // mavenCentral()
        // gradlePluginPortal()
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // Official upstream profile (manually switch by commenting out the
        // mirror entries below and uncommenting these entries):
        // google()
        // mavenCentral()
        maven("https://maven.aliyun.com/repository/google")
        maven("https://maven.aliyun.com/repository/public")
    }
}

rootProject.name = "AvenorLauncher"
include(":app")
