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
package de.sovity.edc.ext.vault.in_memory

import org.eclipse.edc.boot.vault.InMemoryVault
import org.eclipse.edc.runtime.metamodel.annotation.Extension
import org.eclipse.edc.runtime.metamodel.annotation.Provides
import org.eclipse.edc.spi.security.Vault
import org.eclipse.edc.spi.system.ServiceExtension
import org.eclipse.edc.spi.system.ServiceExtensionContext

@Extension(value = "InMemoryVaultExtension")
@Provides(Vault::class)
class InMemoryVaultExtension : ServiceExtension {
    override fun name(): String = InMemoryVaultExtension::class.java.name

    private val sovityVaultInMemoryInitWildcard = "sovity.vault.in-memory.init.*"

    override fun initialize(context: ServiceExtensionContext) {
        val vault = getVault(context)
        context.registerService(Vault::class.java, vault)
    }

    private fun getVault(context: ServiceExtensionContext): Vault {
        val monitor = context.monitor
        val config = context.config
        val vault = InMemoryVault(monitor)

        // Initialize the vault via env
        val prefix = sovityVaultInMemoryInitWildcard.removeSuffix("*")
        val entries = config.entries
            .filter { it.key.startsWith(prefix) }
            .map { it.key.removePrefix(prefix) to it.value }

        entries.forEach { (key, value) ->
            monitor.info("Initializing In-Memory Vault Entry '$key' with (omitted, length ${value.length}).")
            vault.storeSecret(key, value)
        }

        return vault
    }
}
