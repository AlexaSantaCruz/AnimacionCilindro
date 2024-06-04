import java.awt.geom.Point2D;
import java.awt.Color;

class Esfera3D implements Runnable {

    private double radius;
    private double[] center;
    private cilindro2 parent;
    private boolean animacionActiva = false;
    private double anguloY = 0;
    private double speed = 4;
    private boolean rotating;
    private boolean rotateOnAxis;

    public Esfera3D(double radius, cilindro2 parent, boolean rotating, boolean rotateOnAxis) {
        this.radius = radius;
        this.parent = parent;
        this.rotating = rotating;
        this.rotateOnAxis = rotateOnAxis;
        this.center = new double[]{parent.getWidth() / 2, parent.getHeight() / 2, 0};
    }

    public double getDiameter() {
        return 2 * radius;
    }

    public void moveUp() {
        center[1] -= speed;
    }

    public void moveDown() {
        center[1] += speed;
    }

    public void startAnimation() {
        animacionActiva = true;
        new Thread(this).start();
    }

    public void stopAnimation() {
        animacionActiva = false;
    }

    @Override
    public void run() {
        boolean goingUp = true;
        while (animacionActiva) {
            if (goingUp) {
                moveUp();
                if (center[1] <= radius) {
                    goingUp = false;
                }
            } else {
                moveDown();
                if (center[1] >= parent.getHeight() - radius) {
                    goingUp = true;
                }
            }

            if (rotateOnAxis) {
                anguloY += 2; // Rotate the sphere on its axis
            }
    
            // Ajustar la coordenada Z de rotatingSphere para que pase por delante y detrás del cilindro
            if (rotating) {
                center[2] = (parent.getHeight() / 2) - Math.sin(Math.toRadians(anguloY)) * (parent.getWidth() / 2); // Ajustar la profundidad
            }
    
            parent.repaint();
    
            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                //Logger.getLogger(Esfera3D.class.getName()).log(Level.SEVERE, null, ex);
            }

            parent.repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                //Logger.getLogger(Esfera3D.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void paintSphere(Graficos3D buffer) {
        int steps = 50;
        for (int i = 0; i < steps; i++) {
            for (int j = 0; j < steps; j++) {
                double theta = 2 * Math.PI * i / steps;
                double phi = Math.PI * j / steps;
                double x = radius * Math.sin(phi) * Math.cos(theta) + center[0];
                double y = radius * Math.sin(phi) * Math.sin(theta) + center[1];
                double z = radius * Math.cos(phi) + center[2];
                double[] rotated = rotating ? rotarY(new double[]{x, y, z}, anguloY) : new double[]{x, y, z};
                if (rotateOnAxis) {
                    rotated = rotarY(new double[]{x, y, z}, anguloY);
                }
                Point2D p = parent.punto3D_a_2D(rotated[0], rotated[1], rotated[2]);
                buffer.putPixel((int) p.getX(), (int) p.getY(), rotated[2], Color.RED);
            }
        }
    }
    

    private double[] rotarY(double[] point, double angle) {
        double[] result = new double[3];
        result[0] = point[0] * Math.cos(Math.toRadians(angle)) + point[2] * Math.sin(Math.toRadians(angle));
        result[1] = point[1];
        result[2] = -point[0] * Math.sin(Math.toRadians(angle)) + point[2] * Math.cos(Math.toRadians(angle));
        return result;
    }

    
}
