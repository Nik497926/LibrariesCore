package ru.vizzi.librariescore.resouces;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SimpleTextureData {
    private final int[] pixels;
    private final int width, height;
}
