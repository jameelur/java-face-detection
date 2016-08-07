package com.cs.comp7502.classifier;

import org.json.JSONObject;
/**
 * Created by jinyiwu on 31/7/2016.
 */
public interface JSONRW {
    JSONObject encode();
    void decode(JSONObject json);
}
