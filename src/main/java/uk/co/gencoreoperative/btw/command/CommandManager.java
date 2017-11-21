package uk.co.gencoreoperative.btw.command;

import java.util.Set;

/**
 * The role of the {@link CommandManager} is to orchestrate the {@link AbstractCommand}
 * instances that it is provided.
 *
 * It will be responsible for resolving each command and executing them in order to
 * ensure that all pre-requisites are met.
 *
 * If there is a failure during the execution chain, then the error can halt processing
 * and be presented to the caller.
 */
public class CommandManager {
    public void process(Set<AbstractCommand> commands) throws Exception {
        
    }


}
