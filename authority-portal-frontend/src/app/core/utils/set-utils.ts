/*
 * Copyright (c) 2024 sovity GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *      sovity GmbH - initial implementation
 */
export const isEqualSets = <T>(first: Set<T>, other: Set<T>): boolean => {
  return (
    first.size == other.size && [...first].every((value) => other.has(value))
  );
};
