/*
    GNU GENERAL LICENSE
    Copyright (C) 2014 - 2018 Lobo Evolution

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    verion 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General License for more details.

    You should have received a copy of the GNU General Public
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    

    Contact info: ivan.difrancesco@yahoo.it
 */
package org.loboevolution.html.domimpl;


import org.loboevolution.w3c.html.HTMLElement;
import org.loboevolution.w3c.html.HTMLFormElement;
import org.loboevolution.w3c.html.HTMLLabelElement;
import org.w3c.dom.Node;

public class HTMLLabelElementImpl extends HTMLAbstractUIElement implements HTMLLabelElement {

	public HTMLLabelElementImpl(String name) {
		super(name);
	}

	@Override
	public HTMLFormElement getForm() {
		Node parent = this.getParentNode();
		while (parent != null && !(parent instanceof HTMLFormElement)) {
			parent = parent.getParentNode();
		}
		return (HTMLFormElement) parent;
	}

	@Override
	public String getHtmlFor() {
		return this.getAttribute(FOR);
	}

	@Override
	public void setHtmlFor(String htmlFor) {
		this.setAttribute(FOR, htmlFor);

	}

	@Override
	public HTMLElement getControl() {
		// TODO Auto-generated method stub
		return null;
	}
}
