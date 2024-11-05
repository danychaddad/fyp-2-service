package com.forestfire.app.forests.vertex;

import org.springframework.data.annotation.Id;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Vertex {
  @Id
  private String id;

  private float longitude;
  private float latitude;
}