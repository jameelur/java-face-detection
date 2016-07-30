package com.cs.comp7502.parser;

import com.cs.comp7502.classifier.CascadedClassifier;

public interface CascadingParser {

    public CascadedClassifier parse(String path);
}
