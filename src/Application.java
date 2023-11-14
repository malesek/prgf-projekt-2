import objectdata.Point;
import objectdata.Polygon;
import objectops.PolygonCutter;
import rasterdata.RasterBufferedImage;
import rasterop.*;
import rasterop.file.ScanLine;
import rasterop.file.SeedFill;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


public class Application {

    private JFrame frame;
    private JPanel panel;
    private RasterBufferedImage raster;
    private DottedLineRasterizer dottedLineRasterizer;
    private PolygonRasterizer polygonRasterizer;
    private PolygonCutter polygonCutter;
    private Point point2;
    private Polygon polygon;
    private Polygon cuttedPolygon;
    private int index;
    private boolean deletePoint;
    SeedFill seedFill;
    ScanLine scanLine;
    private Point mouseHoverPoint;

    /**
     * Inicializační metoda využívaná i pro uvedení všech proměnných do původního stavu
     */
    public void initializer(){
        dottedLineRasterizer = new DottedLineRasterizer(raster);
        polygonRasterizer = new PolygonRasterizer(raster);
        polygonCutter = new PolygonCutter();
        polygon = new Polygon();
        point2 = null;
        index = -1;
        deletePoint = false;
        seedFill = new SeedFill();
        scanLine = new ScanLine(raster);
        cuttedPolygon = new Polygon();
        cuttedPolygon.addPoint(new Point(200, 100));
        cuttedPolygon.addPoint(new Point(200, 500));
        cuttedPolygon.addPoint(new Point(500, 500));
        cuttedPolygon.addPoint(new Point(500, 100));
        polygonRasterizer.drawPolygon(cuttedPolygon);
    }
    public Application(int width, int height) {
        //Inicializace okna
        frame = new JFrame();
        raster = new RasterBufferedImage(width, height);
        raster.setClearColor(0x2f2f2f);

        initializer();

        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF : " + this.getClass().getName());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }

        };

        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //zde se rozhoduje v jakém módu člověk bude na základě kliknutí na tlačítko
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_C :
                        fullClear();
                        break;
                    case KeyEvent.VK_K :
                        seedFill.fill(raster, mouseHoverPoint.x, mouseHoverPoint.y, 0x00005f, raster.getPixel(mouseHoverPoint.x, mouseHoverPoint.y));
                        panel.repaint();
                        break;
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    raster.setPixel(e.getX(), e.getY(), 0xffff00);
                    //ošetření aby se do polygonu přidal první bod pouze při prvním kliknutí
                    if (polygon.getPoints().size() == 0) {
                        polygon.addPoint(new Point(e.getX(), e.getY()));
                    }
                }
                if(e.getButton() == MouseEvent.BUTTON2){
                    deletePoint = true;
                    if(polygon.getPoints().size() > 2){
                        point2 = findClosestPoint(polygon.getPoints(), e.getX(), e.getY());
                        polygon.removePoint(index);
                        draw();
                    }
                }
                if(e.getButton() == MouseEvent.BUTTON3){
                    if(point2 != null){
                        point2 = findClosestPoint(polygon.getPoints(), e.getX(), e.getY());
                        polygon.removePoint(index);
                    }
                }
                panel.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(!deletePoint){
                    if(polygon.getPoints().size() != 0){
                        draw();
                        panel.repaint();
                    }
                }
                else deletePoint = false;
            }
        });
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(polygon.getPoints().size() != 0){
                    if(!deletePoint) {
                        point2 = new Point(e.getX(), e.getY());
                        drawDrag();
                    }
                    panel.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseHoverPoint = new Point(e.getX(), e.getY());
            }
        });


    }

    /**
     * Metoda pro najití nejbližšího bodu v módu kreslení polygonu
     * @param points body v polygonu
     * @param clickX poloha x po kliknutí myší
     * @param clickY poloha y po kliknutí myší
     * @return
     */
    public Point findClosestPoint(ArrayList<Point> points, double clickX, double clickY) {
        double minDistance = Double.MAX_VALUE;
        Point closestPoint = null;

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            double distance = Math.sqrt(Math.pow((clickX - point.x), 2) + Math.pow((clickY - point.y), 2));
            if (distance < minDistance) {
                minDistance = distance;
                index = i;
                closestPoint = point;
            }
        }
        return closestPoint;
    }

    /**
     * Method for drawing done lines after mouseReleased
     * Metoda pro kreslení hotových čar po zvednutí tlačítka myši
     */
    public void draw(){
        clear();
        if(index == -1 && !deletePoint){
            polygon.addPoint(new Point(point2.x, point2.y));
        }
        if(index != -1 && !deletePoint) {
            polygon.addPointAtIndex(index, new Point(point2.x, point2.y));
            index = -1;
        }
        Polygon cutPolygon = polygonCutter.clipPolygon(polygon, cuttedPolygon);
        if(polygon.getPoints().size() > 2)
            scanLine.fillPolygon(cutPolygon, 0xff0000);
        polygonRasterizer.drawPolygon(cuttedPolygon);
        polygonRasterizer.drawPolygon(polygon);
    }
    /**
     * Metoda pro kreslení tečkovaných čar při táhnutí myší
     */
    public void drawDrag(){
        clear();
        //zde řeším zda táhne člověk pravým tlačítkem a pokud ano ošetřuji chyby
        if(index == -1){
            dottedLineRasterizer.rasterize(polygon.getPoints().get(0), point2, 0xffff00);
            dottedLineRasterizer.rasterize(polygon.getPoints().get(polygon.getPoints().size()-1), point2, 0xffff00);
            polygonRasterizer.drawPolygon(polygon);
        }
        else if(index == 0){
            dottedLineRasterizer.rasterize(polygon.getPoints().get(polygon.getPoints().size()-1), point2, 0xffff00);
            dottedLineRasterizer.rasterize(polygon.getPoints().get(index), point2, 0xffff00);
            polygonRasterizer.drawPolygon(polygon);
        }
        else{
            dottedLineRasterizer.rasterize(polygon.getPoints().get(index-1 < 0 ? polygon.getPoints().size()-1 : index-1), point2, 0xffff00);
            dottedLineRasterizer.rasterize(polygon.getPoints().get(index > polygon.getPoints().size()-1 ? 0 : index), point2, 0xffff00);
            polygonRasterizer.drawPolygon(polygon);
        }
    }

    /**
     * Vyčístí raster a všechny proměnné (kromě drawMode abysme zůstali v kreslícím módu, ve kterém jsme byli)
     */
    public void fullClear(){
        clear();
        initializer();
        panel.repaint();
    }

    /**
     * Vymaže pouze raster
     */
    public void clear(){
        raster.clear();
        raster.getGraphics().drawString("vyplnovani polygonu", 5, 15);
        polygonRasterizer.drawPolygon(cuttedPolygon);
    }

    public void start() {
        clear();
        panel.repaint();
    }

    public void present(Graphics graphics){
        raster.repaint(graphics);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Application(800, 600).start());
    }

}