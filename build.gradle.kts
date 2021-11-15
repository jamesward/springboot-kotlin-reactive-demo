import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	application
	kotlin("jvm")                              version "1.5.31"
	kotlin("plugin.spring")                    version "1.5.31"
	id("org.springframework.boot")             version "2.5.6"
	id("io.spring.dependency-management")      version "1.0.11.RELEASE"
	id("org.springframework.experimental.aot") version "0.10.5"
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
	mavenCentral()
	maven(uri("https://repo.spring.io/release"))
}

dependencies {
	implementation(kotlin("reflect"))
	implementation(kotlin("stdlib-jdk8"))
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = JavaVersion.VERSION_1_8.toString()
	}
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
	if (project.hasProperty("native")) {
		val args = setOf(
			"-Dspring.spel.ignore=true",
			"-Dspring.native.remove-yaml-support=true",
			"--enable-https",
			"--verbose",
			"-H:+ReportExceptionStackTraces"
		)
		builder = "paketobuildpacks/builder:tiny"
		environment = mapOf(
			"BP_BOOT_NATIVE_IMAGE" to "1",
			"BP_BOOT_NATIVE_IMAGE_BUILD_ARGUMENTS" to args.joinToString(" ")
		)
	}
}
