package com.magicaleiling.labyrinth;

public class Tile {
  public static final int DIRECTION_COUNT = 4;
  public static final int HALF_DIRECTION_COUNT = 2;
  private static final int DIRECTION_BITS = (1 << DIRECTION_COUNT) - 1;
  private static final int LEFT_BIT = 0;
  private static final int UP_BIT = 1;
  private static final int RIGHT_BIT = 2;
  private static final int DOWN_BIT = 3;
  private static final int VISITED_BIT = 30;
  private static final int BLOCK_BIT = 31;
  public static final int LEFT = 1 << LEFT_BIT;
  public static final int UP = 1 << UP_BIT;
  public static final int RIGHT = 1 << RIGHT_BIT;
  public static final int DOWN = 1 << DOWN_BIT;
  public static final int VISITED = 1 << VISITED_BIT;
  public static final int BLOCK = 1 << BLOCK_BIT;
  private int value;

  public Tile(int... properties) {
    value = 0;
    for (final int property : properties) {
      apply(property);
    }
  }

  public void apply(int... properties) {
    for (final int property : properties) {
      value |= property;
    }
  }

  public void remove(int... properties) {
    for (final int property : properties) {
      value &= ~property;
    }
  }

  public boolean checkAny(int... properties) {
    int result = 0;
    for (final int property : properties) {
      result |= value & property;
    }
    return result != 0;
  }

  public static int[] getDirections(int direction) {
    final int[] directions = new int[DIRECTION_COUNT - 1];
    for (int i = 1; i < HALF_DIRECTION_COUNT; i++) {
      directions[i - 1] =
          ((direction >> i) | (direction << (DIRECTION_COUNT - i))) & DIRECTION_BITS;
      directions[HALF_DIRECTION_COUNT - i] =
          ((direction >> (DIRECTION_COUNT - i)) | (direction << i)) & DIRECTION_BITS;
    }
    directions[DIRECTION_COUNT - 2] = direction;
    return directions;
  }

  public static int[] getDirectionComponents(int direction) {
    return new int[]{
        (direction >> LEFT_BIT) & 1,
        (direction >> UP_BIT) & 1,
        (direction >> RIGHT_BIT) & 1,
        (direction >> DOWN_BIT) & 1
    };
  }

  @Override
  public String toString() {
    return leftZeroPad(Integer.toBinaryString(value), 32);
  }

  private String leftZeroPad(String s, int len) {
    int zerosNeeded = len - s.length();
    if (zerosNeeded > 0) {
      return "0".repeat(zerosNeeded) + s;
    }
    return s;
  }
}
