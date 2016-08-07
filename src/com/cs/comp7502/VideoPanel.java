package com.cs.comp7502;

import com.cs.comp7502.classifier.CascadedClassifier;
import com.cs.comp7502.utils.ImageUtils;
import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.List;

/**
 * Created by Mohamed Jameel on 6/8/2016.
 */
public class VideoPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private BufferedImage image;

    public VideoPanel() {
        super();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.image==null) return;
        g.drawImage(this.image,10,10,2*this.image.getWidth(),2*this.image.getHeight(), null);
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void convertToBufferedImage(Mat mat){

        image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        mat.get(0, 0, data);
    }

    private void drawRectangles(List<Rectangle> rectangles) {
        if (rectangles != null) {
            Graphics2D drawing = image.createGraphics();
            drawing.setColor(Color.GREEN);
            float thickness = 1f;
            drawing.setStroke(new BasicStroke(thickness));
            for (Rectangle r : rectangles) {
                drawing.drawRect(r.x, r.y, r.height, r.width);
            }
        }
    }

    public void detectFaces(Detector detector, CascadedClassifier cascadedClassifier) {
        int[][] img = ImageUtils.buildImageArray(image, false);
        List<Rectangle> rectangles = detector.detectFaces(img, cascadedClassifier);
        drawRectangles(rectangles);
    }
}
