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

import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 *
 * @author radsaggi
 */
public class HtmlWriter extends PrintWriter {

    private String templateMain;

    private String templateContent;

    /**
     * Initializes PrintWriter with the FileWriter.
     *
     * @param file File Name to which the PrintWriter will do the Output.
     * @throws IOException Exception raised by the FileWriter is passed on to
     * next level.
     * @throws UnsupportedEncodingException Exception raised by the
     * OutputStreamWriter is passed on to next level.
     */
    public HtmlWriter(String file, File template1, File template2)
            throws IOException, UnsupportedEncodingException {
        this(file, "utf-8", template1, template2);
    }

    /**
     * Initializes PrintWriter with the FileWriter.
     *
     * @param file File Name to which the PrintWriter will do the Output.
     * @param encoding Encoding to be used for this file.
     * @throws IOException Exception raised by the FileWriter is passed on to
     * next level.
     * @throws UnsupportedEncodingException Exception raised by the
     * OutputStreamWriter is passed on to next level.
     */
    public HtmlWriter(String file, String encoding, File template1,
            File template2) throws IOException, UnsupportedEncodingException {
        this(null, file, encoding, template1, template2);
    }

    /**
     * Initializes PrintWriter with the FileWriter.
     *
     * @param path The directory path to be created for this file.
     * @param file File Name to which the PrintWriter will do the Output.
     * @param encoding Encoding to be used for this file.
     * @throws IOException Exception raised by the FileWriter is passed on to
     * next level.
     * @throws UnsupportedEncodingException Exception raised by the
     * OutputStreamWriter is passed on to next level.
     */
    public HtmlWriter(String path, String file, String encoding, File template1,
            File template2) throws IOException, UnsupportedEncodingException {
        super(createWriter(path, file, encoding));
        templateMain = loadTemplate(template1);
        templateContent = loadTemplate(template2);
    }

    /**
     * Create the directory path for the file to be generated, construct
     * FileOutputStream and OutputStreamWriter depending upon docencoding.
     *
     * @param path The directory path to be created for this file.
     * @param filename File Name to which the PrintWriter will do the Output.
     * @param encoding Encoding to be used for this file.
     * @throws IOException Exception raised by the FileWriter is passed on to
     * next level.
     * @throws UnsupportedEncodingException Exception raised by the
     * OutputStreamWriter is passed on to next level.
     * @return Writer Writer for the file getting generated.
     */
    private static Writer createWriter(String path, String filename,
            String encoding) throws IOException, UnsupportedEncodingException,
            DocletAbortException {
        FileOutputStream fos;
        if (path != null && !path.isEmpty()) {
            File dir = new File(path);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new IOException("Unable to create dir : "
                            + dir.getAbsolutePath());
                }
            }

            fos = new FileOutputStream(new File(dir, filename));
        } else {
            fos = new FileOutputStream(filename);
        }

        return (encoding != null) ? new OutputStreamWriter(fos, encoding)
                : new OutputStreamWriter(fos);
    }

    private static String loadTemplate(File template) throws IOException {
        StringBuilder sbr = new StringBuilder();

        
        
        return sbr.toString();
    }
}
