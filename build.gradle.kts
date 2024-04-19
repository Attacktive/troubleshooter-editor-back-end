import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinPluginVersion = "1.9.23"

	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version kotlinPluginVersion
	kotlin("plugin.spring") version kotlinPluginVersion
	kotlin("plugin.jpa") version kotlinPluginVersion
}

group = "com.github.attacktive"
version = "1.2.0"
java.sourceCompatibility = JavaVersion.VERSION_21

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot", "spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module", "jackson-module-kotlin")
	implementation("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
	implementation("org.xerial", "sqlite-jdbc")
	annotationProcessor("org.springframework.boot", "spring-boot-configuration-processor")
	testImplementation("org.springframework.boot", "spring-boot-starter-test") {
		exclude(module = "junit")
		exclude(module = "junit-vintage-engine")
		exclude(module = "mockito-core")
		exclude(module = "json-path")
	}
	testImplementation("com.jayway.jsonpath", "json-path", "2.9.0") {
		because("Dependency maven:com.jayway.jsonpath:json-path:2.8.0 is vulnerable CVE-2023-51074 5.3 Out-of-bounds Write vulnerability with Medium severity found")
	}
	testImplementation(kotlin("test"))
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
