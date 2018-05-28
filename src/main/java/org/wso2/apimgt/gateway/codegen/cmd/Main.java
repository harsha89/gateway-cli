/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.apimgt.gateway.codegen.CodeGenerator;
import org.wso2.apimgt.gateway.codegen.ThrottlePolicyGenerator;
import org.wso2.apimgt.gateway.codegen.config.ConfigYAMLParser;
import org.wso2.apimgt.gateway.codegen.config.bean.Config;
import org.wso2.apimgt.gateway.codegen.exception.BallerinaServiceGenException;
import org.wso2.apimgt.gateway.codegen.exception.ConfigParserException;
import org.wso2.apimgt.gateway.codegen.exception.CliLauncherException;
import org.wso2.apimgt.gateway.codegen.service.APIService;
import org.wso2.apimgt.gateway.codegen.service.APIServiceImpl;
import org.wso2.apimgt.gateway.codegen.service.bean.API;
import org.wso2.apimgt.gateway.codegen.service.bean.policy.ApplicationThrottlePolicyDTO;
import org.wso2.apimgt.gateway.codegen.service.bean.policy.SubscriptionThrottlePolicyDTO;
import org.wso2.apimgt.gateway.codegen.token.TokenManagement;
import org.wso2.apimgt.gateway.codegen.token.TokenManagementImpl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class executes the gateway cli program.
 *
 */
public class Main {
    private static final String JC_UNKNOWN_OPTION_PREFIX = "Unknown option:";
    private static final String JC_EXPECTED_A_VALUE_AFTER_PARAMETER_PREFIX = "Expected a value after parameter";

    private static PrintStream outStream = System.err;

    private static final Logger cliLog = LoggerFactory.getLogger(Main.class);

    public static void main(String... args) {
        try {
           /* String ballerinaHome  = System.getProperty(GatewayCliConstants.BALLERINA_HOME);
            if (StringUtils.isEmpty(ballerinaHome)) {
                outStream.println("Please set BALLERINA_HOME variable to continue");
                Runtime.getRuntime().exit(1);
            }*/

            //String trustoreLocation = ballerinaHome + "";
            Optional<GatewayLauncherCmd> optionalInvokedCmd = getInvokedCmd(args);
            optionalInvokedCmd.ifPresent(GatewayLauncherCmd::execute);
            String configPath = "/home/harsha/wso2/apim/repos/gateway-codegen/apis-to-ballerina-generator/src/main/resources/main-config.yaml";
            Config config =
                    ConfigYAMLParser.parse(configPath, Config.class);
            System.setProperty("javax.net.ssl.trustStore", "/home/harsha/wso2/apim/repos/gateway-codegen/apis-to-ballerina-generator/src/main/resources/client-truststore.jks");
            System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
            TokenManagementImpl tokenManagement = new TokenManagementImpl();
            GatewayCmdUtils.setConfig(config);
            tokenManagement.generateClientIdAndSecret(config);
            String accessToken = tokenManagement.generateAccessToken("admin", "admin".toCharArray(),
                    config.getTokenConfig().getClientId(), config.getTokenConfig().getClientSecret().toCharArray());
            System.out.println(accessToken);
            APIService apiService = new APIServiceImpl();
            List<API> apis = apiService.getApis("59fff422-e12c-4814-ac16-33a000d3f486", accessToken);
            CodeGenerator codeGenerator = new CodeGenerator();
            codeGenerator.generate("/home/harsha/Downloads/gentest/gen", apis);

            List<ApplicationThrottlePolicyDTO> applicationPolicies = apiService.getApplicationPolicies(accessToken);
            List<SubscriptionThrottlePolicyDTO> subscriptionPolicies = apiService.getSubscriptionPolicies(accessToken);
            ThrottlePolicyGenerator policyGenerator = new ThrottlePolicyGenerator();
            policyGenerator.generate("/home/harsha/Downloads/gentest/gen", applicationPolicies, subscriptionPolicies);
        } catch (CliLauncherException e) {
            outStream.println(e.getMessages());
            Runtime.getRuntime().exit(1);
        } catch (ConfigParserException e) {
            e.printStackTrace();
        } catch (BallerinaServiceGenException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JCommander addSubCommand(JCommander parentCmd, String commandName, Object commandObject) {
        parentCmd.addCommand(commandName, commandObject);
        return parentCmd.getCommands().get(commandName);
    }

    private static Optional<GatewayLauncherCmd> getInvokedCmd(String... args) {
        try {
            DefaultCmd defaultCmd = new DefaultCmd();
            JCommander cmdParser = new JCommander(defaultCmd);
            defaultCmd.setParentCmdParser(cmdParser);

            HelpCmd helpCmd = new HelpCmd();
            cmdParser.addCommand(GatewayCliCommands.HELP, helpCmd);
            helpCmd.setParentCmdParser(cmdParser);

            SetupCmd setupCmd = new SetupCmd();
            cmdParser.addCommand(GatewayCliCommands.SETUP, setupCmd);
            setupCmd.setParentCmdParser(cmdParser);

            cmdParser.setProgramName("micro-gw");
            cmdParser.parse(args);
            String parsedCmdName = cmdParser.getParsedCommand();

            // User has not specified a command. Therefore returning the main command
            // which simply prints usage information.
            if (parsedCmdName == null) {
                return Optional.of(defaultCmd);
            }

            Map<String, JCommander> commanderMap = cmdParser.getCommands();
            return Optional.of((GatewayLauncherCmd) commanderMap.get(parsedCmdName).getObjects().get(0));

        } catch (MissingCommandException e) {
            String errorMsg = "unknown command '" + e.getUnknownCommand() + "'";
            throw GatewayCmdUtils.createUsageException(errorMsg);

        } catch (ParameterException e) {
            String msg = e.getMessage();
            if (msg == null) {
                throw GatewayCmdUtils.createUsageException("internal error occurred");

            } else if (msg.startsWith(JC_UNKNOWN_OPTION_PREFIX)) {
                String flag = msg.substring(JC_UNKNOWN_OPTION_PREFIX.length());
                throw GatewayCmdUtils.createUsageException("unknown flag '" + flag.trim() + "'");

            } else if (msg.startsWith(JC_EXPECTED_A_VALUE_AFTER_PARAMETER_PREFIX)) {
                String flag = msg.substring(JC_EXPECTED_A_VALUE_AFTER_PARAMETER_PREFIX.length());
                throw GatewayCmdUtils.createUsageException("flag '" + flag.trim() + "' needs an argument");

            } else {
                // Make the first character of the error message lower case
                throw GatewayCmdUtils.createUsageException(GatewayCmdUtils.makeFirstLetterLowerCase(msg));
            }
        }
    }

    private static void printUsageInfo(String commandName) {
        String usageInfo = GatewayLauncherCmd.getCommandUsageInfo(commandName);
        outStream.println(usageInfo);
    }

    private static String getMessageForInternalErrors() {
        String errorMsg;
        try {
            errorMsg = GatewayCmdUtils.readFileAsString("cli-help/internal-error-message.txt");
        } catch (IOException e) {
            errorMsg = "ballerina: internal error occurred";
        }
        return errorMsg;
    }

    /**
     * This class represents the "help" command and it holds arguments and flags specified by the user.
     *
     */
    @Parameters(commandNames = "help", commandDescription = "print usage information")
    private static class HelpCmd implements GatewayLauncherCmd {

        @Parameter(description = "Command name")
        private List<String> helpCommands;

        @Parameter(names = "--java.debug", hidden = true)
        private String javaDebugPort;

        private JCommander parentCmdParser;

        public void execute() {
            if (helpCommands == null) {
                printUsageInfo(GatewayCliCommands.HELP);
                return;

            } else if (helpCommands.size() > 1) {
                throw GatewayCmdUtils.createUsageException("too many arguments given");
            }

            String userCommand = helpCommands.get(0);
            if (parentCmdParser.getCommands().get(userCommand) == null) {
                throw GatewayCmdUtils.createUsageException("unknown help topic `" + userCommand + "`");
            }

            String commandUsageInfo = GatewayLauncherCmd.getCommandUsageInfo(userCommand);
            outStream.println(commandUsageInfo);
        }

        @Override
        public String getName() {
            return GatewayCliCommands.HELP;
        }

        @Override
        public void setParentCmdParser(JCommander parentCmdParser) {
            this.parentCmdParser = parentCmdParser;
        }

    }

    /**
     * This class represents the "help" command and it holds arguments and flags specified by the user.
     *
     */
    @Parameters(commandNames = "setup", commandDescription = "print usage information")
    private static class SetupCmd implements GatewayLauncherCmd {

        @Parameter(description = "Command name")
        private List<String> setupCommands;

        @Parameter(names = "--java.debug", hidden = true)
        private String javaDebugPort;

        @Parameter(names = {"-u", "--user"}, hidden = true)
        private String username;

        @Parameter(names = {"-p", "--password"}, hidden = true)
        private String password;

        @Parameter(names = {"-l", "--label"}, hidden = true)
        private String label;

        @Parameter(names = {"-o", "--overwrite"}, hidden = true)
        private boolean overwrite;

        private JCommander parentCmdParser;

        public void execute() {
            if (setupCommands == null) {
                printUsageInfo(GatewayCliCommands.SETUP);
                return;
            } else if (setupCommands.size() > 1) {
                throw GatewayCmdUtils.createUsageException("too many arguments given");
            }

            Config config = GatewayCmdUtils.getConfig();
            String configuredUser = config.getTokenConfig().getUsername();

            if (StringUtils.isEmpty(configuredUser) && StringUtils.isEmpty(username)) {
                if ((username = promptForTextInput("Enter Username: ")).trim().isEmpty()) {
                    if (username.trim().isEmpty()) {
                        username = promptForTextInput("Username can't be empty; enter secret: ");
                        if (username.trim().isEmpty()) {
                            throw GatewayCmdUtils.createUsageException("Micro gateway setup failed: empty username.");
                        }
                    }
                }
            }

            if (StringUtils.isEmpty(username)) {
                if ((password = promptForTextInput("Enter Password: ")).trim().isEmpty()) {
                    if (StringUtils.isEmpty(username)) {
                        password = promptForTextInput("Password can't be empty; enter secret: ");
                        if (username.trim().isEmpty()) {
                            throw GatewayCmdUtils.createUsageException("Micro gateway setup failed: empty username.");
                        }
                    }
                }
            }

            TokenManagement manager = new TokenManagementImpl();

            String clientId = config.getTokenConfig().getClientId();
            if (StringUtils.isEmpty(clientId)) {
                manager.generateClientIdAndSecret(config);
                clientId = config.getTokenConfig().getClientId();
            }

            String clientSecret = config.getTokenConfig().getClientSecret();
            String accessToken = manager.generateAccessToken(username, password.toCharArray(), clientId, clientSecret.toCharArray());

            APIService service = new APIServiceImpl();
            List<API> apis = service.getApis(label, accessToken);
        }

        @Override
        public String getName() {
            return GatewayCliCommands.HELP;
        }

        @Override
        public void setParentCmdParser(JCommander parentCmdParser) {
            this.parentCmdParser = parentCmdParser;
        }

        private String promptForTextInput(String msg) {
            outStream.println(msg);
            return new String(System.console().readLine());
        }
    }

    /**
     * This class represents the "main" command required by the JCommander.
     *
     */
    private static class DefaultCmd implements GatewayLauncherCmd {

        @Parameter(names = { "--help", "-h", "?" }, hidden = true, description = "for more information")
        private boolean helpFlag;

        @Parameter(names = "--java.debug", hidden = true)
        private String javaDebugPort;

        @Override
        public void execute() {
            if (helpFlag) {
                printUsageInfo(GatewayCliCommands.HELP);
                return;
            }

            printUsageInfo(GatewayCliCommands.DEFAULT);
        }

        @Override
        public String getName() {
            return GatewayCliCommands.DEFAULT;
        }

        @Override
        public void setParentCmdParser(JCommander parentCmdParser) {
        }
    }
}
