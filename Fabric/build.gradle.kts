plugins {
    id("fabric-loom") version "1.6.3"
    id("maven-publish")
    id("java-library")
}

version = libs.versions.project.get()
group = "dev.lightdream"

dependencies {
    minecraft(libs.minecraft.v20)
    mappings("net.fabricmc:yarn:${libs.versions.yarn.mappings.get()}:v2")

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)

    api(project(":command-manager-common"))

    api(libs.luckperms)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
    repositories {
        val githubURL = project.findProperty("github.url") ?: ""
        val githubUsername = project.findProperty("github.auth.username") ?: ""
        val githubPassword = project.findProperty("github.auth.password") ?: ""

        val selfURL = project.findProperty("self.url") ?: ""
        val selfUsername = project.findProperty("self.auth.username") ?: ""
        val selfPassword = project.findProperty("self.auth.password") ?: ""

        maven(url = githubURL as String) {
            name = "github"
            credentials(PasswordCredentials::class) {
                username = githubUsername as String
                password = githubPassword as String
            }
        }

        maven(url = selfURL as String) {
            name = "self"
            credentials(PasswordCredentials::class) {
                username = selfUsername as String
                password = selfPassword as String
            }
        }
    }
}

tasks.register("publishGitHub") {
    dependsOn("publishMavenPublicationToGithubRepository")
    description = "Publishes to GitHub"
}

tasks.register("publishSelf") {
    dependsOn("publishMavenPublicationToSelfRepository")
    description = "Publishes to Self hosted repository"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
