/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subsystem2;

/**
 *
 * @author Jovan
 */

import java.io.Serializable;

public class RequestMessage implements Serializable{
    private String operation;
    private String[] arguments;

    public RequestMessage(String operation, String[] arguments) {
        this.operation = operation;
        this.arguments = arguments;
    }

    public String getOperation() {
        return operation;
    }

    public String[] getArguments() {
        return arguments;
    }
}
