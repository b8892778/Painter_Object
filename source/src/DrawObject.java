
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import javax.swing.JPanel;

class DrawObject extends JPanel {
    /*起點、終點、圖形起點、移動起點*/
    Point p1, p2, loc, lp;
    /*圖形寬高, 線條粗細*/
    int width, height, lineWidth;
    /*線條起點、終點*/
    int start, end;
    /*筆刷*/
    Stroke stroke;
    /*顏色*/
    Color color;
    /*圖形*/
    Shape shape;
    /*畫筆型態、狀態*/
    Status type, status;
    /*Page*/
    Page page;
    /*外框*/
    ResizeBorder rborder;
        
    /*設定形狀、類型、粗細、顏色*/
    DrawObject(Page page, Shape shape, Status type, Stroke stroke, Color color) {
        this.page = page;
        this.shape = shape;
        this.type = type;
        this.stroke = stroke; 
        this.color = color;
        this.lineWidth = page.lineWidth;
    }
    
    /*設定線條區段*/
    void setSection(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /*設定線條起點、終點*/
    void format(Point p1, Point p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    /*設定圖形起點、寬高、填滿、外框*/
    void format(Point loc, int width, int height) {
        this.loc = loc;
        this.width = width;
        this.height = height;
        this.status = Status.Selected;
        /*新增滑鼠事件、設定外框*/
        rborder = new ResizeBorder(this, Color.RED);
        this.setBorder(rborder);
        this.addMouseListener(new MyMouseAdapter());
        this.addMouseListener(rborder);
        this.addMouseMotionListener(new MyMouseAdapter());
        this.addMouseMotionListener(rborder);
        /*變成透明*/
        this.setOpaque(false);
        /*設定大小*/
        this.setSize(new Dimension(width + lineWidth, height + lineWidth));
        /*設定位置*/
        this.setLocation(loc.x - lineWidth / 2, loc.y - lineWidth / 2);
        /*建立圖形*/
        switch (type) {
            case Rectangle:
                shape = new Rectangle2D.Double(lineWidth / 2, lineWidth / 2, width, height);
                break;
            case Round_Rectangle:
                shape = new RoundRectangle2D.Double(lineWidth / 2, lineWidth / 2, width, height, 30, 30);
                break;
            case Oval:
                shape = new Ellipse2D.Double(lineWidth / 2, lineWidth / 2, width, height);
                break;
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.setStroke(stroke);
        g2d.draw(shape);
    }

    /*滑鼠監聽事件*/
    class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            lp = e.getPoint();
            /*如果目前物件是選擇狀態，就變成 Idle*/
            if (page.drawobject.status == Status.Selected) {
                page.drawobject.status = Status.Idle;
            }
            /*如果物件是閒置狀態，就變成 Selected*/
            if (DrawObject.this.status == Status.Idle) {
                DrawObject.this.status = Status.Selected;
                page.drawobject = DrawObject.this;
            }
            /*如果物件是選擇狀態並且畫筆類型為填滿則填滿*/
            if (DrawObject.this.status == Status.Selected && page.type == Status.Fill) {
                DrawObject.this.setOpaque(true);
                DrawObject.this.color = page.penColor;
                DrawObject.this.setBackground(page.penColor);
            }
            page.repaint();
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            if (status == Status.Selected) {
                /*計算移動中的 X Y*/
                int offset_x = DrawObject.this.getX() + e.getX() - lp.x;
                int offset_y = DrawObject.this.getY() + e.getY() - lp.y;
                /*重新設定圖形位置*/
                DrawObject.this.setLocation(offset_x, offset_y);
                page.repaint();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if (DrawObject.this.status != Status.Resize) {
                DrawObject.this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (DrawObject.this.status != Status.Resize) {
                DrawObject.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }
}
