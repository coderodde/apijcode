package com.coderodde.apij.graph.path;

import static com.coderodde.apij.util.Utils.checkNotNull;

public class SearchData {
    
    /**
     * Defines the type of the data.
     */
    private SearchDataType type;
    
    /**
     * Actual search data.
     */
    private Object data;
    
    SearchData(final SearchDataType type, final Object data) {
        checkNotNull(type, "'type' is null.");
        checkNotNull(data, "'data' is null.");
        this.type = type;
        this.data = data;
    }
    
    public SearchDataType getType() {
        return type;
    }
    
    public Object getData() {
        return data;
    }
}
