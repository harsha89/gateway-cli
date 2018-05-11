/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.apimgt.gateway.codegen.cmd;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterDescription;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * {@code GatewayLauncherCmd} represents a micro gateway cli launcher command.
 *
 */
public interface GatewayLauncherCmd {

    void execute();

    String getName();

    void printLongDesc(StringBuilder out);

    void printUsage(StringBuilder out);

    void setParentCmdParser(JCommander parentCmdParser);

    void setSelfCmdParser(JCommander selfCmdParser);

    static void printCommandList(JCommander cmdParser, StringBuilder out) {
        int longestNameLen = 0;
        for (JCommander commander : cmdParser.getCommands().values()) {
            GatewayLauncherCmd cmd = (GatewayLauncherCmd) commander.getObjects().get(0);
            if (cmd.getName().equals(GatewayCliCommands.DEFAULT) || cmd.getName().equals(GatewayCliCommands.HELP)) {
                continue;
            }

            int length = cmd.getName().length() + 2;
            if (length > longestNameLen) {
                longestNameLen = length;
            }
        }

        for (JCommander commander : cmdParser.getCommands().values()) {
            GatewayLauncherCmd cmd = (GatewayLauncherCmd) commander.getObjects().get(0);
            if (cmd.getName().equals(GatewayCliCommands.DEFAULT) || cmd.getName().equals(GatewayCliCommands.HELP)) {
                continue;
            }

            String cmdName = cmd.getName();
            String cmdDesc = cmdParser.getCommandDescription(cmdName);

            int noOfSpaces = longestNameLen - (cmd.getName().length() + 2);
            char[] charArray = new char[noOfSpaces + 4];
            Arrays.fill(charArray, ' ');
            out.append("  ").append(cmdName).append(new String(charArray)).append(cmdDesc).append("\n");
        }
    }

    static void printFlags(List<ParameterDescription> paramDescs, StringBuilder out) {
        int longestNameLen = 0;
        int count = 0;
        for (ParameterDescription parameterDesc : paramDescs) {
            if (parameterDesc.getParameter().hidden()) {
                continue;
            }

            String names = parameterDesc.getNames();
            int length = names.length() + 2;
            if (length > longestNameLen) {
                longestNameLen = length;
            }
            count++;
        }

        if (count == 0) {
            return;
        }
        out.append("Flags:\n");
        for (ParameterDescription parameterDesc : paramDescs) {
            if (parameterDesc.getParameter().hidden()) {
                continue;
            }
            String names = parameterDesc.getNames();
            String desc = parameterDesc.getDescription();
            int noOfSpaces = longestNameLen - (names.length() + 2);
            char[] charArray = new char[noOfSpaces + 4];
            Arrays.fill(charArray, ' ');
            out.append("  ").append(names).append(new String(charArray)).append(desc).append("\n");
        }
    }

    static String getCommandUsageInfo(String commandName) {
        if (commandName == null) {
            throw GatewayCmdUtils.createUsageException("invalid command");
        }

        String fileName = "cli-help/cli-" + commandName + ".help";
        try {
            return GatewayCmdUtils.readFileAsString(fileName);
        } catch (IOException e) {
            throw GatewayCmdUtils.createUsageException("usage info not available for command: " + commandName);
        }
    }
}
