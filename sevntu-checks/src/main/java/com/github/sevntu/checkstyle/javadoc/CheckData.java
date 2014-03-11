////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////
package com.github.sevntu.checkstyle.javadoc;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Tag;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * A class encapsulating a Check class.
 *
 * @author radsaggi
 */
public class CheckData {

    /**
     * Description of the encapsulated check.
     */
    private String description;

    /**
     * Package associated with the encapsulated check.
     */
    private String inPackage;

    /**
     * Properties associated with the encapsulated check.
     */
    private Property[] properties;

    /**
     * Examples for the encapsulated check.
     */
    private Example[] examples;

    public static final String AT_EXAMPLE_TAG = "@cs-example",
            PROPERTY_TAG = "cs-property";

    /**
     * Create a CheckData from classes.
     *
     * @param clazz The class to be parsed.
     */
    public CheckData(ClassDoc clazz) {
        description = clazz.commentText();
        inPackage = clazz.containingPackage().name();

        try {
            BufferedReader br = new BufferedReader(new StringReader(
                    clazz.getRawCommentText()));

            ArrayList<Example> examplesList = new ArrayList<Example>();

            String str, desc = null;
            StringBuilder sbr = null;
            while ((str = br.readLine()) != null) {
                if (str.startsWith(AT_EXAMPLE_TAG)) {
                    if (sbr != null) {
                        examplesList.add(new Example(desc, sbr.toString()));
                    }
                    sbr = new StringBuilder();
                    desc = str.substring(AT_EXAMPLE_TAG.length() + 1);
                } else {
                    if (sbr != null) {
                        sbr.append(str).append("\n");
                    }
                }
            }
            
            examples = examplesList.toArray(examples 
                    = new Example[examplesList.size()]);
        } catch (IOException e) {
            //will never occur!
        }

        ArrayList<Property> propertiesList = new ArrayList<Property>();
        for (FieldDoc f : clazz.fields()) {
            Tag[] tags = f.tags(PROPERTY_TAG);
            if (tags.length != 1) {
                continue;
            }

            propertiesList.add(new Property(tags[0].name(), tags[0].text(),
                    f.type().simpleTypeName(), f.constantValueExpression()));
        }
        properties = propertiesList.toArray(properties
                = new Property[propertiesList.size()]);
    }

    /**
     * Get the examples.
     *
     * @return the examples
     */
    public Example[] getExamples() {
        return examples;
    }

    /**
     * Get the example at specified index.
     *
     * @param index the index of example
     * @return the example at specified index
     */
    public Example getExamples(int index) {
        return this.examples[index];
    }

    /**
     * Get the properties.
     *
     * @return the properties
     */
    public Property[] getProperties() {
        return properties;
    }

    /**
     * Get the property at specified index.
     *
     * @param index the index of property
     * @return the property at specified index
     */
    public Property getProperties(int index) {
        return this.properties[index];
    }

    /**
     * Get the package of the check.
     *
     * @return the package
     */
    public String getPackage() {
        return inPackage;
    }

    /**
     * Get the description of the encapsulated Check.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Encapsulating class for the Check properties.
     */
    public static class Property {

        /**
         * The name of the property.
         */
        private final String name;

        /**
         * Some description for the property.
         */
        private final String description;

        /**
         * The property might just have some type.
         */
        private final String type;

        /**
         * The default value for the property.
         */
        private final String defaultValue;

        /**
         * Create a property.
         *
         * @param name the name
         * @param description the description
         * @param type the type
         * @param defaultValue the default value
         */
        public Property(String name, String description, String type,
                String defaultValue) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.defaultValue = defaultValue;
        }

        /**
         * Get the default value of the property.
         *
         * @return the default value
         */
        public String getDefaultValue() {
            return defaultValue;
        }

        /**
         * Get the type of the property.
         *
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * Get the description of the property.
         *
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Get the name of the property.
         *
         * @return the value of name
         */
        public String getName() {
            return name;
        }

    }

    /**
     * Encapsulating class for some examples of the Check or its properties.
     */
    public static class Example {

        /**
         * Some description.
         */
        private final String description;

        /**
         * Some sample code.
         */
        private final String code;

        /**
         * Create a new Example.
         *
         * @param description the description for the example
         * @param code sample code for the example
         */
        public Example(String description, String code) {
            this.description = description;
            this.code = code;
        }

        /**
         * Get the example code.
         *
         * @return the code
         */
        public String getCode() {
            return code;
        }

        /**
         * Get the example description.
         *
         * @return the description
         */
        public String getDescription() {
            return description;
        }

    }

}
