/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */


package org.apache.tools.ant.taskdefs.optional.ssh;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.KeepAliveOutputStream;
import org.apache.tools.ant.util.TeeOutputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Executes a command on a remote machine via ssh.
 * @since     Ant 1.6 (created February 2, 2003)
 */
public class SSHExec extends SSHBase {

    private static final int BUFFER_SIZE = 8192;
    private static final int RETRY_INTERVAL = 500;

    /** the command to execute via ssh */
    private String command = null;

    /** units are milliseconds, default is 0=infinite */
    private long maxwait = 0;

    /** for waiting for the command to finish */
    private Thread thread = null;

    private String outputProperty = null;
    private File outputFile = null;
    private String errorProperty = null;
    private File errorFile = null;
    private String resultProperty;
    private boolean append = false;

    private Resource commandResource = null;

    private static final String TIMEOUT_MESSAGE =
        "Timeout period exceeded, connection dropped.";

    /**
     * Constructor for SSHExecTask.
     */
    public SSHExec() {
        super();
    }

    /**
     * Sets the command to execute on the remote host.
     *
     * @param command  The new command value
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Sets a commandResource from a file
     * @param f the value to use.
     * @since Ant 1.7.1
     */
    public void setCommandResource(String f) {
        this.commandResource = new FileResource(new File(f));
    }

    /**
     * The connection can be dropped after a specified number of
     * milliseconds. This is sometimes useful when a connection may be
     * flaky. Default is 0, which means &quot;wait forever&quot;.
     *
     * @param timeout  The new timeout value in seconds
     */
    public void setTimeout(long timeout) {
        maxwait = timeout;
    }

    /**
     * If used, stores the output of the command to the given file.
     *
     * @param output  The file to write to.
     */
    public void setOutput(File output) {
        outputFile = output;
    }
    
    /**
     * If used, stores the error of the command to the given file.
     *
     * @param error The file to write to.
     */
    public void setError(File error) {
        this.errorFile = error;
    }

    /**
     * Determines if the output is appended to the file given in
     * <code>setOutput</code>. Default is false, that is, overwrite
     * the file.
     *
     * @param append  True to append to an existing file, false to overwrite.
     */
    public void setAppend(boolean append) {
        this.append = append;
    }

    /**
     * If set, the output of the command will be stored in the given property.
     *
     * @param property  The name of the property in which the command output
     *      will be stored.
     */
    public void setOutputproperty(String property) {
        outputProperty = property;
    }

    /**
     * Set the property name whose value should be set to the error of
     * the process.
     *
     * @param property  The name of a property in which the standard error of 
     *      the command should be stored.
     */
    public void setErrorproperty(String property) {
        errorProperty = property;
    }
    
    /**
     * Set the name of the property in which the return code of the
     * command should be stored. Only of interest if failonerror=false.
     *
     * @param resultProperty name of property.
     *
     * @since Ant 1.6
     */
    public void setResultProperty(String resultProperty) {
        this.resultProperty = resultProperty;
    }
    
    /**
     * Execute the command on the remote host.
     *
     * @exception BuildException  Most likely a network error or bad parameter.
     */
    public void execute() throws BuildException {
        if (getHost() == null) {
            throw new BuildException("Host is required.");
        }
        if (getUserInfo().getName() == null) {
            throw new BuildException("Username is required.");
        }
        if (getUserInfo().getKeyfile() == null
            && getUserInfo().getPassword() == null) {
            throw new BuildException("Password or Keyfile is required.");
        }
        if (command == null && commandResource == null) {
            throw new BuildException("Command or commandResource is required.");
        }

        Session session = null;

        try {
            session = openSession();
            /* called once */
            if (command != null) {
                log("cmd : " + command, Project.MSG_INFO);
                ExecResponse response = executeCommand(session, command);
                if (outputProperty != null) {
                    getProject().setNewProperty(outputProperty, response.getOutput());
                }
                if (errorProperty != null) {
                    getProject().setNewProperty(outputProperty, response.getError());
                }
                if (resultProperty != null) {
                   getProject().setNewProperty(resultProperty, response.getCode()+"");
                }
            } else { // read command resource and execute for each command
                try {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(commandResource.getInputStream()));
                    String cmd;
                    StringBuilder output = new StringBuilder();
                    StringBuilder error = new StringBuilder();
                    int lastCode = -1;
                    while ((cmd = br.readLine()) != null) {
                        log("cmd : " + cmd, Project.MSG_INFO);
                        ExecResponse response = executeCommand(session, cmd);
                        output.append(response.getOutput());
                        error.append(response.getError());
                        lastCode = response.getCode();
                    }
                    if (outputProperty != null) {
                       getProject().setNewProperty(outputProperty, output.toString());
                    }
                    if (errorProperty != null) {
                       getProject().setNewProperty(outputProperty, error.toString());
                    }
                    if (resultProperty != null) {
                       getProject().setNewProperty(resultProperty, lastCode+"");
                    }
                    FileUtils.close(br);
                } catch (IOException e) {
                    throw new BuildException(e);
                }
            }
        } catch (JSchException e) {
            throw new BuildException(e);
        } finally {
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    private static class ExecResponse {
       
       private final String output;
       private final String error;
       private final int code;
       
       public ExecResponse(String output, String error, int code) {
          this.output = output;
          this.error = error;
          this.code = code;
       }

       public String getError() {
          return error;
       }

       public String getOutput() {
          return output;
       }
       
       public int getCode() {
          return code;
       }

    }
    
    private ExecResponse executeCommand(Session session, String cmd)
        throws BuildException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        TeeOutputStream tee = new TeeOutputStream(out, new KeepAliveOutputStream(System.out));
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        TeeOutputStream teeErr = new TeeOutputStream(err, new KeepAliveOutputStream(System.err));
        int ec = -1;
        
        try {
            final ChannelExec channel;
            session.setTimeout((int) maxwait);
            /* execute the command */
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(cmd);
            channel.setOutputStream(tee);
            channel.setExtOutputStream(tee);
            channel.setErrStream(teeErr);
            channel.connect();
            // wait for it to finish
            thread =
                new Thread() {
                    public void run() {
                        while (!channel.isClosed()) {
                            if (thread == null) {
                                return;
                            }
                            try {
                                sleep(RETRY_INTERVAL);
                            } catch (Exception e) {
                                // ignored
                            }
                        }
                    }
                };

            thread.start();
            thread.join(maxwait);

            if (thread.isAlive()) {
                // ran out of time
                thread = null;
                if (getFailonerror()) {
                    throw new BuildException(TIMEOUT_MESSAGE);
                } else {
                    log(TIMEOUT_MESSAGE, Project.MSG_ERR);
                }
            } else {
                //success
                if (outputFile != null) {
                    writeToFile(out.toString(), append, outputFile);
                }
                if (errorFile != null) {
                    writeToFile(err.toString(), append, errorFile);
                }
                // this is the wrong test if the remote OS is OpenVMS,
                // but there doesn't seem to be a way to detect it.
                ec = channel.getExitStatus();
                if (ec != 0) {
                    String msg = "Remote command failed with exit status " + ec;
                    if (getFailonerror()) {
                        throw new BuildException(msg);
                    } else {
                        log(msg, Project.MSG_ERR);
                    }
                }
            }
        } catch (BuildException e) {
            throw e;
        } catch (JSchException e) {
            if (e.getMessage().indexOf("session is down") >= 0) {
                if (getFailonerror()) {
                    throw new BuildException(TIMEOUT_MESSAGE, e);
                } else {
                    log(TIMEOUT_MESSAGE, Project.MSG_ERR);
                }
            } else {
                if (getFailonerror()) {
                    throw new BuildException(e);
                } else {
                    log("Caught exception: " + e.getMessage(),
                        Project.MSG_ERR);
                }
            }
        } catch (Exception e) {
            if (getFailonerror()) {
                throw new BuildException(e);
            } else {
                log("Caught exception: " + e.getMessage(), Project.MSG_ERR);
            }
        }
        return new ExecResponse(out.toString(), err.toString(), ec);
    }

    /**
     * Writes a string to a file. If destination file exists, it may be
     * overwritten depending on the "append" value.
     *
     * @param from           string to write
     * @param to             file to write to
     * @param append         if true, append to existing file, else overwrite
     * @exception Exception  most likely an IOException
     */
    private void writeToFile(String from, boolean append, File to)
        throws IOException {
        FileWriter out = null;
        try {
            out = new FileWriter(to.getAbsolutePath(), append);
            StringReader in = new StringReader(from);
            char[] buffer = new char[BUFFER_SIZE];
            int bytesRead;
            while (true) {
                bytesRead = in.read(buffer);
                if (bytesRead == -1) {
                    break;
                }
                out.write(buffer, 0, bytesRead);
            }
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}