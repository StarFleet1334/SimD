/*
 * (C) Copyright 2018 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     bdelbosc
 */
package com.demo.folder.sim;

import net.quux00.simplecsv.CsvParser;
import net.quux00.simplecsv.CsvParserBuilder;
import net.quux00.simplecsv.CsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * A CsvReader that skips assertion lines.
 *
 * @since 3.0
 */
public class SimulationReader extends CsvReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulationReader.class);
    private static final String ASSERTION = "assertion";

    public SimulationReader(Reader reader) throws IOException {
        this(reader, 0,
                new CsvParserBuilder().trimWhitespace(true).allowUnbalancedQuotes(true).separator('\t').build());
    }

    public SimulationReader(Reader reader, int line, CsvParser csvParser) {
        super(reader, line, csvParser);
    }

    @Override
    public List<String> readNext() throws IOException {
        try {
            List<String> ret = super.readNext();
            if (ret != null && !ret.isEmpty() && ASSERTION.equalsIgnoreCase(ret.get(0))) {
                LOGGER.debug("Skipping assertion line: {}", ret);
                return readNext();
            }
            return ret;
        } catch (IOException e) {
            LOGGER.error("Error reading CSV line", e);
            throw e;
        }
    }
}
