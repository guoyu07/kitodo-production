/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.schemaconverter;

import java.net.URI;

import org.kitodo.api.schemaconverter.SchemaConverterInterface;
import org.kitodo.api.schemaconverter.SchemaFormat;

/**
 * Schema converter.
 */
public class SchemaConverter implements SchemaConverterInterface {

    @Override
    public URI convert(URI inputFileUri, SchemaFormat baseFormat, SchemaFormat resultFormat) {
        if (resultFormat == SchemaFormat.DMS) {
            throw new RuntimeException("Not implemented yet!");
        } else if (resultFormat == SchemaFormat.DMSRUSDML) {
            throw new RuntimeException("Not implemented yet!");
        } else {
            throw new RuntimeException("Not implemented yet!");
        }
    }
}
