package com.example.demo.image;

import lombok.Data;

@Data
public class ImageRequestDto {
    private String appearance;
    private String behavior; // behavior prompt + optional custom behavior
}
