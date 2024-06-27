package com.example.omega.service.util;

import com.example.omega.service.util.annotations.TruncateString;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class StringNormalizationDeserializer extends StdDeserializer<String> implements ContextualDeserializer {

    private Integer start;
    private Integer end;

    public StringNormalizationDeserializer() {
        super(String.class);
    }

    public StringNormalizationDeserializer(String start, String end) {
        super(String.class);
        this.start = StringUtils.isBlank(start) ? null : Integer.parseInt(start);
        this.end = StringUtils.isBlank(end) ? null : Integer.parseInt(end);
    }

    /**
     * @param stringToParse          Parsed used for reading JSON content.
     * @param deserializationContext Context that can be used to access information about  this deserialization activity.
     * @return check if the value is Blank if yes, then saves null, if no it trims for all types of spaces and saves the value.
     * @throws IOException on getting string value from JsonParser.
     */
    @Override
    public String deserialize(JsonParser stringToParse, DeserializationContext deserializationContext) throws IOException {
        var parsedValue = StringUtils.isNotBlank(stringToParse.getValueAsString()) ?
                stringToParse.getValueAsString().trim() : stringToParse.getValueAsString();

        if (StringUtils.isBlank(parsedValue)) {
            return null;
        }

        return start != null && end != null ? StringUtils.substring(parsedValue, start, end) : parsedValue;
    }

    /**
     * Create contextual JsonDeserializer.
     *
     * @param deserializationContext the context.
     * @param property               the object property of the class. If it has the annotation type, create the corresponding instance.
     * @return returning a new instance, so that different properties will not share the same deserializer instance.
     */
    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext deserializationContext, BeanProperty property) {
        var stringTruncateAnnotation = property.getAnnotation(TruncateString.class);
        if (stringTruncateAnnotation != null) {
            var start = stringTruncateAnnotation.start();
            var end = stringTruncateAnnotation.end();
            return new StringNormalizationDeserializer(start, end);
        }
        return new StringNormalizationDeserializer();
    }
}
