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

    val minecraftConfigurationsForJava = listOf(
            "need:str:net/minecraft/block/Block",
            "need:str:net/minecraft/world/World",
            "need:str:net/minecraft/tileentity/TileEntity",
            "need:str:net/minecraft/entity/Entity",
            "need:str:net/minecraft/entity/EntityList",
            "need:str:net/minecraft/entity/NBTTagCompound",
            "need:str:net/minecraft/nbt/NBTBase",
            "need:str:net/minecraft/nbt/NBTTagCompound",
            "need:str:net/minecraft/nbt/NBTTagString",
            "need:str:net/minecraft/nbt/NBTTagList",
            "need:str:net/minecraft/nbt/NBTTagByteArray",
            "need:str:net/minecraft/nbt/NBTTagIntArray",
            "need:str:net/minecraft/nbt/NBTTagByte",
            "need:str:net/minecraft/nbt/NBTTagShort",
            "need:str:net/minecraft/nbt/NBTTagInt",
            "need:str:net/minecraft/nbt/NBTTagLong",
            "need:str:net/minecraft/nbt/NBTTagFloat",
            "need:str:net/minecraft/nbt/NBTTagDouble",
            "need:str:net/minecraft/util/ResourceLocation",

            "only:str:net/minecraft/block/",
            "only:str:net/minecraft/entity/",
            "only:str:net/minecraft/item/",
            "only:str:net/minecraft/nbt/",
            "only:str:net/minecraft/tileentity/",
            "only:str:net/minecraft/util/",
            "only:str:net/minecraft/world/",

            "exclude:str:net/minecraft/world/storage"
    )

    val generateCommJava by creating(JavaExec::class) {
        outputs.file(file("../build/generated/apiComm.jar"))
        group = "application"
        classpath = sourceSets.main.get().runtimeClasspath
        main = application.mainClassName

        dependsOn(downloadDependencies)

        args = listOf(
                "jar:${projectDir.resolve("../build/generated/apiComm.jar")}",

                "always-found:str:com/google/",

                "condition:or:not:if-srg:comment-disallow-either:minecraft 1.12.2 with srg by forge:minecraft 1.7.10 with srg by forge",

                "need:str:jp/ngt/ngtlib/io/NGTLog",
                "comment:str:ngtlib 1.12.2",
                "jar:file:str:$ngtlib1122",

                *minecraftConfigurationsForJava.toTypedArray(),
                "comment:str:minecraft 1.7.10 with srg by forge",
                "srg:zip:file:str:$mcpSrg1710!joined.srg",
                "mcp:zip:file:str:$mcpStable12!fields.csv",
                "mcp:zip:file:str:$mcpStable12!methods.csv",
                "jar:file:str:$minecraft1710ClientJar",

                *minecraftConfigurationsForJava.toTypedArray(),
                "comment:str:minecraft 1.12.2 with srg by forge",
                "sig:str:net/minecraft/util/IObjectIntIterable!<V:Ljava/lang/Object;>Ljava/lang/Object;Ljava/lang/Iterable<TV;>;",
                "srg:zip:file:str:$mcpSrg1122!joined.srg",
                "mcp:zip:file:str:$mcpStable39!fields.csv",
                "mcp:zip:file:str:$mcpStable39!methods.csv",
                "jar:file:str:$minecraft1122ClientJar"
        )
    }

    val generate1710Java by creating(JavaExec::class) {
        outputs.file(file("../build/generated/api1710.jar"))
        group = "application"
        classpath = sourceSets.main.get().runtimeClasspath
        main = application.mainClassName

        dependsOn(downloadDependencies)

        args = listOf(
                "jar:${projectDir.resolve("../build/generated/api1710.jar")}",

                "always-found:str:com/google/",

                "need:str:jp/ngt/ngtlib/io/NGTLog",
                "comment:str:ngtlib 1.12.2",
                "jar:file:str:$ngtlib1122",

                *minecraftConfigurationsForJava.toTypedArray(),
                "comment:str:minecraft 1.7.10 with srg by forge",
                "sig:str:net/minecraft/tileentity/TileEntity:field_145853_j!Ljava/util/Map<Ljava/lang/Class<*>;Ljava/lang/String;>;",
                "srg:zip:file:str:$mcpSrg1710!joined.srg",
                "mcp:zip:file:str:$mcpStable12!fields.csv",
                "mcp:zip:file:str:$mcpStable12!methods.csv",
                "exclude:str:net/minecraft/util/ChunkCoordinates",
                "exclude:str:net/minecraft/util/IChatComponent\$Serializer",
                "exclude:str:net/minecraft/util/ChatStyle\$Serializer",
                "jar:file:str:$minecraft1710ClientJar"
        )
    }

    val generate1122Java by creating(JavaExec::class) {
        outputs.file(file("../build/generated/api1122.jar"))
        group = "application"
        classpath = sourceSets.main.get().runtimeClasspath
        main = application.mainClassName

        dependsOn(downloadDependencies)

        args = listOf(
                "jar:${projectDir.resolve("../build/generated/api1122.jar")}",

                "always-found:str:com/google/",

                "need:str:jp/ngt/ngtlib/io/NGTLog",
                "jar:file:str:$ngtlib1122",

                *minecraftConfigurationsForJava.toTypedArray(),
                "comment:str:minecraft 1.12.2 with srg by forge",
                "sig:str:net/minecraft/util/IObjectIntIterable!<V:Ljava/lang/Object;>Ljava/lang/Object;Ljava/lang/Iterable<TV;>;",
                "srg:zip:file:str:$mcpSrg1122!joined.srg",
                "mcp:zip:file:str:$mcpStable39!fields.csv",
                "mcp:zip:file:str:$mcpStable39!methods.csv",
                "jar:file:str:$minecraft1122ClientJar"
        )
    }

    @Suppress("UNUSED_VARIABLE")
    val generateJavas by creating {
        group = "application"
        dependsOn(generateCommJava)
        dependsOn(generate1710Java)
        dependsOn(generate1122Java)
    }

    @Suppress("UNUSED_VARIABLE")
    val generateApiDts by creating(JavaExec::class) {
        outputs.file(file("../build/generated/api.d.ts"))
        inputs.dir(rootProject.sourceSets.getByName("mainComm").output.classesDirs.single())
        dependsOn(":mainCommClasses", ":dtsJdkClasses")
        group = "application"
        classpath = sourceSets.main.get().runtimeClasspath
        main = application.mainClassName

        args = listOf(
                "dts:${projectDir.resolve("../build/generated/api.d.ts")}",

                "always:",
                "only:str:com/anatawa12/mcWrapper/v2",
                "exclude:str:com/anatawa12/mcWrapper/v2/_InternalAccessor",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTBase!Ljava/lang/Object;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTByte!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTByteArray!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTCompound!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTDouble!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTFloat!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTInt!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTIntArray!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTList!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTLong!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTPrimitive!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTShort!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "sig:str:com/anatawa12/mcWrapper/v2/WNBTString!Lcom/anatawa12/mcWrapper/v2/WNBTBase;",
                "classes:str:${rootProject.sourceSets.getByName("mainComm").output.classesDirs.single()}"
        )
    }

    @Suppress("UNUSED_VARIABLE")
    val generateIncludedV2Dts by creating(JavaExec::class) {
        outputs.file(file("../build/generated/api.included.v2.d.ts"))
        inputs.dir(rootProject.sourceSets.getByName("mainComm").output.classesDirs.single())
        dependsOn(generateApiDts, ":mainCommClasses", ":dtsJdkClasses")
        group = "application"
        classpath = sourceSets.main.get().runtimeClasspath
        main = application.mainClassName

        args = listOf(
                "included-dts:${projectDir.resolve("../build/generated/api.included.v2.d.ts")}" +
                        "!/// <reference path=\"./api.d.ts\" />",

                "always:",
                "only:str:com/anatawa12/mcWrapper/v2",
                "exclude:str:com/anatawa12/mcWrapper/v2/_InternalAccessor",
                "classes:str:${rootProject.sourceSets.getByName("mainComm").output.classesDirs.single()}"
        )
    }
}
