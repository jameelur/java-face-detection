package com.cs.comp7502.parser;

import com.cs.comp7502.classifier.CascadingClassifier;

public interface CascadingParser {

    public CascadingClassifier parse(String path);
}
