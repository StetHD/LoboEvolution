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
/*
 * Created on Feb 12, 2006
 */
package org.loboevolution.html.domimpl;


import org.loboevolution.html.HtmlAttributeProperties;
import org.loboevolution.html.renderstate.ListRenderState;
import org.loboevolution.html.renderstate.RenderState;
import org.loboevolution.html.style.HtmlValues;
import org.loboevolution.w3c.html.HTMLOListElement;

/**
 * The Class HTMLOListElementImpl.
 */
public class HTMLOListElementImpl extends HTMLAbstractUIElement implements HTMLOListElement {

	/**
	 * Instantiates a new HTMLO list element impl.
	 *
	 * @param name
	 *            the name
	 */
	public HTMLOListElementImpl(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLOListElement#getCompact()
	 */
	@Override
	public boolean getCompact() {
		String compactText = this.getAttribute(COMPACT);
		return COMPACT.equalsIgnoreCase(compactText);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLOListElement#setCompact(boolean)
	 */
	@Override
	public void setCompact(boolean compact) {
		this.setAttribute(COMPACT, compact ? COMPACT : null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLOListElement#getStart()
	 */
	@Override
	public int getStart() {
		String startText = this.getAttribute(HtmlAttributeProperties.START);
		return HtmlValues.getPixelSize(startText, this.getRenderState(), 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLOListElement#setStart(int)
	 */
	@Override
	public void setStart(int start) {
		this.setAttribute(HtmlAttributeProperties.START, String.valueOf(start));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLOListElement#getType()
	 */
	@Override
	public String getType() {
		return this.getAttribute(TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLOListElement#setType(java.lang.String)
	 */
	@Override
	public void setType(String type) {
		this.setAttribute(TYPE, type);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.html.domimpl.HTMLElementImpl#createRenderState(org.
	 * loboevolution .html.renderstate.RenderState)
	 */
	@Override
	protected RenderState createRenderState(RenderState prevRenderState) {
		return new ListRenderState(prevRenderState, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLOListElement#getReversed()
	 */
	@Override
	public boolean getReversed() {
		String reversed = this.getAttribute(RESERVED);
		return RESERVED.equalsIgnoreCase(reversed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.loboevolution.w3c.html.HTMLOListElement#setReversed(boolean)
	 */
	@Override
	public void setReversed(boolean reversed) {
		this.setAttribute(RESERVED, reversed ? RESERVED : null);

	}
}
