package com.mahalak.media.framework;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CacheEntry<T> {

    private final T data;

    private final long timestamp;

}
