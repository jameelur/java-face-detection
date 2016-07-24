package parser;

import data.CascadingClassifier;

public class OpenCVParser implements CascadingParser {
    @Override
    public CascadingClassifier parse(String path) {
        CascadingClassifier classifier = new CascadingClassifier();
        return classifier;
    }
}
