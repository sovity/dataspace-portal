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

import org.flywaydb.gradle.task.FlywayCleanTask
import org.flywaydb.gradle.task.FlywayMigrateTask
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Nullability
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer

val jooqDbType = "org.jooq.meta.postgres.PostgresDatabase"
val jdbcDriver = "org.postgresql.Driver"
val postgresContainer = libs.versions.postgresDbImage.get()

val migrationsDir = "src/main/resources/db/migration"
val jooqTargetPackage = "de.sovity.authorityportal.db.jooq"
val jooqTargetSourceRoot = "build/generated/jooq"

val jooqTargetDir = jooqTargetSourceRoot + "/" + jooqTargetPackage.replace(".", "/")
val flywayMigration: Configuration = configurations.create("flywayMigration")

buildscript {
    dependencies {
        classpath(libs.testcontainers.postgresql)
    }
}

plugins {
    alias(libs.plugins.flyway)
    alias(libs.plugins.jooq)
    `java-library`
    `maven-publish`
}

dependencies {
    api(libs.jooq)
    api(libs.jooq.ext.postgresJson)

    jooqGenerator(libs.postgresql)
    flywayMigration(libs.postgresql)
}

sourceSets {
    main {
        java {
            srcDirs.add(File(jooqTargetSourceRoot))
        }
    }
}


val skipTestcontainersEnvVarName = "SKIP_TESTCONTAINERS"
val jdbcUrlEnvVarName = "TEST_POSTGRES_JDBC_URL"
val jdbcUserEnvVarName = "TEST_POSTGRES_JDBC_USER"
val jdbcPasswordEnvVarName = "TEST_POSTGRES_JDBC_PASSWORD"
var container: JdbcDatabaseContainer<*>? = null

fun isTestcontainersEnabled(): Boolean {
    return System.getenv()[skipTestcontainersEnvVarName] != "true"
}

fun jdbcUrl(): String {
    return container?.jdbcUrl
        ?: System.getenv()[jdbcUrlEnvVarName]
        ?: error("Need $jdbcUrlEnvVarName since $skipTestcontainersEnvVarName=true")
}

fun jdbcUser(): String {
    return container?.username
        ?: System.getenv()[jdbcUserEnvVarName]
        ?: error("Need $jdbcUserEnvVarName since $skipTestcontainersEnvVarName=true")
}

fun jdbcPassword(): String {
    return container?.password
        ?: System.getenv()[jdbcPasswordEnvVarName]
        ?: error("Need $jdbcPasswordEnvVarName since $skipTestcontainersEnvVarName=true")
}


tasks.register("startTestcontainer") {
    doLast {
        if (isTestcontainersEnabled()) {
            container = PostgreSQLContainer<Nothing>(postgresContainer)
            container!!.start()
            gradle.buildFinished {
                container?.stop()
            }
        }
    }
}

flyway {
    driver = jdbcDriver
    schemas = arrayOf("public")

    cleanDisabled = false
    cleanOnValidationError = true
    baselineOnMigrate = true

    // We need to mix transactional and non-transactional transactions in migrations,
    // because we want to change enum values in migrations
    mixed = true

    locations = arrayOf("filesystem:${migrationsDir}")
    configurations = arrayOf("flywayMigration")
}

tasks.withType<FlywayCleanTask> {
    doFirst {
        require(this is FlywayCleanTask)
        url = jdbcUrl()
        user = jdbcUser()
        password = jdbcPassword()
    }
}

tasks.withType<FlywayMigrateTask> {
    dependsOn.add("startTestcontainer")
    doFirst {
        require(this is FlywayMigrateTask)
        url = jdbcUrl()
        user = jdbcUser()
        password = jdbcPassword()
    }
}

jooq {
    configurations {
        create("main") {
            jooqConfiguration.apply {
                generator.apply {
                    database.apply {
                        name = jooqDbType
                        excludes = "(.*)flyway_schema_history(.*)"
                        inputSchema = flyway.schemas[0]

                        withForcedTypes(
                            // Force "List<String>" over "String[]" for PostgreSQL "text[]"
                            ForcedType()
                                .withUserType("java.util.List<String>")
                                .withIncludeTypes("_text|_varchar")
                                .withConverter("""org.jooq.Converter.ofNullable(
                                    String[].class,
                                    (Class<java.util.List<String>>) (Class) java.util.List.class,
                                    array -> array == null ? null : java.util.Arrays.asList(array),
                                    list -> list == null ? null : list.toArray(new String[0])
                                  )""")
                                .withNullability(Nullability.ALL)
                        )
                    }
                    generate.apply {
                        isRecords = true
                        isRelations = true
                        isJavaTimeTypes = true
                    }
                    target.apply {
                        packageName = jooqTargetPackage
                        directory = jooqTargetDir
                    }
                }
            }
        }
    }
}

tasks.withType<nu.studer.gradle.jooq.JooqGenerate> {
    dependsOn.add("flywayMigrate")
    inputs.files(fileTree(migrationsDir))
        .withPropertyName("migrations")
        .withPathSensitivity(PathSensitivity.RELATIVE)
    allInputsDeclared.set(true)
    outputs.cacheIf { true }
    doFirst {
        require(this is nu.studer.gradle.jooq.JooqGenerate)

        val jooqConfiguration = nu.studer.gradle.jooq.JooqGenerate::class.java.getDeclaredField("jooqConfiguration")
            .also { it.isAccessible = true }.get(this) as org.jooq.meta.jaxb.Configuration

        jooqConfiguration.jdbc.apply {
            url = jdbcUrl()
            user = jdbcUser()
            password = jdbcPassword()
        }
    }
    doLast {
        container?.stop()
    }
}
