import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val mysqlVersion:String by project
val koinKtor: String by project
val hikaricpVersion: String by project
plugins {
    kotlin("jvm") version "1.9.23"
    id("io.ktor.plugin") version "2.3.9"
    kotlin("plugin.serialization") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "7.1.0"

}

group = "com.example"
version = "0.0.1"
tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "com.MyFitFriend.ApplicationKt"
    }
}
application {
    mainClass.set("com.MyFitFriend.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}



tasks.test {
    useJUnitPlatform()
}
repositories {
    mavenCentral()
}

dependencies {
    implementation( "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation( "io.ktor:ktor-server-netty:$ktor_version")
    implementation( "io.ktor:ktor-server-core:$ktor_version")


    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")

    implementation( "io.ktor:ktor-server-auth:$ktor_version")
    implementation( "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
    implementation( "commons-codec:commons-codec:1.14")
    implementation( "ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-default-headers:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    //MySql
    implementation("mysql:mysql-connector-java:$mysqlVersion")
    //if using Postgres
    // Koin for Ktor
    implementation("io.insert-koin:koin-ktor:$koinKtor")
    //connection pooling
    implementation("com.zaxxer:HikariCP:$hikaricpVersion")

    implementation ("org.jetbrains.exposed:exposed-core:0.38.2")
    implementation( "org.jetbrains.exposed:exposed-java-time:0.38.2") // For Java Time support
    implementation ("org.jetbrains.exposed:exposed-dao:0.38.2" )// For DAO support
    implementation ("org.jetbrains.exposed:exposed-jdbc:0.38.2" )// For JDBC support
    implementation("com.h2database:h2:2.1.210")

    implementation( "org.ktorm:ktorm-core:3.2.0")
    implementation ("org.ktorm:ktorm-support-mysql:3.2.0")


    //dependencies for testing:
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")

    // Mockito for mocking dependencies
    testImplementation("org.mockito:mockito-core:4.0.0")

    // JUnit Jupiter API for writing tests
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")

    // Koin for dependency injection (including Koin test dependencies)
    testImplementation("io.insert-koin:koin-test:$koinKtor")
    testImplementation("io.insert-koin:koin-test-junit5:$koinKtor")
}
