package com.cs.comp7502;

import com.cs.comp7502.classifier.CascadedClassifier;
import com.cs.comp7502.classifier.Feature;
import com.cs.comp7502.classifier.Stage;
import com.cs.comp7502.training.Adaboost;
import com.cs.comp7502.training.WHaarClassifier;
import com.cs.comp7502.training.Trainer;
import com.cs.comp7502.utils.ImageUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
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

import static org.opencv.imgproc.Imgproc.INTER_CUBIC;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CV_CAP_PROP_FRAME_WIDTH;

@SuppressWarnings("serial")
public class MainUI extends JFrame {

    private static final String DEFAULT_FILE_LOCATION = "./cascadedClassifiers/cascade_classifier_default.json";
    private JPopupMenu viewportPopup;
    private JLabel infoLabel = new JLabel("");

    private static CascadedClassifier cascadedClassifier;

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
            detector = new Detector();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new MainUI();
    }

    private static void initClassifiers() {
        List<Feature> featureList = Feature.generateAllFeatures();

        // retrieve list of all face and non face files for training
        File faceFolder = new File("res/trainingSet/faces");
        File nonfaceFolder = new File("res/trainingSet/nonFaces");

        File[] faceFiles = faceFolder.listFiles();
        File[] nonfaceFiles = nonfaceFolder.listFiles();

        List<Feature> trainingFeatures = featureList.subList(0, 999);

        // execute
        long time = System.currentTimeMillis();
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

            viewportPopup.addSeparator();

            JMenuItem trainFaceMenuItem = new JMenuItem("Train base features");
            trainFaceMenuItem.addActionListener(this);
            trainFaceMenuItem.setActionCommand("trainFace1");
            viewportPopup.add(trainFaceMenuItem);

            JMenuItem detectFaceMenuItem = new JMenuItem("Detect Face using base features");
            detectFaceMenuItem.addActionListener(this);
            detectFaceMenuItem.setActionCommand("drawRect");
            viewportPopup.add(detectFaceMenuItem);

            viewportPopup.addSeparator();

            JMenuItem trainFaceMenuItem2 = new JMenuItem("Train one stage");
            trainFaceMenuItem2.addActionListener(this);
            trainFaceMenuItem2.setActionCommand("trainFace2");
            viewportPopup.add(trainFaceMenuItem2);

            JMenuItem detectFaceMenuItem2 = new JMenuItem("Detect Face using one stage");
            detectFaceMenuItem2.addActionListener(this);
            detectFaceMenuItem2.setActionCommand("drawRect2");
            viewportPopup.add(detectFaceMenuItem2);

            viewportPopup.addSeparator();

            JMenuItem trainFaceMenuItem3 = new JMenuItem("Import existing trained classifier json");
            trainFaceMenuItem3.addActionListener(this);
            trainFaceMenuItem3.setActionCommand("trainFace3");
            viewportPopup.add(trainFaceMenuItem3);


            JMenuItem detectFaceMenuItem3 = new JMenuItem("Detect Face using cascaded classifier");
            detectFaceMenuItem3.addActionListener(this);
            detectFaceMenuItem3.setActionCommand("drawRect3");
            viewportPopup.add(detectFaceMenuItem3);

            JMenuItem detectFaceMenuItem4 = new JMenuItem("Detect Face using cascaded classifier (from webcam)");
            detectFaceMenuItem4.addActionListener(this);
            detectFaceMenuItem4.setActionCommand("drawRect4");
            viewportPopup.add(detectFaceMenuItem4);

            viewportPopup.addSeparator();

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

                        double seconds = (System.nanoTime() - start) / 1000000000.0;
                        infoLabel.setText(seconds+"");
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            } else if (e.getActionCommand().equals("drawIntegral")){
                if (originalImg == null) {
                    JOptionPane.showMessageDialog(this, "Load an image first");
                    return;
                }
                img = deepClone(originalImg);
                if (img!=null) {
                    JFrame frame = new JFrame();
                    JLabel jl = new JLabel();
                    frame.setTitle("Integral Image");

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



            } else if (e.getActionCommand().equals("trainFace1")) {

                long startTime = System.currentTimeMillis();

                weakHaarClassifiers = Trainer.trainFaces();

                long endTime   = System.currentTimeMillis();
                long timeTook = endTime - startTime;
                System.out.println(timeTook);
                JOptionPane.showMessageDialog(this, "Time taken to train faces " + (timeTook/1000.0) + "s");

            }else if (e.getActionCommand().equals("trainFace2")) {

                long startTime = System.currentTimeMillis();

                initClassifiers();

                long endTime   = System.currentTimeMillis();
                long timeTook = endTime - startTime;
                System.out.println(timeTook);
                JOptionPane.showMessageDialog(this, "Time taken to train 1 stage: " + (timeTook/1000.0) + "s");

            }else if (e.getActionCommand().equals("trainFace3")) {
                final JFileChooser fc = new JFileChooser();
                FileFilter fileFilter = new FileNameExtensionFilter("JSON Files","json");
                fc.addChoosableFileFilter(fileFilter);
                fc.setDragEnabled(true);
                fc.setMultiSelectionEnabled(false);
                int result =  fc.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        System.out.println("loading json from path: " + file.getAbsolutePath());

                        long startTime = System.currentTimeMillis();

                        JSONObject jsonObject = new JSONObject(readFromFile(file));

                        cascadedClassifier = new CascadedClassifier();
                        cascadedClassifier.decode(jsonObject);

                        long endTime   = System.currentTimeMillis();
                        long timeTaken = endTime - startTime;
                        System.out.println(timeTaken);
                        JOptionPane.showMessageDialog(this, "Time taken to parse json: " + (timeTaken) + "ms");
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }



            }
            else if (e.getActionCommand().equals("drawRect")){
                if (originalImg == null) {
                    JOptionPane.showMessageDialog(this, "Load an image first");
                    return;
                }
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
                long time = System.currentTimeMillis();
                rectangles = detector.detectFaces(image, weakHaarClassifiers, finalThreshold, similarityThreshold);
                long doneTime = (System.currentTimeMillis() - time) / 60000;
                System.out.println("time: " + doneTime + " mins, ft: " + finalThreshold +", st: " +similarityThreshold + ", # of faces detected: " + rectangles.size());
                JOptionPane.showMessageDialog(this, "time: " + doneTime + " mins, ft: " + finalThreshold +", st: " +similarityThreshold + ", # of faces detected: " + rectangles.size());

                drawRectangles();
            } else if (e.getActionCommand().equals("drawRect2")){
                if (originalImg == null) {
                    JOptionPane.showMessageDialog(this, "Load an image first");
                    return;
                }
                img = deepClone(originalImg);
                double stageThreshold = 0.6;

                boolean notOk = true;
                while (notOk) {
                    String s = JOptionPane.showInputDialog(this, "Please enter stage threshold! \n(Must be <10000 and positive)", ""+stage.getStageThreshold());
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
                JOptionPane.showMessageDialog(this, "time: " + doneTime + " mins, st: " + stageThreshold + ", # of faces detected: " + rectangles.size());
                drawRectangles();
            } else if (e.getActionCommand().equals("drawRect3")){
                if (originalImg == null) {
                    JOptionPane.showMessageDialog(this, "Load an image first");
                    return;
                }
                img = deepClone(originalImg);

                if (cascadedClassifier == null) loadDefaultCascadedClassifier();

                //too scared to run or test this bit
                long time = System.currentTimeMillis();

                rectangles = detector.detectFaces(image, cascadedClassifier);
                double doneTime = (System.currentTimeMillis() - time) / 60000.0;
                System.out.println("time: " + doneTime + " mins, # of faces detected: " + rectangles.size());
                JOptionPane.showMessageDialog(this, "time: " + doneTime + " mins, # of faces detected: " + rectangles.size());
                drawRectangles();
            } else if (e.getActionCommand().equals("drawRect4")){

                // check if the cascaded classifier is set, else import the default one,
                new Thread(startWebCam).start();
            } else if (e.getActionCommand().equals("exit")) {
                System.exit(0);
            }
            viewportPopup = null;
            this.updateUI();
        }

        private void drawRectangles() {
            if (rectangles!=null) {
                Graphics2D drawing = img.createGraphics();
                drawing.setColor(Color.GREEN);
                float thickness = 1f;
                drawing.setStroke(new BasicStroke(thickness));
                for (Rectangle r : rectangles) {
                    drawing.drawRect(r.x, r.y, r.height, r.width);
                }
            }
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

    private static String readFromFile(File file) {
        String result = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
            result = stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private Runnable startWebCam = new Runnable() {
        @Override
        public void run() {
            // check if the cascaded classifier is set, else import the default one,
            if (cascadedClassifier == null) {
                loadDefaultCascadedClassifier();
            }

            System.loadLibrary( Core.NATIVE_LIBRARY_NAME );

            JFrame frame = new JFrame();
            frame.setTitle("WebCam");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(290,150);
            VideoPanel videoPanel = new VideoPanel();
            frame.setContentPane(videoPanel);
            frame.setVisible(true);
            Mat webImage = new Mat();
            VideoCapture vidCapture = new VideoCapture(-1);
            if (vidCapture.isOpened()) {
                vidCapture.set(CV_CAP_PROP_FRAME_WIDTH, 480);
                vidCapture.set(CV_CAP_PROP_FRAME_HEIGHT, 300);
                while (true) {
                    vidCapture.read(webImage);
                    if (!webImage.empty()) {
                        long time = System.currentTimeMillis();
                        Imgproc.resize(webImage, webImage, new Size(480, 300), 0, 0, INTER_CUBIC);
                        Core.flip(webImage, webImage, 1);
                        frame.setSize(2 * webImage.width() + 20, 2 * webImage.height() + 40);

                        videoPanel.convertToBufferedImage(webImage);
                        videoPanel.detectFaces(detector, cascadedClassifier);
                        frame.repaint();
                        System.out.println("fps: " + 1000 / (System.currentTimeMillis() - time));
                    } else {
                        // error message to user here
                        System.out.println("No frame received from web cam");
                        break;
                    }

                }
            }
        }
    };

    private void loadDefaultCascadedClassifier() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(readFromFile(new File(DEFAULT_FILE_LOCATION)));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        cascadedClassifier = new CascadedClassifier();
        cascadedClassifier.decode(jsonObject);
    }
}

