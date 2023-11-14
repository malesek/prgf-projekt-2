package rasterop.file;

import objectdata.Point;
import objectdata.Polygon;
import rasterdata.Raster;
import rasterop.FilledLineRasterizer;

import java.util.ArrayList;
import java.util.Collections;

public class ScanLine {
    Raster raster;

    public ScanLine(Raster _raster){
        raster = _raster;
    }

    /**
     * Metoda využívající ScanLine algoritmus pro vyplnění polygonu
     * @param polygon polygon ve kterém vykresluji úsečky
     * @param fillColor barva výplně polygonu
     */
    public void fillPolygon(Polygon polygon, int fillColor) {
        ArrayList<Point> points = polygon.getPoints();
        FilledLineRasterizer filledLineRasterizer = new FilledLineRasterizer(raster);
        int minY = raster.getHeight();
        int maxY = 0;

        // Nalezne minimální a maximální souřadnice y
        for (Point point : points) {
            if(point.y < minY)
                minY = point.y;
            if(point.y > maxY)
                maxY = point.y;
        }

        // Projdeme každou horizontální přímku
        for (int y = minY; y <= maxY; y++) {
            ArrayList<Integer> intersections = new ArrayList<>();

            // Nalezneme protnutí scanline s polygony a uložíme to protnutí do ArrayListu
            for (int i = 0; i < points.size(); i++) {
                Point p1 = points.get(i);
                Point p2 = points.get((i + 1) % points.size());

                if ((p1.y <= y && p2.y > y) || (p2.y <= y && p1.y > y)) {
                    double xIntersection = p1.x + (double) (y - p1.y) / (p2.y - p1.y) * (p2.x - p1.x);
                    intersections.add((int) Math.round(xIntersection));
                }
            }

            // Uspořádáme všechna protnutí
            Collections.sort(intersections);

            // Projdeme protnutími a vykreslíme úsečku ohraničenou danými protnutími
            // i+=2 ošetří úsečky které by byly vně algoritmu
            for (int i = 0; i < intersections.size(); i+=2) {
                int x1 = intersections.get(i);
                int x2 = intersections.get(i+1);
                filledLineRasterizer.rasterize(x1, y, x2, y, fillColor);
            }
        }
    }
}
