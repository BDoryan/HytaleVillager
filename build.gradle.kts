import java.util.Properties

plugins {
    `maven-publish`
    id("hytale-mod") version "0.+"
}

group = "com.example"
version = "0.1.0"
val javaVersion = 25

val appData = System.getenv("APPDATA") ?: (System.getenv("HOME") + "/.var/app/com.hypixel.HytaleLauncher/data")
val defaultAssetsPath = "$appData/Hytale/install/release/package/game/latest/Assets.zip"
val hytaleAssetsPath = localOrGradleProp("hytaleAssetsPath") ?: defaultAssetsPath
val hytaleAssets = file(hytaleAssetsPath)

val localProps = Properties().also { props ->
    val localFile = rootProject.file("gradle-local.properties")
    if (localFile.exists()) {
        localFile.inputStream().use { props.load(it) }
    }
}

fun localOrGradleProp(name: String): String? =
    localProps.getProperty(name) ?: providers.gradleProperty(name).orNull

repositories {
    mavenCentral()
    maven("https://maven.hytale-modding.info/releases") {
        name = "HytaleModdingReleases"
    }
}

dependencies {
    compileOnly(libs.jetbrains.annotations)
    compileOnly(libs.jspecify)

    if (hytaleAssets.exists()) {
        compileOnly(files(hytaleAssets))
    } else {
        // Optional: Print a warning so you know why it's missing
        logger.warn("Hytale Assets.zip not found at: ${hytaleAssets.absolutePath}")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(javaVersion)
    }

    withSourcesJar()
}

tasks.named<ProcessResources>("processResources") {
    var replaceProperties = mapOf(
        "plugin_group" to findProperty("plugin_group"),
        "plugin_maven_group" to project.group,
        "plugin_name" to project.name,
        "plugin_version" to project.version,
        "server_version" to findProperty("server_version"),

        "plugin_description" to findProperty("plugin_description"),
        "plugin_website" to findProperty("plugin_website"),

        "plugin_main_entrypoint" to findProperty("plugin_main_entrypoint"),
        "plugin_author" to findProperty("plugin_author")
    )

    filesMatching("manifest.json") {
        expand(replaceProperties)
    }

    inputs.properties(replaceProperties)
}

hytale {

}

val hytaleServerPath = localOrGradleProp("hytaleServerPath")
if (!hytaleServerPath.isNullOrBlank()) {
    val hytaleExtension = extensions.findByName("hytale")
    if (hytaleExtension != null) {
        val resolvedPath = file(hytaleServerPath)
        val setterCandidates = listOf(
            "setServerPath",
            "setServerDirectory",
            "setServerDir",
            "setServerInstallPath"
        )
        val setter = hytaleExtension.javaClass.methods.firstOrNull { method ->
            method.name in setterCandidates && method.parameterCount == 1
        }
        if (setter != null) {
            setter.invoke(hytaleExtension, resolvedPath)
            logger.lifecycle("✅ Using Hytale server path: ${resolvedPath.absolutePath}")
        } else {
            logger.warn("⚠️ Could not set server path on the Hytale extension (no supported setter found).")
        }
    } else {
        logger.warn("⚠️ 'hytale' extension not found; cannot apply hytaleServerPath.")
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Specification-Title"] = rootProject.name
        attributes["Specification-Version"] = version
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] =
            providers.environmentVariable("COMMIT_SHA_SHORT")
                .map { "${version}-${it}" }
                .getOrElse(version.toString())
    }
}

publishing {
    repositories {
        // This is where you put repositories that you want to publish to.
        // Do NOT put repositories for your dependencies here.
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

// IDEA no longer automatically downloads sources/javadoc jars for dependencies, so we need to explicitly enable the behavior.
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

val syncAssets = tasks.register<Copy>("syncAssets") {
    group = "hytale"
    description = "Automatically syncs assets from Build back to Source after server stops."

    // Take from the temporary build folder (Where the game saved changes)
    from(layout.buildDirectory.dir("resources/main"))

    // Copy into your actual project source (Where your code lives)
    into("src/main/resources")

    // IMPORTANT: Protect the manifest template from being overwritten
    exclude("manifest.json")

    // If a file exists, overwrite it with the new version from the game
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    doLast {
        println("✅ Assets successfully synced from Game to Source Code!")
    }
}

afterEvaluate {
    // Now Gradle will find it, because the plugin has finished working
    val targetTask = tasks.findByName("runServer") ?: tasks.findByName("server")

    if (targetTask != null) {
        targetTask.finalizedBy(syncAssets)
        logger.lifecycle("✅ specific task '${targetTask.name}' hooked for auto-sync.")
    } else {
        logger.warn("⚠️ Could not find 'runServer' or 'server' task to hook auto-sync into.")
    }
}

// Deployment config
val deployHost = localOrGradleProp("deployHost")
val deployUser = localOrGradleProp("deployUser") ?: "debian"
val deployPort = localOrGradleProp("deployPort") ?: "22"
val deployPath = localOrGradleProp("deployPath")
    ?: "/home/debian/GameServers/Hytale/data/mods/"
val deployRestartCmd = localOrGradleProp("deployRestartCmd")
    ?: "cd /home/debian/GameServers/Hytale && docker compose restart"

val deployJarTaskName = if (tasks.findByName("shadowJar") != null) "shadowJar" else "jar"

fun requireProp(name: String, value: String?): String {
    val v = value?.trim()
    if (v.isNullOrEmpty()) {
        throw org.gradle.api.GradleException(
            "Missing required property '$name'. Set it in gradle.properties or ~/.gradle/gradle.properties."
        )
    }
    return v
}

fun findDeployableJar(): java.io.File {
    val libsDir = layout.buildDirectory.dir("libs").get().asFile
    val candidates = libsDir.listFiles { f ->
        f.isFile && f.extension == "jar" &&
            !f.name.endsWith("-sources.jar") &&
            !f.name.endsWith("-javadoc.jar")
    }?.toList().orEmpty()

    if (candidates.isEmpty()) {
        throw org.gradle.api.GradleException(
            "No deployable jar found in ${libsDir.absolutePath} (excluding *-sources.jar and *-javadoc.jar)."
        )
    }

    if (candidates.size > 1) {
        val newest = candidates.maxByOrNull { it.lastModified() }!!
        logger.lifecycle("Multiple jars found; deploying newest: ${newest.name}")
    }

    return candidates.maxByOrNull { it.lastModified() }!!
}

val deployJar = tasks.register<org.gradle.api.tasks.Exec>("deployJar") {
    group = "deployment"
    description = "Builds the jar and deploys it to the VPS via rsync."
    dependsOn(deployJarTaskName)

    doFirst {
        val host = requireProp("deployHost", deployHost)
        val user = deployUser.trim().ifEmpty { "debian" }
        val port = deployPort.trim().ifEmpty { "22" }
        val path = requireProp("deployPath", deployPath)
        val jarFile = findDeployableJar()

        environment("PATH", "/usr/bin:/bin:/usr/sbin:/sbin")
        commandLine(
            "/usr/bin/rsync",
            "-avz",
            "-e", "ssh -p $port",
            jarFile.absolutePath,
            "$user@$host:$path"
        )
    }
}

tasks.register<org.gradle.api.tasks.Exec>("deployJarAndRestart") {
    group = "deployment"
    description = "Deploys the jar and restarts the server on the VPS."
    dependsOn(deployJar)

    doFirst {
        val host = requireProp("deployHost", deployHost)
        val user = deployUser.trim().ifEmpty { "debian" }
        val port = deployPort.trim().ifEmpty { "22" }
        val restartCmd = requireProp("deployRestartCmd", deployRestartCmd)

        environment("PATH", "/usr/bin:/bin:/usr/sbin:/sbin")
        commandLine("/usr/bin/ssh", "-p", port, "$user@$host", restartCmd)
    }
}
