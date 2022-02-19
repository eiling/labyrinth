package com.magicaleiling.labyrinth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import static com.magicaleiling.labyrinth.Tile.*;

public class Labyrinth {
  private final int width;
  private final int height;
  private final Tile[] tiles;
  private final double keepChance;
  private final int[] tileOrder;
  private int tileOrderIndex;
  private int maxDepth = 0;

  public Labyrinth(
      final int width,
      final int height,
      final double keepChance
  ) {
    this.width = width;
    this.height = height;
    this.keepChance = keepChance;

    int len = (width + 2) * (height + 2);
    tiles = new Tile[len];
    for (int i = 0; i < len; i++) {
      if (
          (i < width + 2)
              || (i % (width + 2) == 0)
              || (i >= (width + 2) * (height + 1))
              || (i % (width + 2) == width + 1)
      ) {
        tiles[i] = new Tile(BLOCK);
      } else {
        tiles[i] = new Tile();
      }
    }

    tileOrder = new int[tiles.length];
    tileOrderIndex = 0;
  }

  public int[] orderArray() {
    int[] temp = new int[tileOrderIndex];
    System.arraycopy(tileOrder, 0, temp, 0, tileOrderIndex);
    for (int i = 0; i < temp.length / 2; i++) {
      int k = temp[i];
      temp[i] = temp[temp.length - 1 - i];
      temp[temp.length - 1 - i] = k;
    }
    return temp;
  }

  public int maxDepth() {
    return maxDepth;
  }

  public void generate() {
    generate(false);
  }

  public void generate(boolean exits) {
    tiles[1 + width + 2].apply(LEFT);
    nextTile(1 + width + 2, RIGHT, 0);
    if (exits) {
      tiles[width + 2] = new Tile(UP, DOWN);
      tiles[1 + width + 2].remove(LEFT);
      tiles[width + height * (width + 2)].remove(RIGHT);
      tiles[width + 1 + height * (width + 2)] = new Tile(UP, DOWN);
    }
  }

  private boolean nextTile(int tile, int lastMove, int depth) {
    if (depth > maxDepth) {
      maxDepth = depth;
    }

    if (tiles[tile].checkAny(VISITED, BLOCK)) {
      return false;
    }

    tiles[tile].apply(VISITED);

    final int[] possibleMoves = getDirections(lastMove);
    int moveCount = possibleMoves.length;
    double r = random();
    if (r < keepChance) {
      if (!nextTile(tile + getIndexChange(lastMove), lastMove, depth + 1)) {
        tiles[tile].apply(lastMove);
      }
      moveCount--;
    }

    while (moveCount > 0) {
      int index = random(moveCount--);
      int move = possibleMoves[index];
      if (!nextTile(tile + getIndexChange(move), move, depth + 1)) {
        tiles[tile].apply(move);
      }
      System.arraycopy(
          possibleMoves, index + 1, possibleMoves, index, moveCount - index
      );
      /*  the madness above is equivalent to:
      for (int i = index; i < moveCount; i++) {
        possibleMoves[i] = possibleMoves[i + 1];
      }
      */
    }

    tileOrder[tileOrderIndex++] = tile;
    return true;
  }

  private int getIndexChange(int direction) {
    int[] components = getDirectionComponents(direction);
    return -components[0]
        - (width + 2) * components[1]
        + components[2]
        + (width + 2) * components[3];
  }

  private double random() {
    return ThreadLocalRandom.current().nextDouble();
  }

  private int random(int bound) {
    return ThreadLocalRandom.current().nextInt(bound);
  }

  public String html() {
    var sb = new StringBuilder();
    sb.append("<div id=\"maze\">");
    for (int j = 0; j < height + 2; j++) {
      sb.append("<div class=\"row\">");
      for (int i = 0; i < width + 2; i++) {
        sb.append(tileHtml(i + j * (width + 2)));
      }
      sb.append("</div>");
    }
    sb.append("</div>");
    try {
      Files.writeString(
          Paths.get("result.html"),
          Files.readString(Paths.get("template.html"))
              .replace("{{}}", sb.toString())
              .replace("[[]]", Arrays.toString(orderArray()))
      );
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    return Paths.get(System.getProperty("user.dir"), "result.html")
        .toUri().toString();
  }

  private String tileHtml(int tile) {
    return String.format(
        "<div id=\"%d\">"
            + "<div class=\"row\">"
            + "<div class=\"column corner\"></div>"
            + "<div class=\"column h-wall %s\"></div>"
            + "<div class=\"column corner\"></div>"
            + "</div>"
            + "<div class=\"row\">"
            + "<div class=\"column v-wall %s\"></div>"
            + "<div class=\"column tile %s\"></div>"
            + "<div class=\"column v-wall %s\"></div>"
            + "</div>"
            + "<div class=\"row\">"
            + "<div class=\"column corner\"></div>"
            + "<div class=\"column h-wall %s\"></div>"
            + "<div class=\"column corner\"></div>"
            + "</div>"
            + "</div>",
        tile,
        tiles[tile].checkAny(BLOCK)
            ? "block"
            : tiles[tile].checkAny(UP) ? "wall" : "path",
        tiles[tile].checkAny(BLOCK)
            ? "block"
            : tiles[tile].checkAny(LEFT) ? "wall" : "path",
        tiles[tile].checkAny(BLOCK) ? "block" : "path",
        tiles[tile].checkAny(BLOCK)
            ? "block"
            : tiles[tile].checkAny(RIGHT) ? "wall" : "path",
        tiles[tile].checkAny(BLOCK)
            ? "block"
            : tiles[tile].checkAny(DOWN) ? "wall" : "path"
    );
  }

  @Override
  public String toString() {
    var sb = new StringBuilder();
    for (int j = 0; j < height + 2; j++) {
      sb.append(tiles[j * (width + 2)].toString());
      for (int i = 1; i < width + 2; i++) {
        sb.append(' ').append(tiles[i + j * (width + 2)].toString());
      }
      sb.append(System.lineSeparator());
    }
    sb.append(Arrays.toString(tileOrder));
    return sb.toString();
  }
}
