import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.jetbrains.kotlin.plugin.jpa")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // Boot 4 는 자동설정이 모듈별로 쪼개져 있다. flyway-core 만으로는 기동 시 마이그레이션이 돌지 않는다.
    implementation("org.springframework.boot:spring-boot-flyway")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    // 공고 원문 추출: HTML 파싱(jsoup) → 본문 추려내기(readability) → 마크다운 변환(flexmark)
    implementation("org.jsoup:jsoup:1.18.3")
    implementation("net.dankito.readability4j:readability4j:1.0.8")
    implementation("com.vladsch.flexmark:flexmark-html2md-converter:0.64.8")

    testImplementation("org.springframework.boot:spring-boot-webmvc-test")
}

// ---------------------------------------------------------------------------
// 프런트(web) 빌드 연결
//
// dev 는 `./gradlew :backend:bootRun` + `pnpm --dir web dev` 로 따로 띄운다. bootRun 은
// 아래 태스크에 걸려 있지 않으므로 프런트를 건드리지 않는다.
// 배포 산출물(bootJar)에는 항상 SPA 가 함께 들어간다 — UI 없는 jar 가 나가는 사고를 막는다.
// 프런트 없이 빨리 받아야 할 때만 `-PskipWeb`.
// ---------------------------------------------------------------------------
val webDir = rootProject.file("web")
val pnpm = if (System.getProperty("os.name").lowercase().contains("win")) "pnpm.cmd" else "pnpm"

val installWeb by tasks.registering(Exec::class) {
    group = "web"
    description = "web 의존성 설치 (pnpm-lock.yaml 기준)"
    workingDir = webDir
    commandLine(pnpm, "install", "--frozen-lockfile")
    inputs.file(webDir.resolve("package.json"))
    outputs.dir(webDir.resolve("node_modules"))
}

val buildWeb by tasks.registering(Exec::class) {
    group = "web"
    description = "web 번들 생성 → web/dist"
    dependsOn(installWeb)
    workingDir = webDir
    commandLine(pnpm, "run", "build")
    inputs.dir(webDir.resolve("src"))
    inputs.file(webDir.resolve("index.html"))
    inputs.file(webDir.resolve("vite.config.ts"))
    inputs.file(webDir.resolve("package.json"))
    outputs.dir(webDir.resolve("dist"))
}

tasks.named<BootJar>("bootJar") {
    archiveFileName = "carrepo.jar"
    if (!project.hasProperty("skipWeb")) {
        dependsOn(buildWeb)
        from(webDir.resolve("dist")) { into("BOOT-INF/classes/static") }
    }
}
