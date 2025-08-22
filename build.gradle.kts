import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.attacktive"
version = "1.2.14"
java.sourceCompatibility = JavaVersion.VERSION_21

plugins {
	val kotlinPluginVersion = "2.2.10"

	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("jvm") version kotlinPluginVersion
	kotlin("plugin.spring") version kotlinPluginVersion
	kotlin("plugin.jpa") version kotlinPluginVersion
	jacoco
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
	val exposedVersion = "0.61.0"

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
	}
}

tasks.withType<KotlinCompile> {
	compilerOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget.set(JvmTarget.JVM_21)
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}

tasks.jacocoTestCoverageVerification {
	violationRules {
		rule {
			limit {
				minimum = "0.5".toBigDecimal()
			}
		}
	}
}
