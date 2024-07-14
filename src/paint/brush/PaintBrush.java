/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint.brush;


 import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

    // Main class
 public class PaintBrush  extends JFrame {

   private DrawPanel drawPanel;
    private JButton clearButton, undoButton;
    private JRadioButton lineButton, rectangleButton, ovalButton, pencilButton, eraserButton;
    private JCheckBox solidCheckBox, dottedCheckBox;
    private Color currentColor = Color.BLACK;

    public PaintBrush() {
        setTitle("Paint Brush");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        drawPanel = new DrawPanel();
        add(drawPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        clearButton = new JButton("Clear");
        undoButton = new JButton("Undo");

        clearButton.addActionListener(e -> drawPanel.clear());
        undoButton.addActionListener(e -> drawPanel.undo());

        controlPanel.add(clearButton);
        controlPanel.add(undoButton);

        lineButton = new JRadioButton("Line");
        rectangleButton = new JRadioButton("Rectangle");
        ovalButton = new JRadioButton("Oval");
        pencilButton = new JRadioButton("Pencil");
        eraserButton = new JRadioButton("Eraser");

        ButtonGroup group = new ButtonGroup();
        group.add(lineButton);
        group.add(rectangleButton);
        group.add(ovalButton);
        group.add(pencilButton);
        group.add(eraserButton);

        controlPanel.add(lineButton);
        controlPanel.add(rectangleButton);
        controlPanel.add(ovalButton);
        controlPanel.add(pencilButton);
        controlPanel.add(eraserButton);

        solidCheckBox = new JCheckBox("Solid");
        dottedCheckBox = new JCheckBox("Dotted");

        controlPanel.add(solidCheckBox);
        controlPanel.add(dottedCheckBox);

        JButton colorButton = new JButton("Color");
        colorButton.addActionListener(e -> {
            currentColor = JColorChooser.showDialog(null, "Choose a color", currentColor);
            drawPanel.setCurrentColor(currentColor);
        });

        controlPanel.add(colorButton);

        add(controlPanel, BorderLayout.NORTH);

        drawPanel.setCurrentShapeType("Line");
        lineButton.addActionListener(e -> drawPanel.setCurrentShapeType("Line"));
        rectangleButton.addActionListener(e -> drawPanel.setCurrentShapeType("Rectangle"));
        ovalButton.addActionListener(e -> drawPanel.setCurrentShapeType("Oval"));
        pencilButton.addActionListener(e -> drawPanel.setCurrentShapeType("Pencil"));
        eraserButton.addActionListener(e -> drawPanel.setCurrentShapeType("Eraser"));

        solidCheckBox.addActionListener(e -> drawPanel.setSolid(solidCheckBox.isSelected()));

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PaintBrush::new);
    }
}

class DrawPanel extends JPanel {

    private ArrayList<Shape> shapes = new ArrayList<>();
    private Shape currentShape = null;
    private String currentShapeType = "Line";
    private Color currentColor = Color.BLACK;
    private boolean isSolid = false;

    public DrawPanel() {
        setBackground(Color.WHITE);
        MouseHandler mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setCurrentShapeType(String type) {
        this.currentShapeType = type;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public void setSolid(boolean solid) {
        this.isSolid = solid;
    }

    public void clear() {
        shapes.clear();
        repaint();
    }

    public void undo() {
        if (!shapes.isEmpty()) {
            shapes.remove(shapes.size() - 1);
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Shape shape : shapes) {
            shape.draw(g);
        }
        if (currentShape != null) {
            currentShape.draw(g);
        }
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            switch (currentShapeType) {
                case "Line":
                    currentShape = new Line(currentColor, e.getPoint(), isSolid);
                    break;
                case "Rectangle":
                    currentShape = new Rectangle(currentColor, e.getPoint(), isSolid);
                    break;
                case "Oval":
                    currentShape = new Oval(currentColor, e.getPoint(), isSolid);
                    break;
                case "Pencil":
                    currentShape = new Pencil(currentColor, e.getPoint(), isSolid);
                    break;
                case "Eraser":
                    currentShape = new Eraser(currentColor, e.getPoint(), isSolid);
                    break;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (currentShape != null) {
                currentShape.addPoint(e.getPoint());
                shapes.add(currentShape);
                currentShape = null;
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (currentShape != null) {
                currentShape.addPoint(e.getPoint());
                repaint();
            }
        }
    }
}

abstract class Shape {
    protected Color color;
    protected ArrayList<Point> points;
    protected boolean isSolid;

    public Shape(Color color, Point start, boolean isSolid) {
        this.color = color;
        this.isSolid = isSolid;
        points = new ArrayList<>();
        points.add(start);
    }

    public void addPoint(Point point) {
        points.add(point);
    }

    public abstract void draw(Graphics g);
}

class Line extends Shape {
    public Line(Color color, Point start, boolean isSolid) {
        super(color, start, isSolid);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1));
        if (points.size() > 1) {
            Point p1 = points.get(0);
            Point p2 = points.get(points.size() - 1);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}

class Rectangle extends Shape {
    public Rectangle(Color color, Point start, boolean isSolid) {
        super(color, start, isSolid);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1));
        if (points.size() > 1) {
            Point p1 = points.get(0);
            Point p2 = points.get(points.size() - 1);
            int x = Math.min(p1.x, p2.x);
            int y = Math.min(p1.y, p2.y);
            int width = Math.abs(p1.x - p2.x);
            int height = Math.abs(p1.y - p2.y);
            if (isSolid) {
                g2.fillRect(x, y, width, height);
            } else {
                g2.drawRect(x, y, width, height);
            }
        }
    }
}

class Oval extends Shape {
    public Oval(Color color, Point start, boolean isSolid) {
        super(color, start, isSolid);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1));
        if (points.size() > 1) {
            Point p1 = points.get(0);
            Point p2 = points.get(points.size() - 1);
            int x = Math.min(p1.x, p2.x);
            int y = Math.min(p1.y, p2.y);
            int width = Math.abs(p1.x - p2.x);
            int height = Math.abs(p1.y - p2.y);
            if (isSolid) {
                g2.fillOval(x, y, width, height);
            } else {
                g2.drawOval(x, y, width, height);
            }
        }
    }
}

class Pencil extends Shape {
    public Pencil(Color color, Point start, boolean isSolid) {
        super(color, start, isSolid);
    }

    @Override
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1));
        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            g2.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
}

class Eraser extends Shape {
    public Eraser(Color color, Point start, boolean isSolid) {
        super(color, start, isSolid);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }}}

