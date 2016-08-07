# java-face-detection
An implementation in java of the viola jones algorithm using a trained classifier

## Pre-requisites
This requires 
* OpenCV 3.1, [Download](http://opencv.org/)
    * A compiled mac jar is provided here `lib/openCV/mac/opencv-310.jar` and the native binaries for it here `lib/openCV/mac/native/`
    * You need to configure openCV 3.1 and add it to to the project class path. Configuration steps can be found [here](http://www.rmnd.net/install-and-use-opencv-3-0-on-mac-os-x-with-eclipse-java/)
* Java
* Using an IDE is recommended due to the complexity of configuring openCV
    * IntelliJ IDEA, [Download](https://www.jetbrains.com/idea/)

There are two parts to the face detection
## Training Classifier 
this can be done by
* Calling CascadeClassifier.train() test. This implementation does not yeild good results
* __PREFERRED__ Using the python trainer [here](https://github.com/XanthosisJYW/ViolaJonesCascadeClassifier)  (same creators)

## Face Selection
* The [python](https://github.com/XanthosisJYW/ViolaJonesCascadeClassifier) face detection on single image
* __PREFERRED__ Using java MainUI.main() which gives you the following functionalities

### Feature 1
Calculate Integral Image and display it

### Feature 2 - Using base features
* Create 100 feature vectors from 100 images stored in `res/baseFeaturesTrainingSet/faces`.
* Use cosine similarity to compare each test image feature vector __Tx__ to input image feature vector __I__
* Use similarity threshold _sT_ to determine if that comparison yields a face.
* Find the average of `faces / Sum(faces+nonFaces)` and use the final threshold _ft_ to determine if the input image is a face

### Feature 3 - Using adaboosted 1 stage classifier
* Train a one stage classifier using the face images in `res/trainingSet/faces` and the non face images in `res/trainingSet/nonFaces` and the __1000__ Haar features (of the > 160k haar features in a 24x24 window)
* Use that one stage classifier to determine whether the input image is a face or non face

### Feature 4 - Using a cascaded classifier
* Import your own cascaded classifier. You can train the classifier as explained in __Training Classifier__ section above.
* If you don't import a cascaded classifier, the default one will be used. The default is the best classifier we were able to train so far.
 
#### Feature 4.1 - On loaded image
* You can detect faces on the loaded image

#### Feature 4.2 - Directly from webcam
* You can directly use your webcam to detect faces
