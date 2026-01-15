plugins {
	java
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.musicapp"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2025.0.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
	implementation("io.minio:minio:8.5.17")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("io.jsonwebtoken:jjwt:0.12.6")
    implementation("com.googlecode.soundlibs:mp3spi:1.9.5-1")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val mockitoAgent = configurations.create("mockitoAgent")

dependencies {
    mockitoAgent("org.mockito:mockito-core") { isTransitive = false }
}

tasks {
    test {
        jvmArgs("-javaagent:${mockitoAgent.asPath}")
    }
}
