package rasterop.file;

import objectdata.Point;
import rasterdata.Raster;

import java.util.Stack;

public class SeedFill{
    /**
     * Metoda využívající SeedFill algoritmus pro vyplnění obrazců
     * @param raster raster na který vykreslujeme
     * @param x x souřadnice počátečního bodu
     * @param y y souřadnice počátečního bodu
     * @param replacementColor barva kterou chceme pixel nahradit
     * @param targetColor nahrazovaná barva
     */
    public void fill(Raster raster, int x, int y, int replacementColor, int targetColor) {
        if (targetColor != replacementColor) {
            Stack<Point> stack = new Stack<>();
            stack.push(new Point(x, y));

            while (!stack.isEmpty()) {
                Point current = stack.pop();
                int currentX = current.x;
                int currentY = current.y;

                if (currentX >= 0 && currentX < raster.getWidth() &&
                        currentY >= 0 && currentY < raster.getHeight() &&
                        raster.getPixel(currentX, currentY) == targetColor) {

                    raster.setPixel(currentX, currentY, replacementColor);

                    stack.push(new Point(currentX - 1, currentY)); // Left
                    stack.push(new Point(currentX + 1, currentY)); // Right
                    stack.push(new Point(currentX, currentY - 1)); // Up
                    stack.push(new Point(currentX, currentY + 1)); // Down
                }
            }
        }
    }
}
