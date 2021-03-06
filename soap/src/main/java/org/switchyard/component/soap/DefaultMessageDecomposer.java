/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.component.soap;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.switchyard.Message;
import org.switchyard.component.soap.util.SOAPUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The default implementation of MessageDecomposer simply copies the Message body onto SOAP
 * and adds SOAP headers from the Message's context.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class DefaultMessageDecomposer implements MessageDecomposer {

    /**
     * Extract the SOAPMessage from Message.
     * @param message a Message to be converted
     * @return the SOAPMessage
     * @throws SOAPException If the SOAP message could not be created/formatted.
     */
    public SOAPMessage decompose(final Message message) throws SOAPException {
        if (SOAPUtil.SOAP_MESSAGE_FACTORY == null) {
            throw new SOAPException("Failed to instantiate SOAP Message Factory");
        }
        final SOAPMessage response = SOAPUtil.SOAP_MESSAGE_FACTORY.createMessage();
        if (message != null) {
            Object messagePayload = message.getContent();

            if (messagePayload instanceof SOAPMessage) {
                return (SOAPMessage) messagePayload;
            }
            
            final Element input = toElement(messagePayload);

            if (input == null) {
                throw new SOAPException("Null response from service");
            }
            try {
                Node node = response.getSOAPBody().getOwnerDocument().importNode(input, true);
                response.getSOAPBody().appendChild(node);
            } catch (Exception e) {
                throw new SOAPException("Unable to parse SOAP Message", e);
            }
        }
        return response;
    }

    private Element toElement(Object messagePayload) throws SOAPException {
        if (messagePayload == null) {
            // Let the caller deal with null...
            return null;
        }

        if (messagePayload instanceof Element) {
            return (Element) messagePayload;
        } else if (messagePayload instanceof String) {
            try {
                return SOAPUtil.parseAsDom((String) messagePayload).getDocumentElement();
            } catch (Exception e) {
                e.printStackTrace();
                throw new SOAPException("Error parsing SOAP message to DOM Element.", e);
            }
        }

        throw new SOAPException("Unsupported SOAP message payload type '" + messagePayload.getClass().getName() + "'.  Must be a DOM Element, or a String.");
    }
}
