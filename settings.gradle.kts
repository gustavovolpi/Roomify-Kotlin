pluginManagement {
    repositories {
        gradlePluginPortal()  // Deve vir primeiro
        google()  // Google Maven
        mavenCentral()  // Maven Central

    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Adicione outros repositórios se necessário
    }
}

rootProject.name = "agrvai"
include(":app")