# java-face-detection
An implementation in java of the viola jones algorithm using a trained classifier

## Pre-requisites
This requires 
* Java
    * Set your Java JDK to 1.8
* Using an IDE is recommended due to the complexity of configuring openCV
    * IntelliJ IDEA, [Download](https://www.jetbrains.com/idea/)
* OpenCV 3.1, [Download](http://opencv.org/)
    * A compiled mac jar is provided here `lib/openCV/mac/opencv-310.jar`
    * You need to configure openCV 3.1 and add it to to the project class path.
    * Configuration steps for Mac OS X can be found [here](http://www.rmnd.net/install-and-use-opencv-3-0-on-mac-os-x-with-eclipse-java/)
        * After 'make' has successfully executed, navigate to that directory and locate the 'lib' folder
        * Find 'libopencv_java310.os' and change this file extension to '.dylib'
        * Add 'opencv-301.jar' to your build path.  This jar file can be found in lib/openCV/mac/ in this repo
        * Configure the module library to add the lib folder of the openCV you just compiled
* Java-json (included in repo)
    * locate 'java-json.jar' and add it to your build path

There are two parts to the face detection
## Training Classifier 
this can be done by
* Calling CascadeClassifier.train() test. This implementation does not yield good results
* __PREFERRED__ Using the python trainer [here](https://github.com/XanthosisJYW/ViolaJonesCascadeClassifier)  (same creators)

## Face Selection
* The [python](https://github.com/XanthosisJYW/ViolaJonesCascadeClassifier) face detection on single image
* __PREFERRED__ Using java MainUI.main() which gives you the following functionalities

### Integral image visualization
Calculate Integral Image and display it

### Method 1 - Using base features
* Create 100 feature vectors from 100 images stored in `res/baseFeaturesTrainingSet/faces`.
* Use cosine similarity to compare each test image feature vector __Tx__ to input image feature vector __I__
* Use similarity threshold _sT_ to determine if that comparison yields a face.
* Find the average of `faces / Sum(faces+nonFaces)` and use the final threshold _ft_ to determine if the input image is a face

### Method 2 - Using adaboosted 1 stage classifier
* Train a one stage classifier using the face images in `res/trainingSet/faces` and the non face images in `res/trainingSet/nonFaces` and the __1000__ Haar features (of the > 160k haar features in a 24x24 window)
* Use that one stage classifier to determine whether the input image is a face or non face

### Method 3 - Using a cascaded classifier
* Import your own cascaded classifier. You can train the classifier as explained in __Training Classifier__ section above.
* If you don't import a cascaded classifier, the default one will be used. The default is the best classifier we were able to train so far.
 
#### Method 4.1 - On loaded image
* You can detect faces on the loaded image

#### Method 4.2 - Directly from webcam
* You can directly use your webcam to detect faces
