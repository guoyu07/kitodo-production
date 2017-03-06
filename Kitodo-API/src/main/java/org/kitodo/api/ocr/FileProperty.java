/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the GPL3-License.txt file that was
 * distributed with this source code.
 */

package org.kitodo.api.ocr;

import java.nio.file.Path;

public class FileProperty {

    /** The input file path. */
    private Path inputFilePath;

    /** The name of the generated file. */
    private String outputFileName;

    /**
     * Gets the inputFilePath.
     * @return The inputFilePath.
     */
    public Path getInputFilePath() {
        return inputFilePath;
    }

    /** Sets the inputFilePath.
     * @param inputFilePath The inputFilePath.
     */
    public void setInputFilePath(Path inputFilePath) {
        this.inputFilePath = inputFilePath;
    }

    /**
     * Gets the outputFileName.
     * @return The outputFileName.
     */
    public String getOutputFileName() {
        return outputFileName;
    }

    /** Sets the outputFileName.
     * @param outputFileName The outputFileName.
     */
    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }
}
