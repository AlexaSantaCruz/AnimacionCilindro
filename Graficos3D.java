import java.awt.Color;
import java.awt.image.BufferedImage;

public class Graficos3D {
    private BufferedImage buffer;
    private int WIDTH;
    private int HEIGHT;
    private double[][] zBuffer;

    public Graficos3D(int WIDTH, int HEIGHT) {
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        zBuffer = new double[WIDTH][HEIGHT];
        resetBuffer();
    }

    public void drawLine(int x0, int y0, int x1, int y1, double z0, double z1, Color color) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int pasos = Math.max(Math.abs(dx), Math.abs(dy));
        float xIncremento = (float) dx / pasos;
        float yIncremento = (float) dy / pasos;
        float zIncremento = (float) (z1 - z0) / pasos;
        float x = x0;
        float y = y0;
        float z = (float) z0;

        for (int i = 0; i <= pasos; i++) {
            putPixel(Math.round(x), Math.round(y), z, color);
            x += xIncremento;
            y += yIncremento;
            z += zIncremento;
        }
    }

    public void putPixel(int x, int y, double z, Color color) {
        if (x >= 0 && x < buffer.getWidth() && y >= 0 && y < buffer.getHeight()) {
            if (z < zBuffer[x][y]) {
                buffer.setRGB(x, y, color.getRGB());
                zBuffer[x][y] = z;
            }
        }
    }

    public void resetBuffer() {
        buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                zBuffer[i][j] = Double.MAX_VALUE;
            }
        }
    }

    public BufferedImage getBuffer() {
        return buffer;
    }
}
