/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subsystem1;

import java.io.Serializable;

/**
 *
 * @author Jovan
 */
public class RequestMessage implements Serializable {
    private String operation;
    private String xmlArguments;

    public RequestMessage(String operation, String xmlArguments) {
        this.operation = operation;
        this.xmlArguments = xmlArguments;
    }

    public String getOperation() {
        return operation;
    }

    public String getXmlArguments() {
        return xmlArguments;
    }
}