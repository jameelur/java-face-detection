package com.cs.comp7502;

import com.cs.comp7502.classifier.CascadingClassifier;
import com.cs.comp7502.data.Feature;
import com.cs.comp7502.data.Stage;
import com.cs.comp7502.rnd.WHaarClassifier;
import com.cs.comp7502.rnd.Trainer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import static com.cs.comp7502.data.Feature.colCount;
import static com.cs.comp7502.data.Feature.rowCount;

@SuppressWarnings("serial")
public class MainUI extends JFrame {

    private JPopupMenu viewportPopup;
    private JLabel infoLabel = new JLabel("");

    private static CascadingClassifier openCVFrontalFace;
    private static CascadingClassifier openCVEyes;

    private static Map<String, List<WHaarClassifier>> weakHaarClassifiers;
    private static Stage stage;
    private static Detector detector;
    List<Rectangle> rectangles = new ArrayList<>();
    int[][] image;


    public MainUI() {
        super("COMP 7502 - Project");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JScrollPane scroller = new JScrollPane(new ImagePanel());
        this.add(scroller);
        this.add(infoLabel, BorderLayout.SOUTH);
        this.setSize(500, 500);
        this.setVisible(true);
    }

    public static void main(String args[]) {
        try {
            initClassifiers();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new MainUI();
    }

    private static void initClassifiers() {
//        openCVFrontalFace = new OpenCVParser().parse("file in assets?");

        detector = new Detector();

        // training for method 1
        weakHaarClassifiers = Trainer.trainFaces();

        // training for method 2
        // generate all features
        List<Feature> featureList = new ArrayList<>();
        for (int type = 1; type <= Feature.FEATURE_MAP.size(); type++) {
            int windowCountH = rowCount(type);
            int windowCountW = colCount(type);
            for (int height = 1; height <= (24 / windowCountH); height++) {
                for (int width = 1; width <= (24 / windowCountW); width++) {
                    for (int x = 0; x < 24 - (height * windowCountH - 1); x ++) {
                        for (int y = 0; y < 24 - (width * windowCountW - 1); y ++) {
                            featureList.add(new Feature(type, x, y, width, height));
                        }
                    }
                }
            }
        }

        // retrieve list of all face and non face files for training
        File faceFolder = new File("res/trainingSet/faces");
        File nonfaceFolder = new File("res/trainingSet/nonFaces");


        File[] faceFiles = faceFolder.listFiles();
        File[] nonfaceFiles = nonfaceFolder.listFiles();

        List<Feature> trainingFeatures = featureList.subList(0, 999);
        // prepare
//        Feature feature1 = new Feature(FEATURE_TYPE_1, 11, 6, 6, 2);
//
//
//        File test1 = new File("res/testImages/testImage1.png");
//        File test2 = new File("res/testImages/testImage2.png");
//
//        List<Feature> trainingFeatures = new ArrayList<>();
//        trainingFeatures.add(feature1);



        // execute
        long time = System.currentTimeMillis();
//        Stage stage = Adaboost.learn(trainingFeatures, new File[]{test1}, new File[]{test2});
        stage = Adaboost.learn(trainingFeatures, faceFiles, nonfaceFiles);
        System.out.println("Time taken to boost: " + ((System.currentTimeMillis() - time)/1000) + "s");
    }

    private class ImagePanel extends JPanel implements MouseListener, ActionListener, MouseMotionListener {
        private BufferedImage img;
        private BufferedImage originalImg;
        int row;
        int column;

        public ImagePanel() {
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
        }

        public Dimension getPreferredSize() {
            if (img != null) return (new Dimension(img.getWidth(), img.getHeight()));
            else return (new Dimension(0, 0));
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (img != null)
                g.drawImage(img, 0, 0, this);
        }

        private void showPopup(MouseEvent e) {
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);
            viewportPopup = new JPopupMenu();

            JMenuItem openImageMenuItem = new JMenuItem("load any image ...");
            openImageMenuItem.addActionListener(this);
            openImageMenuItem.setActionCommand("open image");
            viewportPopup.add(openImageMenuItem);

            viewportPopup.addSeparator();

            JMenuItem integralImageMenuItem = new JMenuItem("Calculate Integral Image");
            integralImageMenuItem.addActionListener(this);
            integralImageMenuItem.setActionCommand("drawIntegral");
            viewportPopup.add(integralImageMenuItem);

            JMenuItem detectFaceMenuItem = new JMenuItem("Detect Face using base features");
            detectFaceMenuItem.addActionListener(this);
            detectFaceMenuItem.setActionCommand("drawRect");
            viewportPopup.add(detectFaceMenuItem);

            JMenuItem detectFaceMenuItem2 = new JMenuItem("Detect Face using adaboosted classifier");
            detectFaceMenuItem2.addActionListener(this);
            detectFaceMenuItem2.setActionCommand("drawRect2");
            viewportPopup.add(detectFaceMenuItem2);

            JMenuItem exitMenuItem = new JMenuItem("exit");
            exitMenuItem.addActionListener(this);
            exitMenuItem.setActionCommand("exit");
            viewportPopup.add(exitMenuItem);

            viewportPopup.show(e.getComponent(), e.getX(), e.getY());
        }

        public void mouseClicked(MouseEvent e) {}
        public void mouseEntered(MouseEvent e) {}
        public void mouseExited(MouseEvent e) {}
        public void mouseReleased(MouseEvent e) {}

        public void mousePressed(MouseEvent e) {
            if (viewportPopup != null) {
                viewportPopup.setVisible(false);
                viewportPopup = null;
            } else
                showPopup(e);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("open image")) {
                final JFileChooser fc = new JFileChooser();
                FileFilter imageFilter = new FileNameExtensionFilter("Image files", "bmp", "gif", "jpg");
                fc.addChoosableFileFilter(imageFilter);
                fc.setDragEnabled(true);
                fc.setMultiSelectionEnabled(false);
                int result =  fc.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        System.out.println("loading image from path: " + file.getAbsolutePath());
                        long start = System.nanoTime();
                        img = ImageIO.read(file);
                        originalImg = img;

                        image = ImageUtils.buildImageArray(img, false);

                        // retrieve weak haar classifier
//                        List<WHaarClassifier> computedFeatures = Trainer.train(image);

                        // for each classifier perform a comparison
//                        SimilarityComputation.voting(null,new WeakHaarClassifier(computedFeatures), weakHaarClassifiers, 0.6);

                        double seconds = (System.nanoTime() - start) / 1000000000.0;
                        infoLabel.setText(seconds+"");
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            } else if (e.getActionCommand().equals("drawIntegral")){
                img = deepClone(originalImg);
                if (img!=null) {
                    JFrame frame = new JFrame();
                    JLabel jl = new JLabel();
                    frame.setTitle("Integral Image");

                    //TODO
                    image = ImageUtils.buildImageArray(img, true);
                    int h = image.length;
                    int w = image[0].length;
                    int[][] integralI = new int[h][w];
                    ImageUtils.buildIntegralImage(image, integralI, w, h);
                    BufferedImage bI = new BufferedImage(integralI[0].length, integralI.length, BufferedImage.TYPE_INT_RGB);
                    int max = integralI[integralI.length-1][integralI[0].length-1];

                    // setting 2D array into bufferedimage http://stackoverflow.com/questions/21576096/convert-a-2d-array-of-doubles-to-a-bufferedimage
                    for (int y = 0; y <integralI[0].length; y++) {
                        for (int x = 0; x <integralI.length; x++) {
                            integralI[x][y] = (int)((integralI[x][y] * 255.0/(max)));
                            int value = integralI[x][y] << 16 | integralI[x][y] << 8 | integralI[x][y];
                            bI.setRGB(y, x, value);
                        }
                    }
                    ImageIcon ii = new ImageIcon(bI);
                    jl.setIcon(ii);
                    frame.add(jl);
                    frame.pack();
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setResizable(false);
                    frame.setVisible(true);
                }



            } else if (e.getActionCommand().equals("drawRect")){

                img = deepClone(originalImg);
                double finalThreshold = 0.6;
                double similarityThreshold = 0.6;
                boolean notOk = true;
                while (notOk) {
                    String s = JOptionPane.showInputDialog(this, "Please enter final threshold! \n(Must be <1 and positive)", ""+finalThreshold);
                    if (s==null) {
                        return;
                    }
                    try {
                        double d = Double.parseDouble(s);
                        if (d>=0.0 && d<1.0) {
                            notOk = false;
                            finalThreshold = d;
                        }
                    } catch (Exception ee){
                    }
                }
                notOk = true;
                while (notOk) {
                    String s = JOptionPane.showInputDialog(this, "Please enter similarity threshold! \n(Must be <1 and positive)", ""+similarityThreshold);
                    if (s==null) {
                        return;
                    }
                    try {
                        double d = Double.parseDouble(s);
                        if (d>=0.0 && d<1.0) {
                            notOk = false;
                            similarityThreshold = d;
                        }
                    } catch (Exception ee){
                    }
                }
                //too scared to run or test this bit
                long time = System.currentTimeMillis();
                rectangles = (ArrayList<Rectangle>) detector.detectFaces(image, weakHaarClassifiers, finalThreshold, similarityThreshold);
                long doneTime = (System.currentTimeMillis() - time) / 60000;
                System.out.println("time: " + doneTime + " mins, ft: " + finalThreshold +", st: " +similarityThreshold + ", # of faces detected: " + rectangles.size());

                //for testing color and line thickness
//                Rectangle faceArea = new Rectangle(50, 50, 70, 70);
//                Rectangle faceArea2 = new Rectangle(235, 220, 110, 110);
//                rectangles.add(faceArea);
//                rectangles.add(faceArea2);

                if (rectangles!=null) {
                    Graphics2D drawing = img.createGraphics();
                    drawing.setColor(Color.GREEN);
                    float thickness = 3f;
                    drawing.setStroke(new BasicStroke(thickness));
                    for (Rectangle r : rectangles) {
                        drawing.drawRect(r.x, r.y, r.height, r.width);
                    }
                }
            } else if (e.getActionCommand().equals("drawRect2")){

                img = deepClone(originalImg);
                double stageThreshold = 0.6;

                boolean notOk = true;
                while (notOk) {
                    String s = JOptionPane.showInputDialog(this, "Please enter stage threshold! \n(Must be <1000 and positive)", ""+stage.getStageThreshold());
                    if (s==null) {
                        return;
                    }
                    try {
                        double d = Double.parseDouble(s);
                        if (d>=0.0 && d<10000.0) {
                            notOk = false;
                            stageThreshold = d;
                        }
                    } catch (Exception ee){
                    }
                }

                //too scared to run or test this bit
                long time = System.currentTimeMillis();
                stage.setStageThreshold(stageThreshold);
                rectangles = detector.detectFaces(image, stage);
                long doneTime = (System.currentTimeMillis() - time) / 60000;
                System.out.println("time: " + doneTime + " mins, st: " + stageThreshold + ", # of faces detected: " + rectangles.size());

                if (rectangles!=null) {
                    Graphics2D drawing = img.createGraphics();
                    drawing.setColor(Color.GREEN);
                    float thickness = 3f;
                    drawing.setStroke(new BasicStroke(thickness));
                    for (Rectangle r : rectangles) {
                        drawing.drawRect(r.x, r.y, r.height, r.width);
                    }
                }
            }
            else if (e.getActionCommand().equals("exit")) {
                System.exit(0);
            }
            viewportPopup = null;
            this.updateUI();
        }

        public void mouseDragged(MouseEvent e) {}

        public void mouseMoved(MouseEvent e) {
            column = e.getX();
            row = e.getY();
            infoLabel.setText("("+row+","+column+")");
        }

        public BufferedImage deepClone(BufferedImage bImage) {
            ColorModel cModel = bImage.getColorModel();
            WritableRaster raster = bImage.copyData(null);
            boolean isAlphaPremultiplied = cModel.isAlphaPremultiplied();
            return new BufferedImage(cModel, raster, isAlphaPremultiplied, null);
        }
    }
}

