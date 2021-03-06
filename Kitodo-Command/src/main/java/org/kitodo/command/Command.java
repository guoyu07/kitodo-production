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

package org.kitodo.command;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.api.command.CommandInterface;
import org.kitodo.api.command.CommandResult;

public class Command implements CommandInterface {

    private static final Logger logger = LogManager.getLogger(Command.class);
    private static final String CHARSET = "UTF-8";

    /**
     * Method executes a script.
     *
     * @param id
     *            The id, to identify the command and it's results.
     * @param command
     *            The command as a String.
     * @return The command result.
     */
    @Override
    public CommandResult runCommand(Integer id, String command) {

        CommandResult commandResult = null;
        Process process = null;
        String[] callSequence = command.split("[\\r\\n\\s]+");

        try {
            process = new ProcessBuilder(callSequence).start();
            try (InputStream inputStream = process.getInputStream();
                    InputStream errorInputStream = process.getErrorStream()) {
                ArrayList<String> outputMessage = inputStreamArrayToList(inputStream);
                ArrayList<String> errorMessage = inputStreamArrayToList(errorInputStream);
                int errCode = process.waitFor();

                outputMessage.addAll(errorMessage);

                commandResult = new CommandResult(id, command, errCode == 0, outputMessage);
                if (!commandResult.isSuccessful()) {
                    logger.error("Execution of Command " + commandResult.getId() + " " + commandResult.getCommand()
                            + " failed!: " + commandResult.getMessages());
                } else {
                    logger.info("Execution of Command " + commandResult.getId() + " " + commandResult.getCommand()
                        + " was succesfull!: " + commandResult.getMessages());
                }
            }
        } catch (IOException | InterruptedException exception) {
            ArrayList<String> errorMessages = new ArrayList<>();
            errorMessages.add(exception.getCause().toString());
            errorMessages.add(exception.getMessage());
            commandResult = new CommandResult(id, command, false, errorMessages);
            logger.error("Execution of Command " + commandResult.getId() + " " + commandResult.getCommand()
                    + " failed!: " + commandResult.getMessages());
            return commandResult;
        }
        return commandResult;
    }

    /**
     * The method reads an InputStream and returns it as a ArrayList.
     *
     * @param inputStream
     *            The Stream to convert.
     * @return A ArrayList holding the single lines.
     */
    private static ArrayList<String> inputStreamArrayToList(InputStream inputStream) {
        ArrayList<String> result = new ArrayList<>();

        try (Scanner inputLines = new Scanner(inputStream, CHARSET)) {
            while (inputLines.hasNextLine()) {
                String myLine = inputLines.nextLine();
                result.add(myLine);
            }
        }
        return result;
    }
}
