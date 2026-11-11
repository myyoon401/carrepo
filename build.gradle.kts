import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.2.20" apply false
    kotlin("plugin.spring") version "2.2.20" apply false
    kotlin("plugin.jpa") version "2.2.20" apply false
    id("org.springframework.boot") version "4.1.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    group = "com.carrepo"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencies {
        "implementation"("org.springframework.boot:spring-boot-starter-web")
        "implementation"("org.springframework.boot:spring-boot-starter-validation")
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")
        "implementation"("tools.jackson.module:jackson-module-kotlin")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            javaParameters = true
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }
}
