plugins {
    `kotlin-dsl`
    id("maven-publish")
}

group = "dev.msiejak"
version = "1.0.0"


repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.2.1")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.7.0"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

gradlePlugin {
    val vc by plugins.creating {
        id = "dev.msiejak.versionCodePlugin"
        implementationClass = "versionCodePlugin.VersionCodePlugin"
    }
}

