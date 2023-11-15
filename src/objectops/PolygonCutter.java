package objectops;

import objectdata.Point;
import objectdata.Polygon;

import java.util.ArrayList;

public class PolygonCutter {
    public Polygon clipPolygon(Polygon cuttingPolygon, Polygon cuttedPolygon) {
        // Vstupní polygon, který řeže zadaným uložíme do ArrayListu se kterým dále pracujeme, abychom nedrželi referenci
        // Pokud neprojde žádnou hranou, prostě se vrátí vstupní polygon
        ArrayList result = new ArrayList<>(cuttingPolygon.getPoints());
        int len = cuttedPolygon.getPoints().size();
        // Projdeme skrz každou hranu řezaného polygonu
        for (int i = 0; i < cuttedPolygon.getPoints().size(); i++) {

            int resultLength = result.size();
            ArrayList<Point> input = result;
            result = new ArrayList<>();

            // Získáme 2 body reprezentující hranu řezaného polygonu
            Point A = cuttedPolygon.getPoints().get((i + len - 1) % len);
            Point B = cuttedPolygon.getPoints().get(i);

            // Projdeme každou hranu řezajícího polygonu
            for (int j = 0; j < resultLength; j++) {

                // Získáme 2 body reprezentující hranu řezajícího polygonu
                Point C = input.get((j + resultLength - 1) % resultLength);
                Point D = input.get(j);

                if (isInside(A, B, D)) {
                    // Pokud C je mimo, tak vrátíme průsečík
                    if (!isInside(A, B, C))
                        result.add(intersection(A, B, C, D));
                    result.add(D);
                } else if (isInside(A, B, C)) {
                    result.add(intersection(A, B, C, D));
                }
            }
        }
        // Vytvoříme nový polygon s body co jsme získali
        Polygon resultPolygon = new Polygon();
        resultPolygon.addAllPoints(result);
        return resultPolygon;
    }

    // Kontrola zda C je uvnitř
    private boolean isInside(Point a, Point b, Point c) {
        return (a.x - c.x) * (b.y - c.y) <= (a.y - c.y) * (b.x - c.x);
    }

    // Najde průsečík úseček ab, cd
    private Point intersection(Point a, Point b, Point c, Point d) {
        double A1 = b.y - a.y;
        double B1 = a.x - b.x;
        double C1 = A1 * a.x + B1 * a.y;

        double A2 = d.y - c.y;
        double B2 = c.x - d.x;
        double C2 = A2 * c.x + B2 * c.y;

        double det = A1 * B2 - A2 * B1;
        double x = (B2 * C1 - B1 * C2) / det;
        double y = (A1 * C2 - A2 * C1) / det;

        return new Point((int)Math.round(x), (int)Math.round(y));
    }
}
