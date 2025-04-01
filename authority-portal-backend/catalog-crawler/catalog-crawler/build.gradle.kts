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
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)

    api(libs.edc.controlPlaneAggregateServices)
    api(libs.edc.controlPlaneCore)
    api(libs.edc.controlPlaneSpi)
    api(libs.edc.dataPlaneSelectorCore)
    api(libs.edc.dsp)
    api(libs.edc.http)
    api(libs.edc.jsonLd)
    api(libs.edc.managementApiConfiguration)
    api(libs.edc.oauth2Core)

    implementation(libs.quartz.quartz)
    implementation(libs.commons.lang3)
    implementation(libs.quarkus.jooq)

    api(libs.sovity.edc.catalogParser)
    api(libs.sovity.edc.jsonAndJsonLdUtils)
    api(libs.sovity.edc.wrapperCommonMappers)
    api(libs.sovity.edc.ext.postgresFlywayCore)
    api(libs.sovity.edc.config)
    api(project(":authority-portal-db"))

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(libs.sovity.edc.ext.testUtils)
    testImplementation(libs.assertj.core)
    testImplementation(libs.mockito.core)
    testImplementation(libs.restAssured.restAssured)
    testImplementation(libs.testcontainers.testcontainers)
    testImplementation(libs.flyway.core)
    testImplementation(libs.testcontainers.junitJupiter)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.junit.api)
    testImplementation(libs.jsonAssert)
    testRuntimeOnly(libs.junit.engine)
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
    maxParallelForks = 1
}
