/*
 * Data Space Portal
 * Copyright (C) 2025 sovity GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

plugins {
    `java-library`
    alias(libs.plugins.openapiYamlGen) // ./gradlew clean resolve
    alias(libs.plugins.openapiCodegen) // ./gradlew openApiValidate && ./gradlew openApiGenerate
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.jakarta.wsRsApi)
    api(libs.jakarta.validation)
    api(libs.swaggerCore.annotations)
    api(libs.sovity.edc.api) { isChanging = libs.sovity.edc.api.get().version?.endsWith("SNAPSHOT") ?: false }

    implementation(libs.swaggerCore.jaxrs2)
    implementation(libs.jakarta.servletApi)
}

val openapiFileDir = project.layout.buildDirectory.get().asFile.toString()
val openapiFileFilename = "authority-portal.yaml"
val openapiFile = "$openapiFileDir/$openapiFileFilename"

var typescriptClientOutput = "../../authority-portal-frontend/src/app/core/api/client/generated"

val resolve = tasks.withType<io.swagger.v3.plugins.gradle.tasks.ResolveTask> {
    outputDir = file(openapiFileDir)
    outputFileName = openapiFileFilename.removeSuffix(".yaml")
    prettyPrint = true
    outputFormat = io.swagger.v3.plugins.gradle.tasks.ResolveTask.Format.YAML
    classpath = sourceSets["main"].runtimeClasspath
    // Tell the resolve task to only generate the OpenAPI spec from this folder.
    // Otherwise, it will use the OpenAPI spec from EDC-CE.
    resourcePackages = setOf("de.sovity.authorityportal.api")
}

task<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("openApiGenerateTypeScriptClient") {
    validateSpec.set(false)
    dependsOn(resolve)
    generatorName.set("typescript-fetch")
    configOptions.set(mutableMapOf(
        "supportsES6" to "true",
        "npmVersion" to libs.versions.npmVersion.get(),
        "typescriptThreePlus" to "true",
    ))

    inputSpec.set(openapiFile)
    val outputDirectory = buildFile.parentFile.resolve(typescriptClientOutput).normalize()
    outputDir.set(outputDirectory.toString())

    doFirst {
        project.delete(fileTree(outputDirectory).exclude("**/.gitignore"))
    }

    doLast {
        outputDirectory.resolve("src/generated").renameTo(outputDirectory)
        project.delete(fileTree(outputDirectory).include(
            ".openapi-generator",
            ".openapi-generator-ignore"
        ))
    }
}

// This task acts as an optional task to create an additional jar file
// containing the OpenAPI specification as a yaml file.
val openapiJar by tasks.registering(Jar::class) {
    dependsOn(resolve)
    archiveClassifier.set("openapi")
    from(openapiFile)
}
