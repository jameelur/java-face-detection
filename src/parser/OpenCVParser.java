package parser;

import data.CascadingClassifier;
import data.Stage;
import data.WeakClassifier;
import data.Feature;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.*;

import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.*;

public class OpenCVParser implements CascadingParser {
    @Override
    public CascadingClassifier parse(String path) {
        CascadingClassifier classifier = new CascadingClassifier();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(path));
            doc.getDocumentElement().normalize();

            Element root = doc.getDocumentElement();
            NodeList layer1 = root.getChildNodes();
            Node frontalFace = layer1.item(1);

            NodeList layer2 = frontalFace.getChildNodes();

            Node size = layer2.item(1);
            System.out.println(size.getTextContent());
            String[] sizeComponents = size.getTextContent().split(" ");
            // int width = Integer.parseInt(sizeComponents[0]);
            // int height = Integer.parseInt(sizeComponents[1]);

            // set window size for classifier
            classifier.setWidth(Integer.parseInt(sizeComponents[0]));
            classifier.setHeight(Integer.parseInt(sizeComponents[1]));

            ArrayList<Stage> stageArrayList = new ArrayList<>();

            NodeList stages = layer2.item(3).getChildNodes();

            // stage loop
            for (int stageIndex = 1; stageIndex < stages.getLength(); stageIndex += 2) {
                Stage newStage = new Stage();
                ArrayList<WeakClassifier> classifierArrayList = new ArrayList<>();

                Node stage = stages.item(stageIndex);
                NodeList trees = stage.getChildNodes().item(3).getChildNodes();

                // weak classifier loop
                for (int treeIndex = 1; treeIndex < trees.getLength(); treeIndex += 2) {
                    WeakClassifier newWeakClassifier = new WeakClassifier();
                    ArrayList<Feature> featureArrayList = new ArrayList<>();

                    NodeList weakClassifier = trees.item(treeIndex).getChildNodes().item(3).getChildNodes();
                    NodeList rectangles = weakClassifier.item(3).getChildNodes().item(1).getChildNodes();

                    // feature rectangle loop
                    for (int rectangleIndex = 1; rectangleIndex < rectangles.getLength(); rectangleIndex += 2) {
                        Feature newFeature = new Feature();

                        String[] rectangleComponents = rectangles.item(rectangleIndex).getTextContent().split(" ");
                        // int xCoordinate = Integer.parseInt(rectangleComponents[0]);
                        // int yCoordinate = Integer.parseInt(rectangleComponents[1]);
                        // int xOffset = Integer.parseInt(rectangleComponents[2]);
                        // int yOffset = Integer.parseInt(rectangleComponents[3]);
                        // int weight = (int) Double.parseDouble(rectangleComponents[4]);

                        // set params for each feature rectangle
                        newFeature.setX(Integer.parseInt(rectangleComponents[0]));
                        newFeature.setY(Integer.parseInt(rectangleComponents[1]));
                        newFeature.setxOffset(Integer.parseInt(rectangleComponents[2]));
                        newFeature.setyOffset(Integer.parseInt(rectangleComponents[3]));
                        newFeature.setWeight(Double.parseDouble(rectangleComponents[4]));

                        // add new feature rectangle
                        featureArrayList.add(newFeature);
                    }

                    // set feature arraylist for weak classifier
                    newWeakClassifier.setFeatures(featureArrayList);

                    Node weakClassifierThreshold = weakClassifier.item(5);
                    // double classifierThreshold = Double.parseDouble(weakClassifierThreshold.getTextContent());

                    // set weight for weak classifier
                    newWeakClassifier.setWeight(Double.parseDouble(weakClassifierThreshold.getTextContent()));

                    Node leftValue = weakClassifier.item(7);
                    // double classifierLeftValue = Double.parseDouble(leftValue.getTextContent());

                    // set left value for weak classifier
                    newWeakClassifier.setLeft(Double.parseDouble(leftValue.getTextContent()));

                    Node rightValue = weakClassifier.item(9);
                    // double classifierRightValue = Double.parseDouble(rightValue.getTextContent());

                    // set right value for weak classifier
                    newWeakClassifier.setRight(Double.parseDouble(rightValue.getTextContent()));

                    // add new weak classifier
                    classifierArrayList.add(newWeakClassifier);
                }

                // set weak classifier arraylist for stage
                newStage.setClassifierList(classifierArrayList);

                Node stageThreshold = stage.getChildNodes().item(5).getFirstChild();
                // double stageThresholdValue = Double.parseDouble(stageThreshold.getTextContent());

                // set stage threshold for stage
                newStage.setStageThreshold(Double.parseDouble(stageThreshold.getTextContent()));

                // add new stage
                stageArrayList.add(newStage);
            }

            // set stage arraylist to classifier
            classifier.setStages(stageArrayList);
        } catch (Exception e) {
            // TODO: deal with exception
            e.printStackTrace();
        }

        return classifier;
    }
}
