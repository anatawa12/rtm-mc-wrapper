// COMPILER_ARGUMENTS: -XXLanguage:+TrailingCommas

import java.net.URL
import java.io.IOException

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.3.70"

    // Apply the application plugin to add support for building a CLI application.
    application
}

val ktor_version = "1.3.2"

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.ow2.asm:asm:6.0")
    implementation("org.ow2.asm:asm-commons:6.0")

   // // use the ktor
   // implementation("io.ktor:ktor-io:$ktor_version")
   // implementation("io.ktor:ktor-client-cio:$ktor_version")
}

application {
    // Define the main class for the application.
    mainClassName = "com.anatawa12.dtsGenerator.AppKt"
}

val downloadedDir = buildDir.resolve("downloaded")

data class DownloadedInfo(val file: File, val url: String, val name: String, val version: String)
val downloadeds = mutableListOf<DownloadedInfo>()

fun downloaded(name: String, version: String, classifier: String? = null, extension: String = "jar", url: String): File {
    var fileName = "$name-$version"
    if (classifier != null)
        fileName += "-$classifier"
    fileName += ".$extension"
    val file = downloadedDir.resolve(fileName)
    downloadeds.add(DownloadedInfo(file, url, name, version))
    return file
}

val minecraft1122ClientJar = downloaded("minecraft", "1.12.2", classifier = "client", 
         url = "https://launcher.mojang.com/v1/objects/0f275bc1547d01fa5f56ba34bdc87d981ee12daf/client.jar")

val mcpSrg1122 = downloaded("mcp", "1.12.2", extension = "zip", 
         url = "https://files.minecraftforge.net/maven/de/oceanlabs/mcp/mcp/1.12.2/mcp-1.12.2-srg.zip")

val mcpStable39 = downloaded("mcp-mapping", "stable-39", extension = "zip", 
         url = "http://export.mcpbot.bspk.rs/mcp_stable/39-1.12/mcp_stable-39-1.12.zip")

val ngtlib1122 = downloaded("ngtlib", "2.4.19-37", 
         url = "redl:https://addons-ecs.forgesvc.net/api/v2/addon/288989/file/2934633/download-url")


val minecraft1710ClientJar = downloaded("minecraft", "1.7.10", classifier = "client", 
         url = "https://launcher.mojang.com/v1/objects/e80d9b3bf5085002218d4be59e668bac718abbc6/client.jar")

val mcpSrg1710 = downloaded("mcp", "1.7.10", extension = "zip", 
         url = "https://files.minecraftforge.net/maven/de/oceanlabs/mcp/mcp/1.7.10/mcp-1.7.10-srg.zip")

val mcpStable12 = downloaded("mcp-mapping", "stable-12", extension = "zip", 
         url = "http://export.mcpbot.bspk.rs/mcp_stable/12-1.7.10/mcp_stable-12-1.7.10.zip")

tasks {
    val downloadDependencies by creating

    fun downloadDependencyTask(downloadedInfo: DownloadedInfo) = task(org.gradle.util.GUtil.toLowerCamelCase("download ${downloadedInfo.name} version ${downloadedInfo.version}")) {
        downloadDependencies.dependsOn(this)
        val (file, url) = downloadedInfo
        outputs.file(file)
        doLast {
            downloadedDir.mkdirs()
            try {
                if (url.startsWith("redl:")) {
                    URL(URL(url.substring("redl:".length)).openStream().reader().readText())
                            .openStream().copyTo(file.outputStream())
                } else {
                    URL(url).openStream().copyTo(file.outputStream())
                }
            } catch (e: IOException) {
                throw IOException("downloading '$url' or writing to '$file'", e)
            }
        }
    }

    for (downloaded in downloadeds) {
        downloadDependencyTask(downloaded)
    }

    val minecraftConfigurations = listOf(
            "only:str:net/minecraft/block/",
            "only:str:net/minecraft/entity/",
            "only:str:net/minecraft/item/",
            "only:str:net/minecraft/nbt/",
            "only:str:net/minecraft/tileentity/",
            "only:str:net/minecraft/util/",
            "only:str:net/minecraft/world/",
            "exclude:str:net/minecraft/world/storage"
    )

    val generateDts by creating(JavaExec::class) {
        group = "application"
        classpath = sourceSets.main.get().runtimeClasspath
        main = application.mainClassName

        dependsOn(downloadDependencies)

        args = listOf(
                "dts:../minecraft.d.ts",

                "only:str:org/ietf/jgss/",
                "only:str:org/omg/",
                "only:str:java/",
                "only:str:javax/",
                "jar:file:jvm-home:lib/rt.jar",

                "always:",
                "only:str:java/util/",
                "only:str:java/lang/reflect/",
                "jar:file:jvm-home:lib/rt.jar",

                "always:",
                "only:str:jp/ngt/ngtlib/io",
                "comment:str:ngtlib 1.12.2",
                "jar:file:str:$ngtlib1122",

                "always:",
                *minecraftConfigurations.toTypedArray(),
                "comment:str:minecraft 1.12.2 with srg by forge",
                "sig:str:net/minecraft/util/IObjectIntIterable!<V:Ljava/lang/Object;>Ljava/lang/Object;Ljava/lang/Iterable<TV;>;",
                "srg:zip:file:str:$mcpSrg1122!joined.srg",
                "mcp:zip:file:str:$mcpStable39!fields.csv",
                "mcp:zip:file:str:$mcpStable39!methods.csv",
                "jar:file:str:$minecraft1122ClientJar",

                "always:",
                *minecraftConfigurations.toTypedArray(),
                "comment:str:minecraft 1.7.10 with srg by forge",
                "srg:zip:file:str:$mcpSrg1710!joined.srg",
                "mcp:zip:file:str:$mcpStable12!fields.csv",
                "mcp:zip:file:str:$mcpStable12!methods.csv",
                "jar:file:str:$minecraft1710ClientJar",

                "jar:get:str:https://libraries.minecraft.net/com/google/guava/guava/21.0/guava-21.0.jar",

                "exclude:str:com/google/gson/internal/",
                "jar:get:str:https://libraries.minecraft.net/com/google/code/gson/gson/2.8.0/gson-2.8.0.jar"
        )
    }
}
