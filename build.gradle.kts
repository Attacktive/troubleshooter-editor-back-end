import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.attacktive"
version = "1.2.3"
java.sourceCompatibility = JavaVersion.VERSION_21

plugins {
	val kotlinPluginVersion = "2.0.0"

	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
	kotlin("jvm") version kotlinPluginVersion
	kotlin("plugin.spring") version kotlinPluginVersion
	kotlin("plugin.jpa") version kotlinPluginVersion
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	val exposedVersion = "0.50.1"

	implementation("org.springframework.boot", "spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module", "jackson-module-kotlin")
	implementation("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
	implementation("org.xerial", "sqlite-jdbc")
	implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
	implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
	implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)

	annotationProcessor("org.springframework.boot", "spring-boot-configuration-processor")

	testImplementation(kotlin("test"))
	testImplementation("org.springframework.boot", "spring-boot-starter-test") {
		exclude(module = "junit")
		exclude(module = "junit-vintage-engine")
		exclude(module = "mockito-core")
		exclude(module = "json-path")
	}
	testImplementation("com.jayway.jsonpath", "json-path", "2.9.0") {
		because("Dependency maven:com.jayway.jsonpath:json-path:2.8.0 is vulnerable CVE-2023-51074 5.3 Out-of-bounds Write vulnerability with Medium severity found")
	}
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
