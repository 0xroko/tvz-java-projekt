/*
 * Copyright (c) 2024.
 *
 *
 */

package com.r.projektnizad.models.change;

import java.io.Serializable;
import java.util.Map;

public record DiffContainer<T, P>(Map<T, Diff<P>> diffMap) implements Serializable {
}
