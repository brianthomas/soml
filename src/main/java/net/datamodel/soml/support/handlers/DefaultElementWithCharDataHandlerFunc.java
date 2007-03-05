// CVS $Id$

// DefaultElementWithCharDataHandlerFunc.java Copyright (c) 2004 Brian Thomas. All rights reserved.
 
/* LICENSE

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 2.1 of the License, or (at your option) any later version.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA

*/

/* AUTHOR

   Brian Thomas  (baba-luu@earthlink.net)

*/


package net.datamodel.soml.support.handlers;

// import SOML stuff
import net.datamodel.soml.support.CharDataHandlerAction;
import net.datamodel.soml.support.SOMLDocumentHandler;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

// default character data handler for Elements which 
// contain either PCData or CDATASections.
public class DefaultElementWithCharDataHandlerFunc implements CharDataHandlerAction 
{
	
   	   private static final Logger logger = Logger.getLogger(DefaultElementWithCharDataHandlerFunc.class);
   	   
       public void action (SOMLDocumentHandler handler, char buf [], int offset, int len)
       throws SAXException
       {

             String data = new String(buf,offset,len);
             // do nothing with other character data
             logger.debug("  DefaultElementWithCharDataHandler called for :["+data+"]");

             Node currentNode = handler.getCurrentNode();
             Text text = null;

             if (handler.isReadingCDATASection()) 
                 text = (Text) handler.getDocument().createCDATASection(data);
             else 
                 text = handler.getDocument().createTextNode(data);

             currentNode.appendChild(text);

       }
}

