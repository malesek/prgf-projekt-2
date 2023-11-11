package rasterop;

import objectdata.Point;
import rasterdata.Raster;

import java.util.Stack;

public class SeedFill{
    public void fill(Raster image, int x, int y, int replacementColor, int targetColor) {
        if (targetColor != replacementColor) {
            Stack<Point> stack = new Stack<>();
            stack.push(new Point(x, y));

            while (!stack.isEmpty()) {
                Point current = stack.pop();
                int currentX = current.x;
                int currentY = current.y;

                if (currentX >= 0 && currentX < image.getWidth() &&
                        currentY >= 0 && currentY < image.getHeight() &&
                        image.getPixel(currentX, currentY) == targetColor) {

                    image.setPixel(currentX, currentY, replacementColor);

                    stack.push(new Point(currentX - 1, currentY)); // Left
                    stack.push(new Point(currentX + 1, currentY)); // Right
                    stack.push(new Point(currentX, currentY - 1)); // Up
                    stack.push(new Point(currentX, currentY + 1)); // Down
                }
            }
        }
    }
}
