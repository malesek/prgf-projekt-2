package rasterop;

import objectdata.Point;
import objectdata.Rectangle;
import rasterdata.Raster;

public class RectangleRasterizer extends FilledLineRasterizer{
    public RectangleRasterizer(Raster raster) {
        super(raster);
    }

    public void drawRectangle(Rectangle rectangle){
        int x1 = rectangle.p1.x;
        int y1 = rectangle.p1.y;
        int x2 = rectangle.p2.x;
        int y2 = rectangle.p2.y;

        rectangle.addPoint(new Point(x1,y1));
        rectangle.addPoint(new Point(x2,y1));
        rectangle.addPoint(new Point(x2,y2));
        rectangle.addPoint(new Point(x1,y2));
        rasterize(new Point(x1, y1), new Point(x2, y1), 0xffff00);
        rasterize(new Point(x2, y1), new Point(x2, y2), 0xffff00);
        rasterize(new Point(x2, y2), new Point(x1, y2), 0xffff00);
        rasterize(new Point(x1, y2), new Point(x1, y1), 0xffff00);
        drawPixelLocation(x1, y1, 0xff0000);
        drawPixelLocation(x2, y2, 0x0000ff);
    }
    //Využívané pro zvýraznění místa bodu
    private void drawPixelLocation(int x, int y, int color){
        raster.setPixel(x, y, color);
        raster.setPixel(x, y-1, color);
        raster.setPixel(x, y+1, color);
        raster.setPixel(x-1, y, color);
        raster.setPixel(x+1, y, color);
        raster.setPixel(x+1, y+1, color);
        raster.setPixel(x-1, y-1, color);
        raster.setPixel(x+1, y-1, color);
        raster.setPixel(x-1, y+1, color);
    }
}
