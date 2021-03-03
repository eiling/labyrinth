package com.magicaleiling.labyrinth;

import java.util.Arrays;

public class App {
  public static void main(String[] args) {
    var labyrinth = new Labyrinth(40, 30, .8);
    labyrinth.generate(false);
    System.out.println();
    System.out.println(labyrinth);
    System.out.println(labyrinth.html());
    System.out.println(Arrays.toString(labyrinth.orderArray()));
    System.out.println(labyrinth.maxDepth());
  }
}
