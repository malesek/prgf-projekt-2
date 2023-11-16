package rasterop;

import objectdata.Elipse;
import objectdata.Point;
import objectdata.Rectangle;
import rasterdata.Raster;

public class ElipseRasterizer extends PolygonRasterizer{
    public ElipseRasterizer(Raster raster){super(raster);}

    public void drawElipse(Elipse elipse, Rectangle rectangle){
        int middleX = (rectangle.p1.x + rectangle.p2.x) / 2;
        int middleY = (rectangle.p1.y + rectangle.p2.y) / 2;
        int rX = Math.abs(rectangle.p1.x - rectangle.p2.x) / 2;
        int rY = Math.abs(rectangle.p1.y - rectangle.p2.y) / 2;
        for (int i = 0; i <= 360; i++) {
            int x, y;
            x = (int) Math.round(middleX + rX * Math.cos(Math.toRadians(i)));
            y = (int) Math.round(middleY + rY * Math.sin(Math.toRadians(i)));
            raster.setPixel(x,y,0xffff00);
            elipse.addPoint(new Point(x, y));
        }
        drawPolygon(elipse);
    }
}
