import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class cilindro2 extends JPanel implements KeyListener, Runnable {

    private Graficos3D buffer = new Graficos3D(800, 800);

    private double anguloMaximo = 2 * Math.PI;
    private int numPuntos = 50;
    private double anguloIncremento = anguloMaximo / numPuntos;
    private double escala = 50;

    private boolean animacionActiva = false;

    private ArrayList<double[]> vertices = new ArrayList<>();
    private double[] puntoCubo = {400, 200, 100};
    private double[] puntoFuga = {400, 400, 1000};

    private double faseOnda = 0;
    private Esfera3D rotatingSphere;
    private Esfera3D movingSphere;

    public cilindro2() {
        JFrame frame = new JFrame();
        frame.setSize(800, 800);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.addKeyListener(this);
        frame.setVisible(true);

        generarVertices();

        rotatingSphere = new Esfera3D(25, this, true, true); // Rotating sphere with radius 25
        movingSphere = new Esfera3D(25, this, false, false); // Moving sphere with radius 25 and rotating on axis
    }

    private void generarVertices() {
        vertices.clear();
        for (double alpha = 0; alpha < anguloMaximo; alpha += anguloIncremento) {
            for (double beta = 0; beta < anguloMaximo; beta += anguloIncremento) {
                double[] vertice = new double[3];
                double radio = 2 + Math.cos(alpha + faseOnda); // Añadir la faseOnda aquí
                vertice[0] = radio * Math.cos(beta);
                vertice[2] = radio * Math.sin(beta); // Intercambiamos Z e Y
                vertice[1] = alpha; // Usamos Y para el ángulo alpha
                vertices.add(vertice);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    
        double[][] verticesTrasladados = new double[vertices.size()][3];
        for (int i = 0; i < vertices.size(); i++) {
            double[] vertice = vertices.get(i);
            verticesTrasladados[i] = vertice;
        }
    
        for (int i = 0; i < vertices.size(); i++) {
            double[] v = verticesTrasladados[i];
            double[] trasladado = {
                (v[0] * escala) + puntoCubo[0],
                (v[1] * escala) + puntoCubo[1],
                (v[2] * escala) + puntoCubo[2]
            };
            verticesTrasladados[i] = trasladado;
        }
    
        // Dibujar la esfera que se mueve primero
        movingSphere.paintSphere(buffer);
    
        // Dibujar el cilindro
        for (int i = 0; i < numPuntos - 1; i++) {
            for (int j = 0; j < numPuntos; j++) {
                int index0 = i * numPuntos + j;
                int index1 = (i + 1) * numPuntos + j;
                int index2 = i * numPuntos + (j + 1) % numPuntos; 
                int index3 = (i + 1) * numPuntos + (j + 1) % numPuntos;
    
                double[] v0 = verticesTrasladados[index0];
                double[] v1 = verticesTrasladados[index1];
                double[] v2 = verticesTrasladados[index2];
                double[] v3 = verticesTrasladados[index3];
    
                Point2D p0 = punto3D_a_2D(v0[0], v0[1], v0[2]);
                Point2D p1 = punto3D_a_2D(v1[0], v1[1], v1[2]);
                Point2D p2 = punto3D_a_2D(v2[0], v2[1], v2[2]);
                Point2D p3 = punto3D_a_2D(v3[0], v3[1], v3[2]);
    
                buffer.drawLine((int) p0.getX(), (int) p0.getY(), (int) p1.getX(), (int) p1.getY(), v0[2], v1[2], Color.BLACK);
                buffer.drawLine((int) p0.getX(), (int) p0.getY(), (int) p2.getX(), (int) p2.getY(), v0[2], v2[2], Color.BLACK);
                buffer.drawLine((int) p1.getX(), (int) p1.getY(), (int) p3.getX(), (int) p3.getY(), v1[2], v3[2], Color.BLACK);
                buffer.drawLine((int) p2.getX(), (int) p2.getY(), (int) p3.getX(), (int) p3.getY(), v2[2], v3[2], Color.BLACK);
            }
        }
    
        // Dibujar la esfera que rota después del cilindro
        rotatingSphere.paintSphere(buffer);
    
        g.drawImage(buffer.getBuffer(), 0, 0, null);
        buffer.resetBuffer();
    }
    
    public Point2D punto3D_a_2D(double x, double y, double z) {
        double u = -puntoFuga[2] / (z - puntoFuga[2]);
    
        double px = puntoFuga[0] + (x - puntoFuga[0]) * u;
        double py = puntoFuga[1] + (y - puntoFuga[1]) * u;
    
        return new Point2D.Double(px, py);
    }
    



    public static void main(String[] args) {
        SwingUtilities.invokeLater(cilindro2::new);
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int key = ke.getKeyCode();

        switch (key) {
            case KeyEvent.VK_SPACE:
                animacionActiva = !animacionActiva;
                if (animacionActiva) {
                    new Thread(this).start();
                    rotatingSphere.startAnimation();
                    movingSphere.startAnimation();
                } else {
                    rotatingSphere.stopAnimation();
                    movingSphere.stopAnimation();
                }
                break;
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent ke) {}

    @Override
    public void keyTyped(KeyEvent ke) {}

    @Override
    public void run() {
        while (animacionActiva) {
            faseOnda += 0.1; // Incrementar la fase de la onda para crear el efecto de expansión y contracción
            generarVertices(); // Regenerar los vértices con la nueva fase de la onda
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                //Logger.getLogger(cilindro2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
