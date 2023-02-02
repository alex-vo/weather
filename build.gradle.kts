import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    jacoco
}

group = "com.mintos"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.r2dbc:r2dbc-h2")
    implementation("org.liquibase:liquibase-core")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:mockserver:1.17.6")
    testImplementation("org.mock-server:mockserver-netty:5.15.0")
}

sourceSets {
    create("unitTest") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    }
    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output + sourceSets.getByName("unitTest").output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output + sourceSets.getByName("unitTest").output
    }
    create("componentTest") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    }
}

configurations["unitTestImplementation"].extendsFrom(configurations.testImplementation.get())
configurations["integrationTestImplementation"].extendsFrom(configurations.testImplementation.get())
configurations["componentTestImplementation"].extendsFrom(configurations.testImplementation.get())

java.sourceSets["unitTest"].apply {
    java.srcDir("src/test/unitTest/java")
    resources.srcDir("src/test/unitTest/resources")
}
java.sourceSets["integrationTest"].apply {
    java.srcDir("src/test/integrationTest/java")
    resources.srcDir("src/test/integrationTest/resources")
}
java.sourceSets["componentTest"].apply {
    java.srcDir("src/test/componentTest/java")
    resources.srcDir("src/test/componentTest/resources")
}

val unitTest = task<Test>("unitTest") {
    group = "verification"
    testClassesDirs = sourceSets["unitTest"].output.classesDirs
    classpath = sourceSets["unitTest"].runtimeClasspath
}

val integrationTest = task<Test>("integrationTest") {
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    mustRunAfter(unitTest)
}

val componentTest = task<Test>("componentTest") {
    group = "verification"
    testClassesDirs = sourceSets["componentTest"].output.classesDirs
    classpath = sourceSets["componentTest"].runtimeClasspath
    mustRunAfter(integrationTest)
}

tasks.test {
    dependsOn(unitTest, integrationTest, componentTest)
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)
}

tasks.jacocoTestReport {
    dependsOn(unitTest)
    additionalSourceDirs.setFrom(files(sourceSets.main.get().allSource.srcDirs))
    sourceDirectories.setFrom(files(sourceSets.main.get().allSource.srcDirs))
    classDirectories.setFrom(files(sourceSets.main.get().output))
    executionData.setFrom(fileTree(project.projectDir) {
        setIncludes(setOf("build/jacoco/*.exec"))
    })
    reports {
        csv.required.set(true)
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.jacocoTestCoverageVerification {
    executionData.setFrom(fileTree(project.projectDir) {
        setIncludes(setOf("build/jacoco/*.exec"))
    })
    violationRules {
        rule {
            limit {
                counter = "LINE"
                minimum = "0.9".toBigDecimal()
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
